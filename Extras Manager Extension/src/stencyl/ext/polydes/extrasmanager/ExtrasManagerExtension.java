package stencyl.ext.polydes.extrasmanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.JTextField;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.common.nodes.HierarchyModel;
import stencyl.ext.polydes.extrasmanager.app.MainEditor;
import stencyl.ext.polydes.extrasmanager.data.FileEditor;
import stencyl.ext.polydes.extrasmanager.data.FileOpHierarchyModel;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFile;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFolder;
import stencyl.ext.polydes.extrasmanager.io.FileMonitor;
import stencyl.ext.polydes.extrasmanager.io.FileOperations;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.FileHandler;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class ExtrasManagerExtension extends BaseExtension
{
	private static ExtrasManagerExtension _instance;
	public static HashSet<String> ownedFolderNames = new HashSet<String>();
	private static HashMap<String, BaseExtension> folderOwners = new HashMap<String, BaseExtension>();
	
	private static HierarchyModel<SysFile> model;
	
	private static boolean gameOpen;
	
	public static boolean requestFolderOwnership(BaseExtension ext, String folderName)
	{
		System.out.println(ext.getManifest().id + " requesting ownership of " + folderName);
		
		if(ownedFolderNames.contains(folderName))
			return false;
		
		ownedFolderNames.add(folderName);
		folderOwners.put(folderName, ext);
		return true;
	}
	
	private String gameDir;
	private String extrasDir;

	public static ExtrasManagerExtension get()
	{
		return _instance;
	}

	public static HierarchyModel<SysFile> getModel()
	{
		return model;
	}
	
	/*
	 * Happens when StencylWorks launches.
	 * 
	 * Avoid doing anything time-intensive in here, or it will slow down launch.
	 */
	@Override
	public void onStartup()
	{
		super.onStartup();
		
		_instance = this;
		
		isInMenu = true;
		menuName = "Extras Manager";

		isInGameCenter = true;
		gameCenterName = "Extras Manager";

		gameDir = "";
		
//		requestFolderOwnership(this, dataFolderName);
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
		return MainEditor.get();
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
		if(gameOpen)
			MainEditor.get().gameSaved();
	}

	/*
	 * Happens when the user runs, previews or exports the game.
	 */
	@Override
	public void onGameBuild(Game game)
	{
		onGameSave(game);
	}

	/*
	 * Happens when a game is opened.
	 */
	@Override
	public void onGameOpened(Game game)
	{
		gameOpen = true;
		
		gameDir = Locations.getGameLocation(game);
		extrasDir = gameDir + "extras/";
		File extrasFile = new File(extrasDir);
		
		if(!extrasFile.exists())
			extrasFile.mkdir();
		
		SysFolder rootFolder = FileMonitor.registerOnRoot(extrasFile);
		model = new FileOpHierarchyModel(rootFolder);
		
		for(String s : ownedFolderNames)
		{
			File f = new File(extrasFile, s); 
			if(!f.exists())
				f.mkdir();
		}
		
		File templatesFile = new File(new File(Locations.getGameLocation(game) + "extras/" + getManifest().id), "templates");
		
		if(!templatesFile.exists())
		{
			templatesFile.mkdir();
			loadDefaults(templatesFile);
		}
		
		FileOperations.templates = templatesFile.listFiles();
		
		String input = readData();
		
		String[] data;
		if(input == null || input.isEmpty())
			data = new String[] {"", ""};
		else
			data = input.split("\n");
		
		String textPath = data[0];
		String imagePath = data[1];
		FileEditor.typeProgramMap.put("text/plain", textPath);
		FileEditor.typeProgramMap.put("image/png", imagePath);
	}
	
	public void loadDefaults(File templates)
	{
		try
		{
			FileHelper.writeStringToFile
			(
				new File(templates, "New Text.txt").getAbsolutePath(),
				""
			);
			FileHelper.writeToPNG
			(
				new File(templates, "New Image.png").getAbsolutePath(),
				new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
			);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Happens when a game is closed.
	 */
	@Override
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);

		model.dispose();
		model = null;
		FileMonitor.unregister();
		
		gameDir = "";
		extrasDir = "";
		
		MainEditor.disposePages();
		
		gameOpen = false;
	}

	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	@SuppressWarnings("serial")
	public OptionsPanel onOptions()
	{
		return new OptionsPanel()
		{
			String textPath;
			String imagePath;
			
			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without knowing Swing
			 * (Java's GUI library).
			 */
			@Override
			public void init()
			{
				String[] data;
				if(readData() == null || readData().isEmpty())
					data = new String[] {"", ""};
				else
					data = readData().split("\n");
				textPath = data[0];
				imagePath = data[1];
				
				FileHandler textHandler = new FileHandler()
				{
					@Override
					public void handleFile(File f)
					{
						textPath = "\"" + f.getAbsolutePath() + "\"";
						FileEditor.typeProgramMap.put("text/plain", textPath);
					}
				};
				FileHandler imageHandler = new FileHandler()
				{
					@Override
					public void handleFile(File f)
					{
						imagePath = "\"" + f.getAbsolutePath() + "\"";
						FileEditor.typeProgramMap.put("image/png", imagePath);
					}
				};
				
				startForm();
				addHeader("Options");
				JTextField textField = addFileChooser("Text Editor", textHandler, false);
				JTextField imageField = addFileChooser("Image Editor", imageHandler, false);
				textField.setText(textPath);
				imageField.setText(imagePath);
				endForm();
			}

			/*
			 * Use this to save the form data out. All you need to do is place
			 * the properties into preferences.
			 */
			@Override
			public void onPressedOK()
			{
				saveData(textPath + "\n" + imagePath);
			}

			/*
			 * Happens whenever the user presses cancel or clicks the "x" in the
			 * corner
			 */
			@Override
			public void onPressedCancel()
			{
				
			}

			/*
			 * Happens whenever the user brings this options panel up
			 */
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

	public void print(String s)
	{
		System.out.println(s);
	}
}
