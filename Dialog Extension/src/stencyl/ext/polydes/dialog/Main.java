package stencyl.ext.polydes.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.ext.DataStructureExtension;
import stencyl.ext.polydes.datastruct.ext.DataTypeExtension;
import stencyl.ext.polydes.dialog.app.MainEditor;
import stencyl.ext.polydes.dialog.data.stores.Dialog;
import stencyl.ext.polydes.dialog.data.stores.Macros;
import stencyl.ext.polydes.dialog.defaults.Defaults;
import stencyl.ext.polydes.dialog.res.Resources;
import stencyl.ext.polydes.dialog.types.DialogDataTypes;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.editors.game.advanced.EngineExtension;
import stencyl.sw.editors.game.advanced.ExtensionInstance;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;

public class Main extends BaseExtension implements DataTypeExtension, DataStructureExtension
{
	private static Main _instance;
	
	private static String dataFolderName = "[ext] dialog";
	
	private EngineExtension dialogExtension;
	private String gameDir;
	private String extrasDir;
	private File extras;
	private File dialogFile;
	private File macrosFile;
	
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
		
		name = "Dialog Extension";
		description = "Toolset side of the Dialog Extension.";
		authorName = "Justin Espedal";
		website = "http://dialog.justin.espedaladventures.com/";
		internalVersion = 4;
		version = "1.4.1";

		isInMenu = true;
		menuName = "Dialog Extension";

		isInGameCenter = true;
		gameCenterName = "Dialog Extension";

		dialogExtension = null;
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
		if (dialogExtension == null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					showMessageDialog(
						"Dialog Extension Isn't Enabled",
						"The \"Dialog Extension\" Engine Extension is not yet enabled for this game."
					);
				}
			});
			return getBlankPanel();
		}
		else
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

		if (dialogExtension == null)
			return;
	}

	/*
	 * Happens when a game is saved.
	 */
	@Override
	public void onGameSave(Game game)
	{
		if (dialogExtension == null)
			return;

		Dialog.get().saveChanges(dialogFile);
		Macros.get().saveChanges(macrosFile);
		
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
		dialogExtension = null;

		for (ExtensionInstance ext : game.extensions.values())
		{
			if (ext.getExtensionID().equals("dialog"))
			{
				if (ext.isEnabled())
				{
					dialogExtension = ext.getExtension();
					break;
				}
			}
		}
		
		if (dialogExtension == null)
			return;
		
		gameDir = Locations.getGameLocation(game);
		extrasDir = gameDir + "extras" + File.separator + "[ext] dialog" + File.separator;
		extras = new File(extrasDir);
		
		if (!extras.exists())
		{
			extras.mkdirs();
			loadDefaults();
		}
		
		dialogFile = getExtrasFile(extrasDir + "dialog.txt");
		macrosFile = getExtrasFile(extrasDir + "macros.txt");
		
		Dialog.get().load(dialogFile);
		Macros.get().load(macrosFile);
	}

	private File getExtrasFile(String path)
	{
		File extrasFile = new File(path);
		if (!extrasFile.exists())
		{
			try
			{
				FileHelper.writeStringToFile(path, Defaults.load(extrasFile.getName()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return extrasFile;
	}

	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(extrasDir + "images" + File.separator
					+ "Default Window.png");
			f.getParentFile().mkdirs();
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Default Window.png"));
			
			f = new File(extrasDir + "Default Style.style");
			if (!f.exists())
				FileHelper.writeStringToFile(f.getAbsolutePath(),
						Defaults.load("Default Style.style"));
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

		if (dialogExtension == null)
			return;

		dialogExtension = null;
		gameDir = "";

		Dialog.get().unload();
		Macros.get().unload();

		MainEditor.disposePages();
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
	
	@Override
	public Icon getIcon()
	{
		return super.getIcon();
	}
	
	@Override
	public File getDefinitionsFolder()
	{
		return new File(Locations.getGameExtensionLocation("dialog"), "def");
	}
	
	@Override
	public ArrayList<DataType<?>> getDataTypes()
	{
		return DialogDataTypes.types;
	}
}
