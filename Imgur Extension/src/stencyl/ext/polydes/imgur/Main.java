package stencyl.ext.polydes.imgur;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pulpcore.util.Base64;
import stencyl.core.lib.Game;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;

public class Main extends BaseExtension
{
	private static final Logger log = Logger.getLogger(Main.class);

	BufferedImage image;
	
	// Get these from api.imgur.com for your own version of the 'app'
	private static final String IMGUR_CLIENT_ID = "notarealvalue";
	private static final String IMGUR_CLIENT_SECRET = "alsonotarealvalue";
	
	// temporary access for 1 hour on uploads
	private static String IMGUR_ACCESS_TOKEN;
	
	// permits us to re-access account later
	private static String IMGUR_REFRESH_TOKEN;
	
	// tells us when the access token expires
	private static long expirationTime;
	
	// if this is not true, we can only do anonymous
	private static Boolean didUserAuth;
	
	// provided by the user during the auth process
//	private static String userImgurPIN;

	/*
	 * Happens when StencylWorks launches.
	 * 
	 * Avoid doing anything time-intensive in here, or it will slow down launch.
	 */
	@Override
	public void onStartup()
	{
		super.onStartup();

		log.info("ImgurExtension : Started StencylWorks");
		
		name = "Imgur Extension";
		description = "Upload toolset screenshots directly to Imgur.";
		
		isInMenu = true;
		menuName = "Imgur Extension";
		
		isInGameCenter = false;
//		gameCenterName = "Imgur";
		
		try
		{
			IMGUR_REFRESH_TOKEN = (String) properties.get("refresh");
			IMGUR_ACCESS_TOKEN = (String) properties.get("access");
			didUserAuth = Boolean.parseBoolean((String) properties.get("authed"));
			expirationTime = Long.parseLong(properties.get("expiration").toString());
		}
		catch(NullPointerException ex)
		{
			log.warn("Couldn't load Imgur extension properties (Unset).");
		}
		catch(NumberFormatException ex)
		{
			log.warn("Couldn't load Imgur extension properties (Bad number format).");
		}
	}

	/*
	 * Happens when the extension is told to display.
	 * 
	 * May happen multiple times during the course of the app.
	 * 
	 * A good way to handle this is to make your extension a singleton.
	 */
	@Override
	public void onActivate()
	{
		log.info("ImgurExtension : Activated");
	}

	@Override
	public JPanel onGameCenterActivate()
	{
		return onOptions();
	}

	/*
	 * Happens when StencylWorks closes.
	 * 
	 * Usually used to save things out.
	 */
	@Override
	public void onDestroy()
	{
		log.info("ImgurExtension : Destroyed");
	}

	/*
	 * Happens when a game is saved.
	 */
	@Override
	public void onGameSave(Game game)
	{
		log.info("ImgurExtension : Saved");
	}

	/*
	 * Happens when a game is opened.
	 */
	@Override
	public void onGameOpened(Game game)
	{
		log.info("ImgurExtension : Opened");
	}

