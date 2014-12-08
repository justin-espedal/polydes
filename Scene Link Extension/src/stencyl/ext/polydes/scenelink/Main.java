package stencyl.ext.polydes.scenelink;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.scenelink.data.LinkModel;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;
import stencyl.ext.polydes.scenelink.io.XML;
import stencyl.ext.polydes.scenelink.res.Resources;
import stencyl.ext.polydes.scenelink.ui.MainPage;
import stencyl.ext.polydes.scenelink.ui.combos.PageComboModel;
import stencyl.ext.polydes.scenelink.util.ColorUtil;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;

public class Main extends BaseExtension
{
	private static String dataFolderName = "[ext] scene link";
	//private static String mainFileName = "scenelink.xml";
	
	public static File dataFolder;
	public static File pagesFolder;
	public static File resourcesFolder;
	
	public static HashMap<Integer, LinkPageModel> pages;
	
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	@Override
	public void onStartup()
	{
		icon = Resources.loadIcon("icon.png");
		classname = this.getClass().getName();
		String loc = Locations.getExtensionPrefsLocation(classname);
		if(new File(loc).exists())
			Loader.readLocalDictionary(loc, properties);
		
		name = "Scene Link Extension";
		description = "View and access Scenes spatially.";
		authorName = "Justin Espedal";
		website = "http://dialog.justin.espedaladventures.com/";
		internalVersion = 1;
		version = "1.0.0";

		isInMenu = true;
		menuName = "Scene Link";

		isInGameCenter = true;
		gameCenterName = "Scene Link";
		
		pages = null;
	}
	
	@Override
	public void extensionsReady()
	{
		for(BaseExtension e : ExtensionManager.get().getExtensions().values())
		{
			if(e.getClassname().equals("ExtrasManagerExtension"))
			{
				try
				{
					MethodUtils.invokeMethod(e, "requestFolderOwnership", this, dataFolderName);
				}
				catch (NoSuchMethodException e1)
				{
					e1.printStackTrace();
				}
				catch (IllegalAccessException e1)
				{
					e1.printStackTrace();
				}
				catch (InvocationTargetException e1)
				{
					e1.printStackTrace();
				}
			}
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
		
	}
	
	@Override
	public JPanel onGameCenterActivate()
	{
		return MainPage.get();
	}
	
	/*
	 * Happens when StencylWorks closes.
	 *  
	 * Usually used to save things out.
	 */
	@Override
	public void onDestroy()
	{
		
	}
	
	/*
	 * Happens when a game is saved.
	 */
	@Override
	public void onGameSave(Game game)
	{
		if(pages != null)
		{
			for(LinkPageModel model : pages.values())
			{
				String pageName = pagesFolder.getAbsolutePath() + File.separator + model.getId() + ".xml";
				XML.wrObjectToFile(pageName, model);
			}
		}
	}
	
	/*
	 * Happens when a game is opened.
	 */
	@Override
	public void onGameOpened(Game game)
	{
		dataFolder = openFolder(new File(Locations.getGameLocation(game) + "extras" + File.separator + dataFolderName + File.separator));
		resourcesFolder = openFolder(new File(dataFolder, "resources" + File.separator));
		pagesFolder = openFolder(new File(dataFolder, "pages" + File.separator));
		
		Resources.loadResourceNames();
		
		pages = new HashMap<Integer, LinkPageModel>();
		
		File[] pageFiles = pagesFolder.listFiles();
		for(File f : pageFiles)
		{
			LinkPageModel m = (LinkPageModel) XML.rObjectFromFile(f.getAbsolutePath(), LinkPageModel.class);
			pages.put(m.getId(), m);
		}
		PageComboModel.updatePages();
		
		if(pageFiles.length == 0)
			generateNewModel();
	}
	
	public File openFolder(File f)
	{
		if(!f.exists())
			f.mkdirs();
		return f;
	}

	/*
	 * Happens when a game is closed.
	 */
	@Override
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);
		pages = null;
	}
	
	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	public OptionsPanel onOptions()
	{
		System.out.println("SampleExtension : Options");
		
		return new OptionsPanel()
		{
			JTextField text;
			JCheckBox check;
			JComboBox dropdown;
			
			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without
			 * knowing Swing (Java's GUI library).
			 */
			@Override
			public void init()
			{
				startForm();
				addHeader("Options");
				text = addTextfield("Name:");
				check = addCheckbox("Do you like chocolate?");
				dropdown = addDropdown("Where are you from?", new String[] {"Americas", "Europe", "Asia", "Other"});
				endForm();
				
				//Set the form's values
				text.setText("" + properties.get("name"));
				check.setSelected(Boolean.parseBoolean("" + properties.get("choc")));
				dropdown.setSelectedItem(properties.get("loc"));
			}
			
			/*
			 * Use this to save the form data out.
			 * All you need to do is place the properties into preferences.
			 */
			@Override
			public void onPressedOK()
			{
				properties.put("name", text.getText());
				properties.put("choc", check.isSelected());
				properties.put("loc", dropdown.getSelectedItem());
			}

			@Override
			public void onPressedCancel()
			{
			}

			@Override
			public void onShown()
			{
			}
		};
	}
	
	/*
	 * Happens when the extension is first installed.
	 */
	@Override
	public void onInstall()
	{
		
	}
	
	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	@Override
	public void onUninstall()
	{
		
	}
	
	public static Collection<LinkPageModel> getPages()
	{
		return pages.values();
	}
	
	public static LinkPageModel getPageModel(int id)
	{
		return pages.get(id);
	}
	
	private static int nextPageModelID()
	{
		int id = -1;
		for(int i : pages.keySet())
			id = Math.max(id, i);
		return ++id;
	}
	
	public static LinkPageModel generateNewModel()
	{
		int id = nextPageModelID();
		LinkPageModel newModel = new LinkPageModel
		(
			id,
			"Page " + id,
			"",
			new Dimension(640, 640),
			"",
			new Point(0, 0),
			ColorUtil.decode("#ff333333"),
			new Point(0, 0),
			new Dimension(32, 32),
			ColorUtil.decode("#ff81969a"),
			true,
			true,
			new HashMap<Integer, LinkModel>()
		);
		pages.put(id, newModel);
		PageComboModel.updatePages();
		return newModel;
	}
}