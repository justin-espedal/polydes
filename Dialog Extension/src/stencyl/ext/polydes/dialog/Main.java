package stencyl.ext.polydes.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.common.ext.GameExtension;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.ext.DataStructureExtension;
import stencyl.ext.polydes.datastruct.ext.DataTypeExtension;
import stencyl.ext.polydes.dialog.app.MainEditor;
import stencyl.ext.polydes.dialog.data.stores.Dialog;
import stencyl.ext.polydes.dialog.data.stores.Macros;
import stencyl.ext.polydes.dialog.defaults.Defaults;
import stencyl.ext.polydes.dialog.types.DialogDataTypes;
import stencyl.sw.SW;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.editors.game.GameSettingsDialog;
import stencyl.sw.editors.game.advanced.ExtensionInstance;
import stencyl.sw.editors.snippet.designer.DefinitionParser;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class Main extends GameExtension implements DataTypeExtension, DataStructureExtension
{
	private static Main _instance;
	
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
		super.onStartup();
		
		_instance = this;
		
		name = "Dialog Extension";
		description = "Toolset side of the Dialog Extension.";
		authorName = "Justin Espedal";
		website = "https://github.com/justin-espedal/polydes";
		internalVersion = 5;
		version = "1.6.0";

		isInMenu = true;
		menuName = "Dialog Extension";

		isInGameCenter = true;
		gameCenterName = "Dialog Extension";
	}
	
	@Override
	public void extensionsReady()
	{
//		for(BaseExtension e : ExtensionManager.get().getExtensions().values())
//		{
//			if(e.getClassname().equals("ExtrasManagerExtension"))
//			{
//				try
//				{
//					MethodUtils.invokeMethod(e, "requestFolderOwnership", this, dataFolderName);
//				}
//				catch (NoSuchMethodException e1)
//				{
//					e1.printStackTrace();
//				}
//				catch (IllegalAccessException e1)
//				{
//					e1.printStackTrace();
//				}
//				catch (InvocationTargetException e1)
//				{
//					e1.printStackTrace();
//				}
//			}
//		}
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
	public boolean isInstalledForGame(Game game)
	{
		GameExtension dsExt = findDataStructuresExtension();
		boolean dsExtInstalled = dsExt.isActive() && dsExt.isInstalledForGame(game);
		ExtensionInstance dgExt = findDialogEngineExtension();
		boolean dgExtInstalled = dgExt != null && dgExt.isEnabled();
		
		return dgExtInstalled && dsExtInstalled &&
			(
				//v4 installation
				new File(Locations.getGameLocation(game) + "extras/[ext] dialog").exists() ||
				//v5+ installation
				getExtrasFolder().exists()
			);
	}
	
	@Override
	public void onInstalledForGame(Game game)
	{
		GameExtension dsExt = findDataStructuresExtension();
		if(!dsExt.isActive())
			dsExt.setActive(true);
		if(!dsExt.isInstalledForGame(game))
			dsExt.installForGame(game);
		
		if(findDialogEngineExtension() == null)
			downloadDialogEngineExtension(new Runnable()
			{
				@Override
				public void run()
				{
					engineExtensionReady();
				}
			});
		else
			engineExtensionReady();
	}
	
	private void engineExtensionReady()
	{
		ExtensionInstance dgExt = findDialogEngineExtension();
		
		if(!dgExt.isEnabled())
			installEngineExtension(dgExt);
		getExtrasFolder().mkdirs();
		
		loadDefaults();
	}
	
	@Override
	public void onUninstalledForGame(Game game)
	{
		FileHelper.delete(getExtrasFolder());
		FileHelper.delete(getDataFolder());
	}
	
	@Override
	public void updateFromVersion(Game game, int fromVersion)
	{
		if(fromVersion <= 4)
		{
			File oldExtrasFolder = new File(Locations.getGameLocation(game) + "extras/[ext] dialog");
			
			File extrasFolder = getExtrasFolder();
			extrasFolder.mkdirs();
			
			try
			{
				FileUtils.moveDirectory(oldExtrasFolder, extrasFolder);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private GameExtension findDataStructuresExtension()
	{
		for(BaseExtension e : ExtensionManager.get().getExtensions().values())
		{
			if(e instanceof stencyl.ext.polydes.datastruct.Main)
			{
				return (GameExtension) e;
			}
		}
		
		return null;
	}
	
	private ExtensionInstance findDialogEngineExtension()
	{
		for (ExtensionInstance ext : Game.getGame().extensions.values())
		{
			if (ext.getExtensionID().equals("dialog"))
				return ext;
		}
		
		return null;
	}
	
	private void downloadDialogEngineExtension(final Runnable callback)
	{
		final File tempZip = new File(Locations.getGameExtensionsLocation(), "dialog.zip");
		
		FileHelper.downloadFile
		(
			"http://dialogextension.com/download/Dialog%20Extension%20Latest.zip",
			"Dialog Engine Extension",
			"Failed to download engine extension.",
			tempZip,
			LanguagePack.get(),
			new Runnable()
			{
				@Override
				public void run()
				{
					FileHelper.unzip(tempZip, new File(Locations.getGameExtensionsLocation()));
					FileHelper.delete(new File(Locations.getPath(Locations.getGameExtensionsLocation(),"__MACOSX")));
					SW.get().getEngineExtensionManager().reload();
					callback.run();
				}
			}
		);
	}
	
	private void installEngineExtension(ExtensionInstance ext)
	{
		ext.enable();
		DefinitionParser.addDefinitionsForExtension(ext.getExtension());
		GameSettingsDialog.reset();
		showMessage("Dialog Engine Extension Installed", "Refresh any open behaviors in order to see Dialog Extension blocks.");
	}
	
	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(getExtrasFolder(), "images" + File.separator + "Default Window.png");
			f.getParentFile().mkdirs();
			FileHelper.writeToPNG(f.getAbsolutePath(), Defaults.loadImage("Default Window.png"));
			
			FileUtils.writeStringToFile(new File(getExtrasFolder(), "Default Style.style"), Defaults.load("Default Style.style"));
			FileUtils.writeStringToFile(new File(getExtrasFolder(), "dialog.txt"), Defaults.load("dialog.txt"));
			FileUtils.writeStringToFile(new File(getExtrasFolder(), "macros.txt"), Defaults.load("macros.txt"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public JPanel getMainPage()
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
		print("DialogExtension : Destroyed");
	}

	
	/*
	 * Happens when the user runs, previews or exports the game.
	 */
	@Override
	public void onGameBuild(Game game)
	{
		
	}
	
	@Override
	public void onGameWithDataOpened(Game game)
	{
		Dialog.get().load(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().load(new File(getExtrasFolder(), "macros.txt"));
		
		((stencyl.ext.polydes.datastruct.Main) findDataStructuresExtension()).addExtension(this);
	}

	@Override
	public void onGameWithDataSaved(Game game)
	{
		Dialog.get().saveChanges(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().saveChanges(new File(getExtrasFolder(), "macros.txt"));
		
		MainEditor.get().gameSaved();
	}
	
	@Override
	public void onGameWithDataClosed(Game game)
	{
		Dialog.get().unload();
		Macros.get().unload();

		MainEditor.disposePages();
		
		stencyl.ext.polydes.datastruct.Main dsExt = (stencyl.ext.polydes.datastruct.Main) findDataStructuresExtension();
		dsExt.dataStructureExtensions.remove(this);
		dsExt.dataTypeExtensions.remove(this);
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
			@Override
			public void init()
			{
			}
			@Override
			public void onPressedOK()
			{
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