	/*
	 * Happens when a game is closed.
	 */
	@Override
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);

		log.info("ImgurExtension : Closed");
	}

	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	public OptionsPanel onOptions()
	{
		log.info("Imgur Extension: Options");

		return new OptionsPanel()
		{
//			JTextField text;
			JCheckBox check;
//			JComboBox dropdown;
//			JButton authButton;
			JTextField pin;
			JPanel pinPanel;
//			JButton pasteButton;
//			JButton pinButton;
			JLabel status;
			URL authURL;

//			JButton deauthButton;
//			JButton printAuthButton;

			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without knowing Swing
			 * (Java's GUI library).
			 */
			@Override
			public void init()
			{
				log.info("Init starting.");
				try
				{
					authURL = new URL(
							"https://api.imgur.com/oauth2/authorize?client_id="
									+ IMGUR_CLIENT_ID
									+ "&response_type=pin&state=state");
				}
				catch (MalformedURLException e1)
				{
					log.info("Forming URL for Auth failed: " + e1.getMessage());
				}
				startForm();
//				log.info("Building Options Panel");
				/*** AUTHORIZATION header of Options Panel ***/
				addHeader("Authorization");
				JButton authButton = new JButton("Open Browser");
				authButton.setBackground(null);
				authButton.setOpaque(false);
				authButton.setActionCommand("authorize");
				authButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// the "Open Browser" button was pressed
						if (e.getActionCommand().equals("authorize")) 
						{
							log.info("Imgur Extension: Opening browser...");
							openWebpage(authURL);
						}
					}
				});
				addGenericRow("Authorize this extension to access your Imgur account:", authButton);
				pin = addTextfield("Imgur-supplied auth PIN");
				pinPanel = new JPanel();
				pinPanel.setLayout(new GridLayout(1, 0));
				JButton pasteButton = new JButton("Paste from Clipboard");
				pasteButton.setBackground(null);
				pasteButton.setOpaque(false);
				pasteButton.setActionCommand("paste");
				pasteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (e.getActionCommand().equals("paste"))
						{
							log.info("Pasting from clipboard to PIN field...");
							pin.setText(getClipboardContents());
							repaint();
						}
					}
				});
				JButton pinButton = new JButton("Submit PIN");
				pinButton.setBackground(null);
				pinButton.setOpaque(false);
				pinButton.setActionCommand("pinsubmit");
				pinButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// submit pin was pressed
						if (e.getActionCommand().equals("pinsubmit"))
						{
							log.info("Imgur Extension: Trading PIN for tokens...");
							try
							{
								pinToTokens(pin.getText());
								didUserAuth = true;
								properties.put("authed", didUserAuth);
								properties.put("access", IMGUR_ACCESS_TOKEN);
								properties.put("refresh", IMGUR_REFRESH_TOKEN);
								status.setText("Authorization credentials acquired: " + didUserAuth.toString());
								status.setForeground(Color.green);
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
								log.info("Exception: Submit PIN: " + e1.getMessage());
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
				if (didUserAuth && (IMGUR_ACCESS_TOKEN != null) && (IMGUR_REFRESH_TOKEN != null))
				{
					status.setForeground(Color.green);
					status.setText("Authorization credentials acquired: " + didUserAuth.toString());
				}
				else
				{
					status.setForeground(Color.red);
					status.setText("Authorization credentials acquired: " + didUserAuth.toString());
				}
				status.setOpaque(false);
				addGenericRow("", status);
				JButton deauthButton = new JButton("Deauthorize");
				deauthButton.setBackground(null);
				deauthButton.setOpaque(false);
				deauthButton.setActionCommand("deauth");
				deauthButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// the "deauthorize" button was pressed
						if (e.getActionCommand().equals("deauth"))
						{
							log.info("Imgur Extension: Deleting authorization information.");
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
				printAuthButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// the "print auth" button was pressed
						if (e.getActionCommand().equals("printauth"))
						{
							log.info("Imgur Extension: Printing authorization information.");
							log.info("* Access Token: " + IMGUR_ACCESS_TOKEN);
							log.info("* Refresh Token: " + IMGUR_REFRESH_TOKEN);
							log.info("* Expiration date: " + expirationTime);
						}
					}
				});
				addGenericRow("Print authorization credentials to Log Viewer", printAuthButton);
				/*** OPTIONS header of Options Panel ***/
				addHeader("Options");
				// text = addTextfield("Name:");
				check = addCheckbox("Save Image to Disk?:");
				check.setBackground(null);
				// dropdown = addDropdown("Where are you from?", new String[]
				// {"Americas", "Europe", "Asia", "Other"});

				// log.info("Options Panel built.");
				endForm();

				// Set the form's values
				check.setSelected(Boolean.parseBoolean(""
						+ properties.get("save")));
			}

			/**
			 * Get the String residing on the clipboard.
			 *
			 * @return any text found on the Clipboard; if none found, return an
			 *         empty String.
			 */
			public String getClipboardContents()
			{
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable clipData = clipboard.getContents(clipboard);
				if (clipData != null)
				{
					try
					{
						if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor))
						{
							String s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
							return s;
						}
					}
					catch (UnsupportedFlavorException ufe)
					{
						log.info("Imgur Extension: Paste Flavor unsupported: " + ufe);
						return "";
					}
					catch (IOException ioe)
					{
						System.err.println("Imgur Extension: Paste Data not available: " + ioe);
						return "";
					}
				}
				return "";
			}

			private void pinToTokens(String pin) throws Exception
			{
				// make a request to trade the PIN for access and refresh tokens
				
				String str = "";
				String IMGUR_POST_URI = "https://api.imgur.com/oauth2/token"; // Can't get XML response?!

				log.info("Connecting and sending Imgur PIN...");
				URL pinURL = new URL(IMGUR_POST_URI);
//				log.info("URL made, building connection");
				URLConnection conn = pinURL.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//				log.info("pinToTokens: attempting connection");
				conn.connect();

				String data;
				try
				{
//					log.info("pinToTokens: encoding URL");
					data = URLEncoder.encode("client_id", "UTF-8") + "="
							+ URLEncoder.encode(IMGUR_CLIENT_ID, "UTF-8") + "&"
							+ URLEncoder.encode("client_secret", "UTF-8") + "="
							+ URLEncoder.encode(IMGUR_CLIENT_SECRET, "UTF-8")
							+ "&" + URLEncoder.encode("grant_type", "UTF-8")
							+ "=" + URLEncoder.encode("pin", "UTF-8") + "&"
							+ URLEncoder.encode("pin", "UTF-8") + "="
							+ URLEncoder.encode(pin, "UTF-8");
				}
				catch (Exception e)
				{
					e.printStackTrace();
					log.info("pinToTokens: Encoding error: " + e.getMessage());
					data = "Encoding failed!";
				}
//				log.info(data);
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(data);
				wr.flush();

				log.info("Finished sending...");
				// capture the XML response as a raw string
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null)
				{
					log.info(line);
					// copy the response before we lose our temporary sting "line"
					str = new String(line);
				}
				in.close();
