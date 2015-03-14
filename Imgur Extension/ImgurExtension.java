import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Map;

import stencyl.core.lib.*;
import stencyl.sw.ext.*;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.debug.Debug;
import pulpcore.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ImgurExtension extends BaseExtension
{
	BufferedImage image;
	String IMGUR_CLIENT_ID = "notarealvalue"; //Get this from api.imgur.com for your own version of the 'app'
	String IMGUR_CLIENT_SECRET = "alsonotarealvalue"; //Get this from api.imgur.com for your own version of the 'app'
	String IMGUR_ACCESS_TOKEN; //temporary access for 1 hour on uploads
	String IMGUR_REFRESH_TOKEN; //permits us to re-access account later
	long expirationTime; //tells us when the access token expires
	Boolean didUserAuth; //if this is not true, we can only do anonymous
	String userImgurPIN; //provided by the user during the auth process
		
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	public void onStartup()
	{
		super.onStartup();

		System.out.println("ImgurExtension : Started StencylWorks");
		
		name = "Imgur Extension";
		description = "Upload toolset screenshots directly to Imgur.";
		
		isInMenu = true;
		menuName = "Imgur Extension";
		
		isInGameCenter = true;
		gameCenterName = "Imgur";
		IMGUR_REFRESH_TOKEN = (String)properties.get("refresh");
		IMGUR_ACCESS_TOKEN = (String)properties.get("access");
		didUserAuth = Boolean.parseBoolean((String)properties.get("authed"));
		expirationTime = Long.parseLong(properties.get("expiration").toString());
	}
	
	/*
	 * Happens when the extension is told to display.
	 * 
	 * May happen multiple times during the course of the app. 
	 * 
	 * A good way to handle this is to make your extension a singleton.
	 */
	public void onActivate()
	{
		System.out.println("ImgurExtension : Activated");
	}
	
	public JPanel onGameCenterActivate()
	{
		return onOptions();
	}
	
	/*
	 * Happens when StencylWorks closes.
	 *  
	 * Usually used to save things out.
	 */
	public void onDestroy()
	{
		System.out.println("ImgurExtension : Destroyed");
	}
	
	/*
	 * Happens when a game is saved.
	 */
	public void onGameSave(Game game)
	{
		System.out.println("ImgurExtension : Saved");
	}
	
	/*
	 * Happens when a game is opened.
	 */
	public void onGameOpened(Game game)
	{
		System.out.println("ImgurExtension : Opened");
	}

	/*
	 * Happens when a game is closed.
	 */
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);
		
		System.out.println("ImgurExtension : Closed");
	}
	
	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	public OptionsPanel onOptions()
	{
		System.out.println("ImgurExtension : Options");
		Debug.log("Imgur Extension: Options");
		
		return new OptionsPanel()
		{
			//JTextField text;
			JCheckBox check;
			//JComboBox dropdown;
			JButton authButton;
			JTextField pin;
			JPanel pinPanel;
			JButton pasteButton;
			JButton pinButton;
			JLabel status;
			URL authURL;
			JButton deauthButton;
			JButton printAuthButton;
						
			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without
			 * knowing Swing (Java's GUI library).
			 */
			public void init()
			{
				Debug.log("Init starting.");
				try {
					authURL = new URL("https://api.imgur.com/oauth2/authorize?client_id=" + IMGUR_CLIENT_ID + "&response_type=pin&state=state");
				} catch (MalformedURLException e1) {
					Debug.log("Forming URL for Auth failed: " + e1.getMessage());
				}
				startForm();
				//Debug.log("Building Options Panel");
				/*** AUTHORIZATION header of Options Panel ***/
				addHeader("Authorization");
				JButton authButton = new JButton("Open Browser");
					authButton.setBackground(null);
					authButton.setOpaque(false);
					authButton.setActionCommand("authorize");
					authButton.addActionListener(
							new ActionListener() {
							public void actionPerformed(ActionEvent e)
							{
								if (e.getActionCommand().equals("authorize")) //the "Open Browser" button was pressed
								{
									Debug.log("Imgur Extension: Opening browser...");
									openWebpage(authURL);
								}
							}
							});
				addGenericRow("Authorize this extension to access your Imgur account:", authButton);
				pin = addTextfield("Imgur-supplied auth PIN");
				pinPanel = new JPanel();
				pinPanel.setLayout(new GridLayout(1,0));
				JButton pasteButton = new JButton("Paste from Clipboard");
					pasteButton.setBackground(null);
					pasteButton.setOpaque(false);
					pasteButton.setActionCommand("paste");
					pasteButton.addActionListener(
							new ActionListener() {
								public void actionPerformed(ActionEvent e)
								{
									if (e.getActionCommand().equals("paste"))
									{
										Debug.log("Pasting from clipboard to PIN field...");
										pin.setText(getClipboardContents());
										repaint();
									}
								}
							});
				JButton pinButton = new JButton("Submit PIN");
					pinButton.setBackground(null);
					pinButton.setOpaque(false);
					pinButton.setActionCommand("pinsubmit");
					pinButton.addActionListener(
							new ActionListener() {
								public void actionPerformed(ActionEvent e)
								{
									if (e.getActionCommand().equals("pinsubmit")) //submit pin was pressed
									{
										Debug.log("Imgur Extension: Trading PIN for tokens...");
										try {
											pinToTokens(pin.getText());
											didUserAuth = true;
											properties.put("authed", didUserAuth);
											properties.put("access", IMGUR_ACCESS_TOKEN);
											properties.put("refresh", IMGUR_REFRESH_TOKEN);
											status.setText("Authorization credentials acquired: " + didUserAuth.toString());
											status.setForeground(Color.green);
											
										} catch (Exception e1) {
											e1.printStackTrace();
											Debug.log("Exception: Submit PIN: " + e1.getMessage());
										}
									}
								}
							});
				pinPanel.add(pasteButton);
				pinPanel.add(pinButton);
				pinPanel.setBackground(null);
				pinPanel.setOpaque(false);
				addGenericRow("Use PIN to finalize authorization:", pinPanel);
				status = new JLabel("Authorization credentials acquired: " + didUserAuth.toString());
					status.setBackground(null);
					if(didUserAuth && (IMGUR_ACCESS_TOKEN != null) && (IMGUR_REFRESH_TOKEN != null))
						{
							status.setForeground(Color.green);
							status.setText("Authorization credentials acquired: " + didUserAuth.toString());
						}
					else { status.setForeground(Color.red); status.setText("Authorization credentials acquired: " + didUserAuth.toString());}
					status.setOpaque(false);
				addGenericRow("",status);
				JButton deauthButton = new JButton("Deauthorize");
				deauthButton.setBackground(null);
				deauthButton.setOpaque(false);
				deauthButton.setActionCommand("deauth");
				deauthButton.addActionListener(
						new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							if (e.getActionCommand().equals("deauth")) //the "deauthorize" button was pressed
							{
								Debug.log("Imgur Extension: Deleting authorization information.");
								didUserAuth = false;
								properties.put("authed", didUserAuth);
								status.setText("Authorization credentials acquired: " + didUserAuth.toString());
								status.setForeground(Color.red);
								IMGUR_ACCESS_TOKEN = "";
								IMGUR_REFRESH_TOKEN = "";
								expirationTime = 0;
							}
						}
						});
				addGenericRow("Delete saved authorization information", deauthButton);
				JButton printAuthButton = new JButton("Print");
				printAuthButton.setBackground(null);
				printAuthButton.setOpaque(false);
				printAuthButton.setActionCommand("printauth");
				printAuthButton.addActionListener(
						new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							if (e.getActionCommand().equals("printauth")) //the "print auth" button was pressed
							{
								Debug.log("Imgur Extension: Printing authorization information.");
								Debug.log("* Access Token: " + IMGUR_ACCESS_TOKEN);
								Debug.log("* Refresh Token: " + IMGUR_REFRESH_TOKEN);
								Debug.log("* Expiration date: " + expirationTime);
							}
						}
						});
				addGenericRow("Print authorization credentials to Log Viewer", printAuthButton);
				/*** OPTIONS header of Options Panel ***/
				addHeader("Options");
				//text = addTextfield("Name:");
				check = addCheckbox("Save Image to Disk?:");
				//dropdown = addDropdown("Where are you from?", new String[] {"Americas", "Europe", "Asia", "Other"});
				
				//Debug.log("Options Panel built.");
				endForm();
								
				//Set the form's values
				check.setSelected(Boolean.parseBoolean("" + properties.get("save")));
			}

			 /**
			  * Get the String residing on the clipboard.
			  *
			  * @return any text found on the Clipboard; if none found, return an
			  * empty String.
			  */
			  public String getClipboardContents() {
				  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				   Transferable clipData = clipboard.getContents(clipboard);
				   if (clipData != null) {
				     try {
				       if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				         String s = (String)(clipData.getTransferData(
				           DataFlavor.stringFlavor));
				         return s;
				       }
				     } catch (UnsupportedFlavorException ufe) {
				       Debug.log("Imgur Extension: Paste Flavor unsupported: " + ufe);
				       return "";
				     } catch (IOException ioe) {
				       System.err.println("Imgur Extension: Paste Data not available: " + ioe);
				       return "";
				     }
				   }
				   return "";
			  }

			  private void pinToTokens(String pin) throws Exception  //make a request to trade the PIN for access and refresh tokens
			  {
				  String str = "";
				  String IMGUR_POST_URI = "https://api.imgur.com/oauth2/token"; //Can't get XML response?!

			        	 Debug.log("Connecting and sending Imgur PIN...");
			        	 URL pinURL = new URL(IMGUR_POST_URI); 
			        	 //Debug.log("URL made, building connection");
			             URLConnection conn = pinURL.openConnection();
			             conn.setDoOutput(true); 
			             conn.setDoInput(true);
			             conn.setUseCaches(false);
			             conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			             //Debug.log("pinToTokens: attempting connection");
			             conn.connect();

			             String data;
						try {							
							//Debug.log("pinToTokens: encoding URL");
							data = URLEncoder.encode("client_id", "UTF-8") + "=" + URLEncoder.encode(IMGUR_CLIENT_ID, "UTF-8")
							 + "&" + URLEncoder.encode("client_secret", "UTF-8") + "=" + URLEncoder.encode(IMGUR_CLIENT_SECRET, "UTF-8")
							 + "&" + URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("pin", "UTF-8")
							 + "&" + URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8");
						} catch (Exception e) {
							e.printStackTrace();
							Debug.log("pinToTokens: Encoding error: " + e.getMessage());
							data = "Encoding failed!";
						}
			             //Debug.log(data);
			             OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			             wr.write(data);
			             wr.flush();

			             Debug.log("Finished sending...");
			             //capture the XML response as a raw string
			             BufferedReader in = new BufferedReader (new InputStreamReader (conn.getInputStream()) );
			             String line;
			             while ( (line = in.readLine()) != null      )
			             {
			                System.out.println(line);
			                str = new String(line);  //copy the response before we lose our temporary sting "line" 
			             }
			             in.close();
			             //Debug.log(str);
			             IMGUR_ACCESS_TOKEN = str.substring(17,57); //JSON parsing always fails, so just get the stupid token substring from the string myself. Total hack :P
			             IMGUR_REFRESH_TOKEN = str.substring(129,169); //ditto
			             properties.put("access", IMGUR_ACCESS_TOKEN);
			             properties.put("refresh", IMGUR_REFRESH_TOKEN);
			             Calendar calendar = Calendar.getInstance();
			             expirationTime = (calendar.getTimeInMillis() + 3600000); //add 1 hour to current time for expiration of the access token
			             properties.put("expiration", expirationTime);
			         	 //Debug.log("Expiration: " + calendar.getTimeInMillis() + " | " + expirationTime);

			  }
				
				/*
				 * Use this to save the form data out.
				 * All you need to do is place the properties into preferences.
				 */
			public void onPressedOK()
			{
				//properties.put("name", text.getText());
				properties.put("save", check.isSelected());
				//properties.put("loc", dropdown.getSelectedItem());
				properties.put("authed", didUserAuth);
				properties.put("access", IMGUR_ACCESS_TOKEN);
				properties.put("refresh", IMGUR_REFRESH_TOKEN);
				properties.put("expiration", expirationTime);
				//Debug.log("Closing panel, refresh token saved as: " + properties.get("refresh"));
			}
			
			/*
			 * Happens whenever the user presses cancel or clicks the "x" in the corner
			 */
			public void onPressedCancel()
			{
				System.out.println("ImgurExtension : OptionsPanel : onPressedCancel");
			}
			
			/*
			 * Happens whenever the user brings this options panel up
			 */
			public void onShown()
			{
				System.out.println("ImgurExtension : OptionsPanel : onShown");
			}
		};
	}
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	        	//Debug.log("openWebpage(URI)");
	            desktop.browse(uri);
	        } catch (Exception e) {
	            Debug.log("openWebpage(URI): " + e.getMessage());
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	    	//Debug.log("openWebpage(URL)");
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        Debug.log("openWebpage(URL): " + e.getMessage());
	    }
	}
	/*
	 * Happens when a screen capture is triggered.
	 */
	public void onScreenCapture(BufferedImage img) //TODO: separate Yes/No/Maybe save decision to a separate callback
	{
		Debug.log("ImgurExtension : onScreenCapture");
		image = img;

		saveFrame myFrame = new saveFrame();
		myFrame.setSize(300,400);
		myFrame.setVisible(true);
	}
	public Integer getImageSaveRequest()
	{
		Debug.log("ImgurExtension : getImageSaveRequest");
		Boolean saveToDisk;
		saveToDisk = Boolean.parseBoolean(properties.get("save").toString());
		if(saveToDisk)
		{
			return 1;
		}
		else return -1;
	}
	/*
	 * Happens when the extension is first installed.
	 */
	public void onInstall()
	{
		System.out.println("ImgurExtension : Install");
	}
	
	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	public void onUninstall()
	{
		System.out.println("ImgurExtension : Uninstall");
	}
	
	/*
	 * For quick testing. We recommend testing inside StencylWorks.
	 * 
	 * 1) Run the ANT script, which will package and copy the plugin over for you.
	 * 2) Launch Stencylworks.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					ImgurExtension e = new ImgurExtension();
					e.onStartup();
					e.onActivate();
				}
			}
		);
	}

	class saveFrame extends JFrame implements ActionListener
	{
		JLabel urlLabel;
		JTextField urlTextField;
		JButton sendButton;
		JButton anonButton;
		JButton copyButton;
		JPanel urlPanel;
		JPanel textPanel;
		JLabel statusLabel;
		//JButton refreshButton;
				
		saveFrame()
		{
			super("Imgur Extension");
			//Debug.log("Building the JFrame for the extension.");
			JPanel urlPanel = new JPanel();
			urlLabel = new JLabel("Image URL:");
			urlTextField = new JTextField("You must upload your image first to get the URL.", 60);
			urlTextField.setEditable(false);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(0,1));
			anonButton = new JButton("Send to Imgur anonymously");
			anonButton.setActionCommand("anon");
			sendButton = new JButton("Send to Imgur account");
			if (didUserAuth == false) { sendButton.setEnabled(false); }
			sendButton.setActionCommand("send");
			copyButton = new JButton("Copy URL to Clipboard");
			copyButton.setEnabled(false);
			copyButton.setActionCommand("copy");
			statusLabel = new JLabel("Ready to upload");
			//refreshButton = new JButton("Refresh Token");
			//refreshButton.setActionCommand("refresh");
			//refreshButton.setEnabled(true);
			
			setLayout(new GridLayout(0,1));
			add(urlLabel);
			add(urlTextField);
			buttonPanel.add(anonButton);
			buttonPanel.add(sendButton); //TODO: disable this when not authed already
			buttonPanel.add(copyButton);
			//buttonPanel.add(refreshButton);
			//add(urlPanel);
			add(buttonPanel);
			add(statusLabel);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			anonButton.addActionListener(this);
			sendButton.addActionListener(this);
			copyButton.addActionListener(this);
			//refreshButton.addActionListener(this);
			//Debug.log("Building done.");
			
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("refresh"))
			{
				try {
					refreshToken();
				} catch (Exception e1) {
					Debug.log("Exception: actionPerformed: refreshToken: " + e1.getMessage());
				}
			}
			if (e.getActionCommand().equals("anon")) //the "send anonymously" button was pressed
			{
				String strTemp;
				//Debug.log("actionPerformed: sendToImgur happening");
				strTemp = sendToImgur(true); //send the image and get URL back
				anonButton.setBackground(Color.yellow);
				urlTextField.setText(strTemp);
				anonButton.setEnabled(false);
				sendButton.setEnabled(false);
				copyButton.setEnabled(true);
			    repaint();
				
			}
			if (e.getActionCommand().equals("send")) //The "send as user" button was pressed
			{
				String strTemp;
				//Debug.log("actionPerformed: sendToImgur happening");
				strTemp = sendToImgur(false); //send the image and get the URL back...
				sendButton.setBackground( Color.yellow );
				urlTextField.setText(strTemp);
				anonButton.setEnabled(false);
				sendButton.setEnabled(false);
				copyButton.setEnabled(true);
			    repaint();
			}
			if (e.getActionCommand().equals("copy")) //simple copy-to-clipboard code for the URL string
			{
				StringSelection stringSelection = new StringSelection (urlTextField.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				
				copyButton.setText("Copied!");
				copyButton.setBackground(Color.yellow);
				repaint();
			}
			
		}

		private String sendToImgur(Boolean anon)
		{
			 String IMGUR_POST_URI = "https://api.imgur.com/3/image.xml"; //use the .xml URL so we get an XML response
	         String strURL = new String("http://www.stencyl.com"); //temporary URL just so there is one
	         String str = new String("");
	         String encodedImg = new String("");
	         
	         try
	         {
	        	 //Debug.log("Opening BAOS");
	        	 ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	        	 //Debug.log("Writing image...");
	             ImageIO.write(image, "PNG", baos);
	             byte[] bytes = baos.toByteArray();

	             Debug.log("Encoding image..."); 
	             
	             encodedImg = Base64.encode(bytes);
	             URL url = new URL(IMGUR_POST_URI);
	             String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encodedImg, "UTF-8");

	             Debug.log("Connecting to Imgur...");
	             URLConnection conn = url.openConnection();
	             conn.setDoOutput(true);
	             conn.setDoInput(true);
	             if(anon)  //anonymous upload requested, set headers to not include any user data
	             {
	            	 conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
	             }
	             else  //user upload using existing tokens
	             {
	            	 Calendar cal = Calendar.getInstance();
	            	 if (expirationTime <= cal.getTimeInMillis())
	            	 {
	            		 refreshToken();
	            	 }
	            	 conn.setRequestProperty("Authorization", "Bearer " + IMGUR_ACCESS_TOKEN);
	             }
	             OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

	             Debug.log("Sending data...");
	             wr.write(data);
	             wr.flush();

	             statusLabel.setText("Finished.");
	             Debug.log("Finished upload.");
	             repaint();

	             //capture the XML response as a raw string
	             BufferedReader in = new BufferedReader (new InputStreamReader (conn.getInputStream()) );
	             String line;
	             while ( (line = in.readLine()) != null      )
	             {
	                System.out.println(line);
	                strURL = new String(line);  //copy the XML response before we lose our temporary sting "line" 
	             }
	             in.close();
	         }
	         catch(Exception e)
	         {
	             statusLabel.setText("Error:sendToImgur: " + e.getMessage() );
	             Debug.log("Exception: sendToImgur: " + e.getMessage());
	             repaint();
	             e.printStackTrace();
	         }
			str = getElement(strURL, "link");  //parse the XML and extract the image's URL on Imgur
			return str;  //give the URL back to the rest of the program
		}
		private void refreshToken() throws Exception
		{
			 String str = "";
			  String IMGUR_POST_URI = "https://api.imgur.com/oauth2/token"; //Can't get XML response?!

		        	 Debug.log("Connecting to update access token...");
		        	 URL pinURL = new URL(IMGUR_POST_URI); 
	        	 	//Debug.log("refreshToken: New URL made");
		             URLConnection conn = pinURL.openConnection();
		             conn.setDoOutput(true); 
		             conn.setDoInput(true);
		             conn.setUseCaches(false);
		             conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		             conn.connect();
		            // Debug.log("refreshToken: Connected.");
		             String data;
					try {
						//Debug.log("refreshToken: encoding URL");
						data = URLEncoder.encode("refresh_token", "UTF-8") + "=" + URLEncoder.encode(IMGUR_REFRESH_TOKEN, "UTF-8")
						 + "&" + URLEncoder.encode("client_id", "UTF-8") + "=" + URLEncoder.encode(IMGUR_CLIENT_ID, "UTF-8")
						 + "&" + URLEncoder.encode("client_secret", "UTF-8") + "=" + URLEncoder.encode(IMGUR_CLIENT_SECRET, "UTF-8")
						 + "&" + URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("refresh_token", "UTF-8");
					} catch (Exception e) {
						e.printStackTrace();
						Debug.log("refreshToken: Encoding error: " + e.getMessage());
						data = "Encoding failed!";
					}
		             //Debug.log(data);
		             OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		             wr.write(data);
		             wr.flush();

		             Debug.log("Finished sending...");
		             //capture the XML response as a raw string
		             BufferedReader in = new BufferedReader (new InputStreamReader (conn.getInputStream()) );
		             String line;
		             while ( (line = in.readLine()) != null      )
		             {
		                System.out.println(line);
		                str = new String(line);  //copy the response before we lose our temporary sting "line" 
		             }
		             in.close();
		            // Debug.log(str);
		             IMGUR_ACCESS_TOKEN = str.substring(17,57); //JSON parsing always fails, so just get the stupid token substring from the string myself. THIS IS A TOTAL HACK AND NOT RECOMMENDED
		             properties.put("access", IMGUR_ACCESS_TOKEN);
		             Calendar calendar = Calendar.getInstance();
		             expirationTime = (calendar.getTimeInMillis() + 3600000); //add 1 hour to current time for expiration of the access token
		             properties.put("expiration", expirationTime);
		         	// Debug.log("Expiration: " + calendar.getTimeInMillis() + " | " + expirationTime);
		}

		private String getElement(String xml, String element) //returns a link text from a Strong containing XML data
		{								   //the smart thing to do would be to make this "getWhatever(String xmlString, String property)"
			Document doc;
			Element elem;
			String returnElem = "No element.";  //just in case the parsing fails to produce a URL
			try {
				Debug.log("getElement: parsing XML");
				doc = loadXMLFromString(xml); //parse the string into an Document
				elem = doc.getDocumentElement(); //get the root element
				NodeList nl = elem.getChildNodes(); //get the children of the root element
				Node an;
				for (int i=0; i < nl.getLength(); i++) //Check them all; TODO: replace with a simpler function to call a specific node name rather than loop?
				{
				    an = nl.item(i);
				    //Debug.log(an.toString());
				    if(an.getNodeName() == element)
				    {
				        returnElem = an.getTextContent();
				    }
				}    
				//Debug.log(returnElem);
				return returnElem;
			} catch (Exception e) {
				Debug.log("Error:getElement: " + e.getMessage());
				return xml; //return whatever came in if it was bad
			}
			
		}
		public Document loadXMLFromString(String xml) throws Exception  //turns an String containing XML into a Document object
		{
			//Debug.log("loadXMLFromString: building document");
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    //Debug.log("loadXMLFromString: returning document");
		    return builder.parse(is);
		}
	}

}
