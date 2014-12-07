package stencyl.ext.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.datastruct.data.core.Images;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ext.DataStructureExtension;
import stencyl.ext.polydes.datastruct.ext.DataTypeExtension;
import stencyl.ext.polydes.datastruct.io.HXGenerator;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.utils.DelayedInitialize;
import stencyl.sw.app.ExtensionManager;
import stencyl.sw.editors.snippet.designer.Definition;
import stencyl.sw.editors.snippet.designer.Definitions;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;

public class Main extends BaseExtension
{
	private static Main instance;
	
	private static String dataFolderName = "[ext] data structures";
	
	public static File extFolder;
	public static File defsFolder;
	public static File dataFolder;
	
	public ArrayList<DataTypeExtension> dataTypeExtensions;
	public ArrayList<DataStructureExtension> dataStructureExtensions;
	
	public static boolean forceUpdateData = false;
	
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	@Override
	public void onStartup()
	{
		icon = Resources.loadIcon("icon.png");
		classname = this.getClass().getName();
		String loc = Locations.getExtensionPrefsLocation(classname);
		if(new File(loc).exists())
			Loader.readLocalDictionary(loc, properties);
		
		instance = this;
		
		name = "Data Structures Extension";
		description = "Create and Manage Data Structures.";
		authorName = "Justin Espedal";
		website = "http://dialog.justin.espedaladventures.com/";
		internalVersion = 2;
		version = "1.1.0";
		
		isInMenu = true;
		menuName = "Data Structures";
		
		isInGameCenter = true;
		gameCenterName = "Data Structures";
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
			if(e instanceof DataTypeExtension)
				dataTypeExtensions.add((DataTypeExtension) e);
			if(e instanceof DataStructureExtension)
				dataStructureExtensions.add((DataStructureExtension) e);
			
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
		
	}
	
	private static String sourceDir;
	
	@Override
	public void onGameSave(Game game)
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
			if(dataFolder == null)
				return;
			
			File temp = new File(Locations.getTemporaryDirectory() + File.separator + "data structures save");
			temp.mkdirs();
			boolean failedToSave = false;
			
			try{FileUtils.deleteDirectory(temp);}catch(IOException e){e.printStackTrace();}
				temp.mkdirs();
			try{Structures.get().saveChanges(temp);}catch(IOException e){failedToSave = true; e.printStackTrace();}
			if(!failedToSave)
			{
				try{FileUtils.deleteDirectory(dataFolder);}catch(IOException e){e.printStackTrace();}
				dataFolder.mkdirs();
				try{FileUtils.copyDirectory(temp, dataFolder);}catch(IOException e){e.printStackTrace();}
			}
		}
		
		Main.sourceDir = Locations.getPath(Locations.getHXProjectDir(game), "Source");
		
		File out = new File(Locations.getPath(Locations.getHXProjectDir(game), "Assets", "data"), "MyDataStructures.txt");
		Text.writeLines(out, HXGenerator.generateFileList());
		
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
		
		Prefs.save();
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
	public void onGameOpened(Game game)
	{
		extFolder = openFolder(Locations.getGameLocation(game) + "extras" + File.separator + dataFolderName + File.separator);
		defsFolder = openFolder(extFolder, "defs" + File.separator);
		dataFolder = openFolder(extFolder, "data" + File.separator);
		
		//Add all Types
		
		Types.addBasicTypes();
		
		for(DataTypeExtension ext : dataTypeExtensions)
			for(DataType<?> type : ext.getDataTypes())
				Types.addType(type);
		
		StructureDefinitions.get().addFolder(defsFolder, "My Structures");
		for(DataStructureExtension ext : dataStructureExtensions)
			StructureDefinitions.get().addFolder(ext.getDefinitionsFolder(), ((BaseExtension) ext).getName());
		
		//Field datatypes need to be loaded before Structures are loaded.
		for(DataType<?> type : Types.typeFromXML.values())
			DelayedInitialize.initPropPartial(type.xml, type, DelayedInitialize.CALL_FIELDS);
		
		Images.get().load(new File(Locations.getGameLocation(game), "extras"));
		Structures.get().load(dataFolder);
		
		//This is how extras are loaded, because they often rely on things that haven't been loaded
		//yet when they're read in from XML files.
		for(DataType<?> type : Types.typeFromXML.values())
			DelayedInitialize.initPropPartial(type.xml, type, DelayedInitialize.CALL_METHODS);
		DelayedInitialize.clearProps();
		
		Blocks.addDesignModeBlocks();
	}
	
	public File openFolder(File f, String s)
	{
		return openFolder(new File(f, s));
	}
	
	public File openFolder(String s)
	{
		return openFolder(new File(s));
	}
	
	public File openFolder(File f)
	{
		if(!f.exists())
			f.mkdirs();
		return f;
	}

	@Override
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);
		
		extFolder = null;
		defsFolder = null;
		dataFolder = null;
		
		MainPage.disposePages();
		StructureDefinitions.dispose();
		StructureCondition.dispose();
		Types.dispose();
		Images.dispose();
		Structures.dispose();
		Blocks.dispose();
	}
	
	@Override
	public ArrayList<Definition> getDesignModeBlocks()
	{
		ArrayList<Definition> defs = new ArrayList<Definition>();
		
		for(String tag : Blocks.tagCache)
		{
			defs.add(Definitions.get().get(tag));
		}
		
		return defs;
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
	
	public String readInternalData()
	{
		return super.readData();
	}
	
	public void writeInternalData(String data)
	{
		saveData(data);
	}
}