//				log.info(str);
				
				// JSON parsing always fails, so just get the stupid token substring from the string myself. Total hack :P
				IMGUR_ACCESS_TOKEN = str.substring(17, 57); 
				
				// ditto
				IMGUR_REFRESH_TOKEN = str.substring(129, 169);
				
				properties.put("access", IMGUR_ACCESS_TOKEN);
				properties.put("refresh", IMGUR_REFRESH_TOKEN);
				Calendar calendar = Calendar.getInstance();
				
				// add 1 hour to current time for expiration of the access token
				expirationTime = (calendar.getTimeInMillis() + 3600000);
				properties.put("expiration", expirationTime);
				
//				log.info("Expiration: " + calendar.getTimeInMillis() + " | " + expirationTime);

			}

			/*
			 * Use this to save the form data out. All you need to do is place
			 * the properties into preferences.
			 */
			@Override
			public void onPressedOK()
			{
				// properties.put("name", text.getText());
				properties.put("save", check.isSelected());
				// properties.put("loc", dropdown.getSelectedItem());
				properties.put("authed", didUserAuth);
				properties.put("access", IMGUR_ACCESS_TOKEN);
				properties.put("refresh", IMGUR_REFRESH_TOKEN);
				properties.put("expiration", expirationTime);
//				log.info("Closing panel, refresh token saved as: " +
				// properties.get("refresh"));
			}

			/*
			 * Happens whenever the user presses cancel or clicks the "x" in the
			 * corner
			 */
			@Override
			public void onPressedCancel()
			{
				log.info("ImgurExtension : OptionsPanel : onPressedCancel");
			}

			/*
			 * Happens whenever the user brings this options panel up
			 */
			@Override
			public void onShown()
			{
				log.info("ImgurExtension : OptionsPanel : onShown");
			}
		};
	}

	public static void openWebpage(URI uri)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
