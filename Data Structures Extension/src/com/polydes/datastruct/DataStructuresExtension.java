package com.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.polydes.common.ext.GameExtensionWatcher;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.datastruct.data.core.Images;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.ext.DataStructureExtension;
import com.polydes.datastruct.ext.DataTypeExtension;
import com.polydes.datastruct.ext.StructureDefinitionExtension;
import com.polydes.datastruct.io.HXGenerator;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.updates.V3_GameExtensionUpdate;
import com.polydes.datastruct.updates.V4_FullTypeNamesUpdate;

import stencyl.core.lib.Game;
import stencyl.sw.editors.snippet.designer.Definitions.DefinitionMap;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.Locations;

public class DataStructuresExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DataStructuresExtension.class);
	
	private static DataStructuresExtension instance;
	
	private ExtensionMonitor monitor;
	public ArrayList<DataTypeExtension> dataTypeExtensions = new ArrayList<>();
	public ArrayList<DataStructureExtension> dataStructureExtensions = new ArrayList<>();
	public ArrayList<StructureDefinitionExtension> sdeExtensions = new ArrayList<>();
	
	public static boolean forceUpdateData = false;
	private boolean initialized = false;
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
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
		
		monitor = new ExtensionMonitor();
		monitor.startWatching();
	}
	
	public static DataStructuresExtension get()
	{
		return instance;
	}
	
	private class ExtensionMonitor extends GameExtensionWatcher
	{
		@Override
		public void extensionAdded(BaseExtension e)
		{
			DataTypeExtension dtExt =
				(e instanceof DataTypeExtension) ?
				(DataTypeExtension) e :
				null;
			
			DataStructureExtension dsExt =
				(e instanceof DataStructureExtension) ?
				(DataStructureExtension) e :
				null;
				
			StructureDefinitionExtension sdExt =
				(e instanceof StructureDefinitionExtension) ?
				(StructureDefinitionExtension) e :
				null;
				
			if(dtExt != null)
				dataTypeExtensions.add(dtExt);
			if(dsExt != null)
				dataStructureExtensions.add(dsExt);
			if(sdExt != null)
				sdeExtensions.add(sdExt);
			
			if(initialized)
			{
				if(sdExt != null)
					for(SDEType<?> type : sdExt.getSdeTypes())
						SDETypes.addType(e.getManifest().id, type);
				
				if(dtExt != null)
					for(DataType<?> type : dtExt.getDataTypes())
						Types.addType(type);
					
				if(dsExt != null)
					StructureDefinitions.get().addFolder(dsExt.getDefinitionsFolder(), e.getManifest().name);
				
				Types.resolveChanges();
			}
		}

		@Override
		public void extensionRemoved(BaseExtension e)
		{
			DataTypeExtension dtExt =
				(e instanceof DataTypeExtension) ?
				(DataTypeExtension) e :
				null;
			
			DataStructureExtension dsExt =
				(e instanceof DataStructureExtension) ?
				(DataStructureExtension) e :
				null;
			
			StructureDefinitionExtension sdExt =
				(e instanceof StructureDefinitionExtension) ?
				(StructureDefinitionExtension) e :
				null;
			
			if(dtExt != null)
				dataTypeExtensions.remove(dtExt);
			if(dsExt != null)
				dataStructureExtensions.remove(dsExt);
			if(sdExt != null)
				sdeExtensions.remove(sdExt);
			
			if(initialized)
			{
//				if(sdExt != null)
//					for(SDEType<?> type : sdExt.getSdeTypes())
//						SDETypes.removeType(e.getManifest().id, type);
				
				if(dtExt != null)
					for(DataType<?> type : dtExt.getDataTypes())
						Types.removeType(type);
				
				if(dsExt != null)
					StructureDefinitions.get().removeFolder(dsExt.getDefinitionsFolder());
				
				Types.resolveChanges();
			}
		}
	}
	
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
		
		monitor.stopWatching();
		monitor = null;
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
		}
	}
	
	@Override
	public void onGameBuild(Game game)
	{
		if(initialized)
		{
			sourceDir = Locations.getPath(Locations.getHXProjectDir(getGame()), "Source");
			
			File dataList = new File(Locations.getPath(Locations.getHXProjectDir(getGame()), "Assets", "data"), "MyDataStructures.txt");
			Text.writeLines(dataList, HXGenerator.generateFileList(new File(getExtrasFolder(), "data")));
			
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
		if(fromVersion < 3)
			new V3_GameExtensionUpdate().run();
		if(fromVersion < 4)
			new V4_FullTypeNamesUpdate().run();
		forceUpdateData = true;
	}
	
	@Override
	public void onGameWithDataOpened()
	{
		try
		{
			for(StructureDefinitionExtension sdExt : sdeExtensions)
			{
				String extensionID = ((BaseExtension) sdExt).getManifest().id;
				for(SDEType<?> type : sdExt.getSdeTypes())
					SDETypes.addType(extensionID, type);
			}
			
			//Add all Types
			Types.initialize();
			
			for(DataTypeExtension ext : dataTypeExtensions)
				for(DataType<?> type : ext.getDataTypes())
					Types.addType(type);
			
			StructureDefinitions.get().addFolder(new File(getDataFolder(), "defs"), "My Structures");
			for(DataStructureExtension ext : dataStructureExtensions)
				StructureDefinitions.get().addFolder(ext.getDefinitionsFolder(), ((BaseExtension) ext).getManifest().name);
			
			Images.get().load(new File(Locations.getGameLocation(getGame()), "extras"));
			Structures.get().load(new File(getExtrasFolder(), "data"));
			
			Blocks.addDesignModeBlocks();
			
			initialized = true;
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
		SDETypes.disposeExtended();
		
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
		monitor.stopWatching();
		monitor = null;
	}
}