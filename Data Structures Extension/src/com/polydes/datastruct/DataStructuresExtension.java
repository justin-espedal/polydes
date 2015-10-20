package com.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import stencyl.core.lib.Game;
import stencyl.sw.SW;
import stencyl.sw.editors.snippet.designer.Definitions.DefinitionMap;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.datastruct.data.core.Images;
import com.polydes.datastruct.data.structure.StructureCondition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.Structures.MissingStructureDefinitionException;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.ext.DataStructureExtension;
import com.polydes.datastruct.ext.DataTypeExtension;
import com.polydes.datastruct.io.HXGenerator;
import com.polydes.datastruct.io.Text;

public class DataStructuresExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DataStructuresExtension.class);
	
	private static DataStructuresExtension instance;
	
	public ArrayList<DataTypeExtension> dataTypeExtensions = new ArrayList<DataTypeExtension>();
	public ArrayList<DataStructureExtension> dataStructureExtensions = new ArrayList<DataStructureExtension>();
	
	public static boolean forceUpdateData = false;
	private boolean initialized = false;
	
	private boolean tryToReloadForNewExtensions = false;
	
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	@Override
	public void onStartup()
	{
		super.onStartup();
		
		instance = this;
		
		isInMenu = true;
		menuName = "Data Structures";
		
		isInGameCenter = true;
		gameCenterName = "Data Structures";
		
		initialized = false;
		
		Prefs.DEFPAGE_X = readIntProp("defpage.x", -1);
		Prefs.DEFPAGE_Y = readIntProp("defpage.y", -1);
		Prefs.DEFPAGE_WIDTH = readIntProp("defpage.width", 640);
		Prefs.DEFPAGE_HEIGHT = readIntProp("defpage.height", 480);
		Prefs.DEFPAGE_SIDEWIDTH = readIntProp("defpage.sidewidth", DarkTree.DEF_WIDTH);
		Prefs.DEFPAGE_SIDEDL = readIntProp("defpage.sidedl", 150);
		
//		SW.get().getExtensionManager().getActiveExtensions().addListener(extListener);
//		SW.get().getExtensionManager().getActiveGameExtensions().addListener(gameExtListener);
		refreshExtensions();
	}
	
	public static DataStructuresExtension get()
	{
		return instance;
	}
	
	public void refreshExtensions()
	{
		BaseExtension[] exts = allExtensions.toArray(new BaseExtension[allExtensions.size()]);
		for(BaseExtension e : exts)
			removeExtension(e);
		
		for(BaseExtension e : SW.get().getExtensionManager().getActiveExtensions().values())
		{
			if(e instanceof GameExtension)
				continue;
			
			addExtension(e);
		}
		for(GameExtension e : SW.get().getExtensionManager().getActiveGameExtensions().values())
		{
			addExtension(e);
		}
	}
	
	private HashSet<BaseExtension> allExtensions = new HashSet<BaseExtension>();
	
	public void addExtension(BaseExtension e)
	{
		if(allExtensions.contains(e))
			return;
		allExtensions.add(e);
		
		DataTypeExtension dtExt =
			(e instanceof DataTypeExtension) ?
			(DataTypeExtension) e :
			null;
		
		DataStructureExtension dsExt =
			(e instanceof DataStructureExtension) ?
			(DataStructureExtension) e :
			null;
			
		if(dtExt != null)
			dataTypeExtensions.add(dtExt);
		if(dsExt != null)
			dataStructureExtensions.add(dsExt);
		
		if(initialized)
		{
			if(dtExt != null)
				for(DataType<?> type : dtExt.getDataTypes())
					Types.addType(type);
				
			if(dsExt != null)
				StructureDefinitions.get().addFolder(dsExt.getDefinitionsFolder(), e.getManifest().name);
			
			Types.initNewTypeFields();
			Types.initNewTypeMethods();
			Types.finishInit();
		}
		else if(tryToReloadForNewExtensions)
		{
			tryToReloadForNewExtensions = false;
			reloadGame();
		}
	}
	
	public void removeExtension(BaseExtension e)
	{
		if(!allExtensions.contains(e))
			return;
		allExtensions.remove(e);
		
		DataTypeExtension dtExt =
			(e instanceof DataTypeExtension) ?
			(DataTypeExtension) e :
			null;
		
		DataStructureExtension dsExt =
			(e instanceof DataStructureExtension) ?
			(DataStructureExtension) e :
			null;
		
		if(dtExt != null)
			dataTypeExtensions.remove(dtExt);
		if(dsExt != null)
			dataStructureExtensions.remove(dsExt);
		
		if(initialized)
		{
			if(dtExt != null)
				for(DataType<?> type : dtExt.getDataTypes())
					Types.removeType(type);
			
			if(dsExt != null)
				StructureDefinitions.get().removeFolder(dsExt.getDefinitionsFolder());
			
			Types.initNewTypeFields();
			Types.initNewTypeMethods();
			Types.finishInit();
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
		properties.put("defpage.x", Prefs.DEFPAGE_X);
		properties.put("defpage.y", Prefs.DEFPAGE_Y);
		properties.put("defpage.width", Prefs.DEFPAGE_WIDTH);
		properties.put("defpage.height", Prefs.DEFPAGE_HEIGHT);
		properties.put("defpage.sidewidth", Prefs.DEFPAGE_SIDEWIDTH);
		properties.put("defpage.sidedl", Prefs.DEFPAGE_SIDEDL);
		
		super.onDestroy();
		
//		SW.get().getExtensionManager().getActiveExtensions().removeListener(extListener);
//		SW.get().getExtensionManager().getActiveGameExtensions().removeListener(gameExtListener);
	}
	
	private static String sourceDir;
	
	@Override
	public void onGameWithDataSaved()
	{
		if(initialized)
		{
			try
			{
				StructureDefinitions.get().saveChanges();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			if(forceUpdateData)
			{
				forceUpdateData = false;
				Structures.root.setDirty(true);
			}
			
			File saveTo = new File(getExtrasFolder(), "data");
			if(Structures.root.isDirty())
			{
				File temp = new File(Locations.getTemporaryDirectory() + File.separator + "data structures save");
				temp.mkdirs();
				boolean failedToSave = false;
				
				try{FileUtils.deleteDirectory(temp);}catch(IOException e){e.printStackTrace();}
					temp.mkdirs();
				try{Structures.get().saveChanges(temp);}catch(IOException e){failedToSave = true; e.printStackTrace();}
				if(!failedToSave)
				{
					try{FileUtils.deleteDirectory(saveTo);}catch(IOException e){e.printStackTrace();}
					saveTo.mkdirs();
					try{FileUtils.copyDirectory(temp, saveTo);}catch(IOException e){e.printStackTrace();}
				}
			}
			
			DataStructuresExtension.sourceDir = Locations.getPath(Locations.getHXProjectDir(getGame()), "Source");
			
			File out = new File(Locations.getPath(Locations.getHXProjectDir(getGame()), "Assets", "data"), "MyDataStructures.txt");
			Text.writeLines(out, HXGenerator.generateFileList(saveTo));
		}
	}
	
	@Override
	public void onGameBuild(Game game)
	{
		if(initialized)
		{
			write("com.polydes.datastruct.DataStructureReader", HXGenerator.generateReader());
			for(DataType<?> type : Types.typeFromXML.values())
			{
				List<String> lines = type.generateHaxeClass();
				if(lines != null)
					write(type.haxeType, lines);
			}
		}
	}
	
	private void write(String path, List<String> lines)
	{
		path = StringUtils.replace(path, ".", File.separator) + ".hx";
		File out = new File(sourceDir, path);
		if(!out.getParentFile().exists())
			out.getParentFile().mkdirs();
		Text.writeLines(out, lines);
	}
	
	@Override
	public void onInstalledForGame()
	{
		if(detectOldVersion())
			updateFromVersion(2);
		else
		{
			new File(getExtrasFolder(), "data").mkdirs();
			new File(getDataFolder(), "defs").mkdirs();
		}
	}
	
	private boolean detectOldVersion()
	{
		return new File(Locations.getGameLocation(getGame()) + "extras/[ext] data structures").exists();
	}
	
	@Override
	public void onUninstalledForGame()
	{
		
	}
	
	@Override
	public void updateFromVersion(int fromVersion)
	{
		if(fromVersion <= 2)
		{
			File oldExtrasFolder = new File(Locations.getGameLocation(getGame()) + "extras/[ext] data structures");
			File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
			File oldExtrasDataFolder = new File(oldExtrasFolder, "data");
			
			FileHelper.copyDirectory(oldExtrasDataFolder, new File(getExtrasFolder(), "data"));
			FileHelper.copyDirectory(oldExtrasDefsFolder, new File(getDataFolder(), "defs"));
			FileHelper.delete(oldExtrasFolder);
		}
	}
	
	@Override
	public void onGameWithDataOpened()
	{
		refreshExtensions();
		
		try
		{
			//Add all Types
			Types.addBasicTypes();
			
			for(DataTypeExtension ext : dataTypeExtensions)
				for(DataType<?> type : ext.getDataTypes())
					Types.addType(type);
			
			StructureDefinitions.get().addFolder(new File(getDataFolder(), "defs"), "My Structures");
			for(DataStructureExtension ext : dataStructureExtensions)
				StructureDefinitions.get().addFolder(ext.getDefinitionsFolder(), ((BaseExtension) ext).getManifest().name);
			
			//Field datatypes need to be loaded before Structures are loaded.
			Types.initNewTypeFields();
			
			Images.get().load(new File(Locations.getGameLocation(getGame()), "extras"));
			Structures.get().load(new File(getExtrasFolder(), "data"));
			
			//This is how extras are loaded, because they often rely on things that haven't been loaded
			//yet when they're read in from XML files.
			Types.initNewTypeMethods();
			
			Blocks.addDesignModeBlocks();
			Types.finishInit();
			
			initialized = true;
		}
		catch(MissingStructureDefinitionException ex)
		{
			showMessageDialog
			(
				"Couldn't initialize Data Structures Extension",
				"Error: " + ex.getMessage() + "<br><br>" +
				"If this type is defined in another extension, enable that extension to continue.\n"
			);
			
			log.error(ex.getMessage(), ex);
			
			tryToReloadForNewExtensions = true;
		}
		catch(Exception ex)
		{
			showMessageDialog
			(
				"Couldn't initialize Data Structures Extension",
				"Error: " + ex.getMessage()
			);
			
			log.error(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void onGameWithDataClosed()
	{
		MainPage.disposePages();
		StructureDefinitions.dispose();
		StructureCondition.dispose();
		Types.dispose();
		Images.dispose();
		Structures.dispose();
		Blocks.dispose();
		
		initialized = false;
	}
	
	public void reloadGame()
	{
		onGameWithDataClosed();
		onGameWithDataOpened();
	}
	
	@Override
	public DefinitionMap getDesignModeBlocks()
	{
		return Blocks.tagCache;
	}
	
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

	@Override
	public void onInstall()
	{
	}

	@Override
	public void onUninstall()
	{
//		SW.get().getExtensionManager().getActiveExtensions().removeListener(extListener);
//		SW.get().getExtensionManager().getActiveGameExtensions().removeListener(gameExtListener);
	}
}