//				log.info("openWebpage(URI)");
				desktop.browse(uri);
			}
			catch (Exception e)
			{
				log.info("openWebpage(URI): " + e.getMessage());
			}
		}
	}

	public static void openWebpage(URL url)
	{
		try
		{
//			log.info("openWebpage(URL)");
			openWebpage(url.toURI());
		}
		catch (URISyntaxException e)
		{
			log.info("openWebpage(URL): " + e.getMessage());
		}
	}

	/*
	 * Happens when a screen capture is triggered.
	 */
	@Override
	public void onScreenCapture(BufferedImage img) 
	{
		// TODO: separate Yes/No/Maybe save decision to a separate callback
		
		log.info("ImgurExtension : onScreenCapture");
		image = img;

		saveFrame myFrame = new saveFrame();
		myFrame.setSize(300, 400);
		myFrame.setVisible(true);
	}

	@Override
	public Integer getImageSaveRequest()
	{
		log.info("ImgurExtension : getImageSaveRequest");
		Boolean saveToDisk;
		saveToDisk = Boolean.parseBoolean(properties.get("save").toString());
		if (saveToDisk)
		{
			return 1;
		}
		else
			return -1;
	}

	/*
	 * Happens when the extension is first installed.
	 */
	@Override
	public void onInstall()
	{
		log.info("ImgurExtension : Install");
	}

	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	@Override
	public void onUninstall()
	{
		log.info("ImgurExtension : Uninstall");
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

		// JButton refreshButton;

		saveFrame()
		{
			super("Imgur Extension");
//			log.info("Building the JFrame for the extension.");
//			JPanel urlPanel = new JPanel();
			urlLabel = new JLabel("Image URL:");
			urlTextField = new JTextField("You must upload your image first to get the URL.", 60);
			urlTextField.setEditable(false);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(0, 1));
			anonButton = new JButton("Send to Imgur anonymously");
			anonButton.setActionCommand("anon");
			sendButton = new JButton("Send to Imgur account");
			if (didUserAuth == false)
			{
				sendButton.setEnabled(false);
			}
			sendButton.setActionCommand("send");
			copyButton = new JButton("Copy URL to Clipboard");
			copyButton.setEnabled(false);
			copyButton.setActionCommand("copy");
			statusLabel = new JLabel("Ready to upload");
//			refreshButton = new JButton("Refresh Token");
//			refreshButton.setActionCommand("refresh");
//			refreshButton.setEnabled(true);

			setLayout(new GridLayout(0, 1));
			add(urlLabel);
			add(urlTextField);
			buttonPanel.add(anonButton);
			buttonPanel.add(sendButton); // TODO: disable this when not authed already
			buttonPanel.add(copyButton);
//			buttonPanel.add(refreshButton);
//			add(urlPanel);
			add(buttonPanel);
			add(statusLabel);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			anonButton.addActionListener(this);
			sendButton.addActionListener(this);
			copyButton.addActionListener(this);
//			refreshButton.addActionListener(this);
//			log.info("Building done.");

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("refresh"))
			{
				try
				{
					refreshToken();
				}
				catch (Exception e1)
				{
					log.info("Exception: actionPerformed: refreshToken: " + e1.getMessage());
				}
			}
			if (e.getActionCommand().equals("anon")) 
			{
				// the "send anonymously" button was pressed
//				log.info("actionPerformed: sendToImgur happening");
				
				// send the image and get URL back
				String strTemp = sendToImgur(true);
				anonButton.setBackground(Color.yellow);
				urlTextField.setText(strTemp);
				anonButton.setEnabled(false);
				sendButton.setEnabled(false);
				copyButton.setEnabled(true);
				repaint();

			}
			if (e.getActionCommand().equals("send"))
			{
				// The "send as user" button was pressed
//				log.info("actionPerformed: sendToImgur happening");
				
				// send the image and get the URL back...
				String strTemp = sendToImgur(false);
				sendButton.setBackground(Color.yellow);
				urlTextField.setText(strTemp);
				anonButton.setEnabled(false);
				sendButton.setEnabled(false);
				copyButton.setEnabled(true);
				repaint();
			}
			if (e.getActionCommand().equals("copy"))
			{
				// simple copy-to-clipboard code for the URL string
				
				StringSelection stringSelection = new StringSelection(urlTextField.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);

				copyButton.setText("Copied!");
				copyButton.setBackground(Color.yellow);
				repaint();
			}

		}

		private String sendToImgur(Boolean anon)
		{
			// use the .xml URL so we get an XML response
			String IMGUR_POST_URI = "https://api.imgur.com/3/image.xml";
			
			// temporary URL just so there is one
			String strURL = new String("http://www.stencyl.com");
			
			String str = new String("");
			String encodedImg = new String("");

			try
			{
//				log.info("Opening BAOS");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				log.info("Writing image...");
				ImageIO.write(image, "PNG", baos);
				byte[] bytes = baos.toByteArray();

				log.info("Encoding image...");

				encodedImg = Base64.encode(bytes);
				URL url = new URL(IMGUR_POST_URI);
				String data = URLEncoder.encode("image", "UTF-8") + "="
						+ URLEncoder.encode(encodedImg, "UTF-8");

				log.info("Connecting to Imgur...");
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				if (anon)
				{
					// anonymous upload requested, set headers to not include any user data
					
					conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
				}
				else
				{
					// user upload using existing tokens
					
					Calendar cal = Calendar.getInstance();
					if (expirationTime <= cal.getTimeInMillis())
					{
						refreshToken();
					}
					conn.setRequestProperty("Authorization", "Bearer " + IMGUR_ACCESS_TOKEN);
				}
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

				log.info("Sending data...");
				wr.write(data);
				wr.flush();

				statusLabel.setText("Finished.");
				log.info("Finished upload.");
				repaint();

				// capture the XML response as a raw string
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null)
				{
					log.info(line);
					
					// copy the XML response before we lose our temporary sting "line"
					strURL = new String(line);
				}
				in.close();
			}
			catch (Exception e)
			{
				statusLabel.setText("Error:sendToImgur: " + e.getMessage());
				log.info("Exception: sendToImgur: " + e.getMessage());
				repaint();
				e.printStackTrace();
			}
			// parse the XML and extract the image's URL on Imgur
			str = getElement(strURL, "link");
			// give the URL back to the rest of the program
			return str;
		}

		private void refreshToken() throws Exception
		{
			String str = "";
			// Can't get XML response?!
			String IMGUR_POST_URI = "https://api.imgur.com/oauth2/token";

			log.info("Connecting to update access token...");
			URL pinURL = new URL(IMGUR_POST_URI);
//			log.info("refreshToken: New URL made");
			URLConnection conn = pinURL.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
//			log.info("refreshToken: Connected.");
			String data;
			try
			{
//				log.info("refreshToken: encoding URL");
				data = URLEncoder.encode("refresh_token", "UTF-8") + "="
						+ URLEncoder.encode(IMGUR_REFRESH_TOKEN, "UTF-8") + "&"
						+ URLEncoder.encode("client_id", "UTF-8") + "="
						+ URLEncoder.encode(IMGUR_CLIENT_ID, "UTF-8") + "&"
						+ URLEncoder.encode("client_secret", "UTF-8") + "="
						+ URLEncoder.encode(IMGUR_CLIENT_SECRET, "UTF-8") + "&"
						+ URLEncoder.encode("grant_type", "UTF-8") + "="
						+ URLEncoder.encode("refresh_token", "UTF-8");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				log.info("refreshToken: Encoding error: " + e.getMessage());
				data = "Encoding failed!";
			}
//			log.info(data);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			log.info("Finished sending...");
			// capture the XML response as a raw string
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				log.info(line);
				// copy the response before we lose our temporary sting "line"
				str = new String(line);
			}
			in.close();
