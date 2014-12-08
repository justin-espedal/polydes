package stencyl.ext.polydes.paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.paint.app.MainEditor;
import stencyl.ext.polydes.paint.data.stores.Fonts;
import stencyl.ext.polydes.paint.data.stores.Images;
import stencyl.ext.polydes.paint.defaults.Defaults;
import stencyl.ext.polydes.paint.res.Resources;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;

public class Main extends BaseExtension
{
	private static String dataFolderName = "[ext] paint";
	
	private static Main _instance;

	private String gameDir;
	private String extrasDir;
	private File extras;
	private File fontsFile;
	private File imagesFile;

	public static Main get()
	{
		return _instance;
	}

	/*
	 * Happens when StencylWorks launches.
	 * 
	 * Avoid doing anything time-intensive in here, or it will slow down launch.
	 */
	@Override
	public void onStartup()
	{
		icon = Resources.loadIcon("icon.png");
		classname = this.getClass().getName();
		String loc = Locations.getExtensionPrefsLocation(classname);
		if(new File(loc).exists())
			Loader.readLocalDictionary(loc, properties);
		
		_instance = this;

		name = "Paint Extension";
		description = "Paint Extension.";
		authorName = "Justin Espedal";
		website = "http://dialog.justin.espedaladventures.com/";
		internalVersion = 2;
		version = "1.1.1";

		isInMenu = true;
		menuName = "Paint Extension";

		isInGameCenter = true;
		gameCenterName = "Paint Extension";

		gameDir = "";
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
		print("DialogExtension : Activated");
	}

	@Override
	public JPanel onGameCenterActivate()
	{
		return MainEditor.get();
	}

	public JPanel getBlankPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(62, 62, 62));
		return panel;
	}

	/*
	 * Happens when StencylWorks closes.
	 * 
	 * Usually used to save things out.
	 */
	@Override
	public void onDestroy()
	{
		print("DialogExtension : Destroyed");
	}

	/*
	 * Happens when a game is saved.
	 */
	@Override
	public void onGameSave(Game game)
	{
		Fonts.get().saveChanges(fontsFile);
		Images.get().saveChanges(imagesFile);
		
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
		gameDir = Locations.getGameLocation(game);
		extrasDir = gameDir + "extras" + File.separator + dataFolderName + File.separator;
		extras = new File(extrasDir);
		
		if(extras.list().length == 0)
			loadDefaults();
		
		fontsFile = new File(extrasDir + "fonts" + File.separator);
		imagesFile = new File(extrasDir + "images" + File.separator);
		
		Fonts.get().load(fontsFile);
		Images.get().load(imagesFile);
	}

	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(extrasDir + "fonts" + File.separator + "Default Font.fnt");
			f.getParentFile().mkdirs();
			if (!f.exists())
				FileHelper.writeStringToFile(f.getAbsolutePath(), Defaults.load("Default Font.fnt"));
	
			f = new File(extrasDir + "fonts" + File.separator + "Default Font.png");
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Default Font.png"));
	
			f = new File(extrasDir + "images" + File.separator
					+ "Default Window.png");
			f.getParentFile().mkdirs();
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Default Window.png"));
	
			f = new File(extrasDir + "images" + File.separator + "Pointer.png");
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Pointer.png"));
		}
		catch (IOException e)
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

		gameDir = "";

		Fonts.get().unload();
		Images.get().unload();

		MainEditor.disposePages();
	}

	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	public OptionsPanel onOptions()
	{
		return new OptionsPanel()
		{
			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without knowing Swing
			 * (Java's GUI library).
			 */
			@Override
			public void init()
			{
				startForm();
				addHeader("Options");
				endForm();
			}

			/*
			 * Use this to save the form data out. All you need to do is place
			 * the properties into preferences.
			 */
			@Override
			public void onPressedOK()
			{
				System.out
						.println("DialogExtension : OptionsPanel : onPressedOK");
			}

			/*
			 * Happens whenever the user presses cancel or clicks the "x" in the
			 * corner
			 */
			@Override
			public void onPressedCancel()
			{
				System.out
						.println("DialogExtension : OptionsPanel : onPressedCancel");
			}

			/*
			 * Happens whenever the user brings this options panel up
			 */
			@Override
			public void onShown()
			{
				System.out.println("DialogExtension : OptionsPanel : onShown");
			}
		};
	}

	/*
	 * Happens when the extension is first installed.
	 */
	@Override
	public void onInstall()
	{
		print("DialogExtension : Install");
	}

	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	@Override
	public void onUninstall()
	{
		print("DialogExtension : Uninstall");
	}

	public void print(String s)
	{
		System.out.println(s);
	}
}
