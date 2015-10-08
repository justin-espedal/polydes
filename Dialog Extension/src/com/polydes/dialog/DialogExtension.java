package com.polydes.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import stencyl.core.lib.Game;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
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
	}
	
	@Override
	public void onInstalledForGame()
	{
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
	
	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(getExtrasFolder(), "images" + File.separator + "Default Window.png");
			f.getParentFile().mkdirs();
			if(!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(), Defaults.loadImage("Default Window.png"));
			if(!new File(getExtrasFolder(), "dialog.txt").exists())
				FileUtils.writeStringToFile(new File(getExtrasFolder(), "dialog.txt"), Defaults.load("dialog.txt"));
			if(!new File(getExtrasFolder(), "macros.txt").exists())
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
	
	@Override
	public File getDefinitionsFolder()
	{
		return new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "def");
	}
	
	@Override
	public ArrayList<DataType<?>> getDataTypes()
	{
		return DialogDataTypes.types;
	}
}
