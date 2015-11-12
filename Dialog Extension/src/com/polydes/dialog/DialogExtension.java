package com.polydes.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import com.polydes.common.ext.ExtensionInterface;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ext.HaxeDataTypeExtension;
import com.polydes.dialog.app.MainEditor;
import com.polydes.dialog.data.def.elements.StructureCommand.CommandType;
import com.polydes.dialog.data.def.elements.StructureCommands.CommandsType;
import com.polydes.dialog.data.def.elements.StructureDrawkey.DrawkeyType;
import com.polydes.dialog.data.def.elements.StructureDrawkeys.DrawkeysType;
import com.polydes.dialog.data.def.elements.StructureExtension;
import com.polydes.dialog.data.def.elements.StructureExtension.ExtensionType;
import com.polydes.dialog.data.stores.Dialog;
import com.polydes.dialog.data.stores.Macros;
import com.polydes.dialog.defaults.Defaults;
import com.polydes.dialog.updates.V5_GameExtensionUpdate;
import com.polydes.dialog.updates.V6_ExtensionSubmodules;

import stencyl.core.lib.Game;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class DialogExtension extends GameExtension
{
	private static DialogExtension _instance;
	
	ArrayList<HaxeDataType> types;
	ArrayList<SDEType<?>> sdeTypes;
	
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
		
		sdeTypes = Lang.arraylist(
			new ExtensionType(),
			new CommandsType(),
			new CommandType(),
			new DrawkeysType(),
			new DrawkeyType()
		);
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
		if(fromVersion < 5)
			new V5_GameExtensionUpdate().run();
		if(fromVersion < 6)
			new V6_ExtensionSubmodules().run();
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
		ExtensionInterface.doUponLoad("com.polydes.datastruct", () -> {
			
			DataStructuresExtension dse = DataStructuresExtension.get();
			
			for(SDEType<?> sdet : sdeTypes)
				dse.getSdeTypes().registerItem(getManifest().id, sdet);
			SDETypes.fromClass(StructureTab.class).childTypes.add(StructureExtension.class);
			
			types = HaxeDataTypeExtension.readTypesFolder(new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "types"));
			for(HaxeDataType type : types)
				dse.getHaxeTypes().registerItem(type);
			
			File defLoc = new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "def");
			dse.getStructureDefinitions().addFolder(defLoc, getManifest().name);
		});
		
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
		SDETypes.fromClass(StructureTab.class).childTypes.remove(StructureExtension.class);
		
		Dialog.get().unload();
		Macros.get().unload();

		MainEditor.disposePages();
		
		types = null;
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
}
