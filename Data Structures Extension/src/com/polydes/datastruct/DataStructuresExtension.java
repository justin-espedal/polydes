package com.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.polydes.common.data.types.Types;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.io.HXGenerator;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.updates.V3_GameExtensionUpdate;
import com.polydes.datastruct.updates.V4_FullTypeNamesUpdate;

import stencyl.core.lib.Game;
import stencyl.sw.editors.snippet.designer.Definitions.DefinitionMap;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.Locations;

public class DataStructuresExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DataStructuresExtension.class);
	
	private static DataStructuresExtension instance;
	
	public SDETypes sdeTypes;
	public HaxeTypes haxeTypes;
	public StructureDefinitions structureDefinitions;
	
	public static boolean forceUpdateData = false;
	private boolean initialized = false;
	
	public static DataStructuresExtension get()
	{
		return instance;
	}
	
	public SDETypes getSdeTypes()
	{
		return sdeTypes;
	}
	
	public HaxeTypes getHaxeTypes()
	{
		return haxeTypes;
	}
	
	public StructureDefinitions getStructureDefinitions()
	{
		return structureDefinitions;
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
	}
	
	public boolean isInitialized()
	{
		return initialized;
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
	}
	
	private static String sourceDir;
	
	@Override
	public void onGameWithDataSaved()
	{
		if(initialized)
		{
			try
			{
				DataStructuresExtension.get().getStructureDefinitions().saveChanges();
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
			for(HaxeDataType type : haxeTypes.values())
			{
				List<String> lines = type.generateHaxeClass();
				if(lines != null)
					write(type.getHaxeType(), lines);
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
			DSTypes.register();
			
			sdeTypes = new SDETypes();
			haxeTypes = new HaxeTypes();
			structureDefinitions = new StructureDefinitions();
			
			new File(getDataFolder(), "defs").mkdirs();
			DataStructuresExtension.get().getStructureDefinitions().addFolder(new File(getDataFolder(), "defs"), "My Structures");
			
			new File(getExtrasFolder(), "data").mkdirs();
//			Images.get().load(new File(Locations.getGameLocation(getGame()), "extras"));
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
		structureDefinitions.dispose();
		StructureCondition.dispose();
		haxeTypes.dispose();
//		Images.dispose();
		Structures.dispose();
		Blocks.dispose();
		sdeTypes.dispose();
		DSTypes.unregister();
		
		sdeTypes = null;
		haxeTypes = null;
		structureDefinitions = null;
		
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
	}
}