//			log.info(str);
			
			/*
			 * JSON parsing always fails, so just get the stupid token
			 * substring from the string myself.
			 * 
			 * THIS IS A TOTAL HACK AND NOT RECOMMENDED
			 */
			IMGUR_ACCESS_TOKEN = str.substring(17, 57); 
			properties.put("access", IMGUR_ACCESS_TOKEN);
			Calendar calendar = Calendar.getInstance();
			
			//add 1 hour to current time for expiration of the access token
			expirationTime = (calendar.getTimeInMillis() + 3600000);
			properties.put("expiration", expirationTime);
//			log.info("Expiration: " + calendar.getTimeInMillis() + " | " + expirationTime);
		}

		/**
		 * returns a link text from a String containing XML data
		 */
		private String getElement(String xml, String element)
		{
			try
			{
				log.info("getElement: parsing XML");
				
				Document doc = FileHelper.readXMLFromString(xml);
				Element elem = doc.getDocumentElement();
				NodeList nl = elem.getElementsByTagName(element);
				
				if(nl.getLength() > 0)
				{
//					log.info(nl.item(0).getTextContent());
					return nl.item(0).getTextContent();
				}
				else
				{
					return "No element.";
				}
			}
			catch (Exception e)
			{
				log.info("Error:getElement: " + e.getMessage());
				return xml; // return whatever came in if it was bad
			}

		}
	}

}
