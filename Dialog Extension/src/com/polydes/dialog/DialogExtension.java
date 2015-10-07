package com.polydes.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import stencyl.core.lib.Game;
import stencyl.sw.SW;
import stencyl.sw.editors.game.GameSettingsDialog;
import stencyl.sw.editors.game.advanced.ExtensionInstance;
import stencyl.sw.editors.snippet.designer.DefinitionParser;
import stencyl.sw.ext.ExtensionWrapper;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.ext.DataStructureExtension;
import com.polydes.datastruct.ext.DataTypeExtension;
import com.polydes.dialog.app.MainEditor;
import com.polydes.dialog.data.stores.Dialog;
import com.polydes.dialog.data.stores.Macros;
import com.polydes.dialog.defaults.Defaults;
import com.polydes.dialog.types.DialogDataTypes;

public class DialogExtension extends GameExtension implements DataTypeExtension, DataStructureExtension
{
	private static DialogExtension _instance;
	
	public static DialogExtension get()
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
		
		isInMenu = true;
		menuName = "Dialog Extension";

		isInGameCenter = true;
		gameCenterName = "Dialog Extension";
	}
	
	@Override
	public void extensionsReady()
	{
		
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
	public void onInstalledForGame()
	{
		ExtensionWrapper dsExtWrapper = SW.get().getExtensionManager().getExtensions().get("com.polydes.datastruct");
		GameExtension e = (GameExtension) dsExtWrapper.getExtension();
		
		if(e.getGame() == null)
			e.installForGame();
		
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
		
		if(detectOldInstall())
			updateFromVersion(4);
		else
			loadDefaults();
	}
	
	private boolean detectOldInstall()
	{
		return new File(Locations.getGameLocation(getGame()) + "extras/[ext] dialog").exists();
	}
	
	@Override
	public void onUninstalledForGame()
	{
		FileHelper.delete(getExtrasFolder());
		FileHelper.delete(getDataFolder());
	}
	
	@Override
	public void updateFromVersion(int fromVersion)
	{
		if(fromVersion <= 4)
		{
			File oldExtrasFolder = new File(Locations.getGameLocation(getGame()) + "extras/[ext] dialog");
			
			FileHelper.copyDirectory(oldExtrasFolder, getExtrasFolder());
			FileHelper.delete(oldExtrasFolder);
		}
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
//		showMessageDialog("Dialog Engine Extension Installed", "Refresh any open behaviors in order to see Dialog Extension blocks.");
	}
	
	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(getExtrasFolder(), "images" + File.separator + "Default Window.png");
			f.getParentFile().mkdirs();
			FileHelper.writeToPNG(f.getAbsolutePath(), Defaults.loadImage("Default Window.png"));
			
			FileUtils.writeStringToFile(new File(getExtrasFolder(), "dialog.txt"), Defaults.load("dialog.txt"));
			FileUtils.writeStringToFile(new File(getExtrasFolder(), "macros.txt"), Defaults.load("macros.txt"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
	public void onGameWithDataOpened()
	{
		Dialog.get().load(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().load(new File(getExtrasFolder(), "macros.txt"));
	}

	@Override
	public void onGameWithDataSaved()
	{
		Dialog.get().saveChanges(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().saveChanges(new File(getExtrasFolder(), "macros.txt"));
		
		MainEditor.get().gameSaved();
	}
	
	@Override
	public void onGameWithDataClosed()
	{
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
	public OptionsPanel onOptions()
	{
		return null;
	}
	
	@Override
	protected boolean hasOptions()
	{
		return false;
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
