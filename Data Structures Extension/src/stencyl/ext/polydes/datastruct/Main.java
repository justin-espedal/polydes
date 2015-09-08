package stencyl.ext.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.common.ext.GameExtension;
import stencyl.ext.polydes.datastruct.data.core.Images;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.data.structure.Structures.MissingStructureDefinitionException;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ext.DataStructureExtension;
import stencyl.ext.polydes.datastruct.ext.DataTypeExtension;
import stencyl.ext.polydes.datastruct.io.HXGenerator;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.editors.snippet.designer.Definitions.DefinitionMap;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class Main extends GameExtension
{
	private static Main instance;
	
	public ArrayList<DataTypeExtension> dataTypeExtensions;
	public ArrayList<DataStructureExtension> dataStructureExtensions;
	
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
		
		name = "Data Structures Extension";
		description = "Create and Manage Data Structures.";
		authorName = "Justin Espedal";
		website = "https://github.com/justin-espedal/polydes";
		internalVersion = 3;
		version = "1.2.0";
		
		isInMenu = true;
		menuName = "Data Structures";
		
		isInGameCenter = true;
		gameCenterName = "Data Structures";
		
		initialized = false;
	}
	
	public static Main get()
	{
		return instance;
	}
	
	@Override
	public void extensionsReady()
	{
		dataTypeExtensions = new ArrayList<DataTypeExtension>();
		dataStructureExtensions = new ArrayList<DataStructureExtension>();
		
		for(BaseExtension e : ExtensionManager.get().getExtensions().values())
		{
			if(e instanceof GameExtension)
				continue;
			
			addExtension(e);
		}
	}
	
	public void addExtension(BaseExtension e)
	{
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
		
//		if(e.getClassname().equals("ExtrasManagerExtension"))
//		{
//			try
//			{
//				MethodUtils.invokeMethod(e, "requestFolderOwnership", this, dataFolderName);
//			}
//			catch (NoSuchMethodException e1)
//			{
//				e1.printStackTrace();
//			}
//			catch (IllegalAccessException e1)
//			{
//				e1.printStackTrace();
//			}
//			catch (InvocationTargetException e1)
//			{
//				e1.printStackTrace();
//			}
//		}
		
		if(initialized)
		{
			if(dtExt != null)
				for(DataType<?> type : dtExt.getDataTypes())
					Types.addType(type);
				
			if(dsExt != null)
				StructureDefinitions.get().addFolder(dsExt.getDefinitionsFolder(), e.getName());
			
			Types.initNewTypeFields();
			Types.initNewTypeMethods();
			Types.finishInit();
		}
		else if(tryToReloadForNewExtensions)
		{
			tryToReloadForNewExtensions = false;
			reloadGame(Game.getGame());
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
	public JPanel getMainPage()
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
		
	}
	
	private static String sourceDir;
	
	@Override
	public void onGameWithDataSaved(Game game)
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
					try{FileUtils.deleteDirectory(getExtrasFolder());}catch(IOException e){e.printStackTrace();}
					getExtrasFolder().mkdirs();
					try{FileUtils.copyDirectory(temp, getExtrasFolder());}catch(IOException e){e.printStackTrace();}
				}
			}
			
			Main.sourceDir = Locations.getPath(Locations.getHXProjectDir(game), "Source");
			
			File out = new File(Locations.getPath(Locations.getHXProjectDir(game), "Assets", "data"), "MyDataStructures.txt");
			Text.writeLines(out, HXGenerator.generateFileList());
			
			Prefs.save();
		}
	}
	
	@Override
	public void onGameBuild(Game game)
	{
		if(initialized)
		{
			write("scripts.ds.DataStructure", HXGenerator.generateDataStructure());
			write("scripts.DataStructures", HXGenerator.generateAccessFile());
			write("scripts.ds.DataStructureReader", HXGenerator.generateReader());
			write("scripts.ds.StringData", HXGenerator.generateEncoder());
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
	public boolean isInstalledForGame(Game game)
	{
		return
				//v2 installation
				new File(Locations.getGameLocation(game) + "extras/[ext] data structures").exists() ||
				//v3+ installation
				(getDataFolder().exists() && getExtrasFolder().exists());
	}
	
	@Override
	public void onInstalledForGame(Game game)
	{
		File extrasFolder = getExtrasFolder();
		extrasFolder.mkdirs();
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
		if(fromVersion <= 2)
		{
			File oldExtrasFolder = new File(Locations.getGameLocation(game) + "extras/[ext] data structures");
			File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
			File oldExtrasDataFolder = new File(oldExtrasFolder, "data");
			
			File dataFolder = getDataFolder();
			File extrasFolder = getExtrasFolder();
			extrasFolder.mkdirs();
			
			FileHelper.copyDirectory(oldExtrasDataFolder, extrasFolder);
			FileHelper.copyDirectory(oldExtrasDefsFolder, dataFolder);
		}
	}
	
	@Override
	public void onGameWithDataOpened(Game game)
	{
		try
		{
			File gameExtrasFolder = getExtrasFolder();
			File gameDataFolder = getDataFolder();
			
			//Add all Types
			Types.addBasicTypes();
			
			for(DataTypeExtension ext : dataTypeExtensions)
				for(DataType<?> type : ext.getDataTypes())
					Types.addType(type);
			
			StructureDefinitions.get().addFolder(gameDataFolder, "My Structures");
			for(DataStructureExtension ext : dataStructureExtensions)
				StructureDefinitions.get().addFolder(ext.getDefinitionsFolder(), ((BaseExtension) ext).getName());
			
			//Field datatypes need to be loaded before Structures are loaded.
			Types.initNewTypeFields();
			
			Images.get().load(new File(Locations.getGameLocation(game), "extras"));
			Structures.get().load(gameExtrasFolder);
			
			//This is how extras are loaded, because they often rely on things that haven't been loaded
			//yet when they're read in from XML files.
			Types.initNewTypeMethods();
			
			Blocks.addDesignModeBlocks();
			Types.finishInit();
			
			initialized = true;
		}
		catch(MissingStructureDefinitionException ex)
		{
			showMessage
			(
				"Couldn't initialize Data Structures Extension",
				"Error: " + ex.getMessage() + "<br><br>" +
				"If this type is defined in another extension, enable that extension to continue.\n"
			);
			
			tryToReloadForNewExtensions = true;
		}
		catch(Exception ex)
		{
			showMessage
			(
				"Couldn't initialize Data Structures Extension",
				"Error: " + ex.getMessage()
			);
		}
	}
	
	@Override
	public void onGameWithDataClosed(Game game)
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
	
	public void reloadGame(Game game)
	{
		onGameWithDataClosed(game);
		onGameWithDataOpened(game);
	}
	
	@Override
	public DefinitionMap getDesignModeBlocks()
	{
		return Blocks.tagCache;
	}
	
	@Override
	public OptionsPanel onOptions()
	{
		System.out.println("SampleExtension : Options");
		
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

	@Override
	public void onInstall()
	{
	}

	@Override
	public void onUninstall()
	{
	}
}