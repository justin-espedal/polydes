package com.polydes.datastruct.data.structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.folder.FolderPolicy;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.general.StructureType;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.io.XML;

import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class StructureDefinitions
{
	private static StructureDefinitions _instance;
	public static Folder root;
	private static HashMap<Folder, File> baseFolders;
	public static HashMap<String, StructureDefinition> defMap = new HashMap<String, StructureDefinition>();
	
	private StructureDefinitions()
	{
		root = new Folder("Structure Definitions");
		baseFolders = new HashMap<Folder, File>();
		
		FolderPolicy policy = new FolderPolicy()
		{
			@Override
			public boolean canAcceptItem(Folder folder, DataItem item)
			{
				return false;
			}
		};
		policy.folderCreationEnabled = false;
		policy.itemCreationEnabled = false;
		policy.itemEditingEnabled = false;
		policy.itemRemovalEnabled = false;
		root.setPolicy(policy);
	}
	
	public static StructureDefinitions get()
	{
		if(_instance == null)
			_instance = new StructureDefinitions();
		
		return _instance;
	}
	
	public void addFolder(File fsfolder, String name)
	{
		Folder newFolder = new Folder(name);
		newFolder.setPolicy(new UniqueRootPolicy());
		baseFolders.put(newFolder, fsfolder);
		for(File f : FileHelper.listFiles(fsfolder))
			load(f, newFolder);
		root.addItem(newFolder);
		root.setDirty(false);
	}
	
	public void load(File fsfile, Folder dsfolder)
	{
		if(fsfile.isDirectory())
		{
			Folder newFolder = new Folder(fsfile.getName());
			for(File f : fsfile.listFiles())
				load(f, newFolder);
		}
		else
		{
			if(fsfile.getName().endsWith(".xml"))
			{
				dsfolder.addItem(loadDefinition(fsfile).dref);
			}
		}
	}
	
	public static StructureDefinition loadDefinition(File fsfile)
	{
		String fname = fsfile.getName();
		
		if(!fname.endsWith(".xml"))
			return null;
		
		String defname = fname.substring(0, fname.length() - 4);
		
		Element structure = XML.getFile(fsfile.getAbsolutePath());
		StructureDefinition def = new StructureDefinition(defname, structure.getAttribute("classname"));
		XML.readDefinition(structure, def);
		
		File parent = fsfile.getParentFile();
		
		File haxeFile = new File(parent, defname + ".hx");
		if(haxeFile.exists())
			def.customCode = Text.readString(haxeFile);
		else
			def.customCode = "";
		
		try
		{
			def.setImage(ImageIO.read(new File(parent, defname + ".png")));
		}
		catch (IOException e)
		{
			System.out.println("Couldn't load icon for Structure Definition " + def.getName());
		}
		
		addDefinition(def);
		
		return def;
	}
	
	public static void addDefinition(StructureDefinition def)
	{
		defMap.put(def.getName(), def);
		Structures.structures.put(def, new ArrayList<Structure>());
		Types.addType(new StructureType(def));
	}
	
	/*
	public static void addDesignModeBlocks(StructureDefinition def)
	{
		for(StructureField field : def.getFields())
		{
			String spec = String.format("set %s for %s %%0 to %%1", field.label, def.label);
			
			Definition blockDef = new Definition
			(
				Definition.Category.CUSTOM,
				String.format("ds-%s-set-%s", def.name, field.name),
				new Type[] { Definition.Type.OBJECT, Definition.getTypeFromString(Types.fromXML(field.type).stencylType) },
				new BasicCodeMap().setCode(CodeMap.HX, String.format("~.%s = ~;", field.name)),
				spec,
				Block.BlockType.ACTION,
				Definition.Type.VOID
			);
			
			blockDef.guiTemplate = spec;
			blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
			
			Definitions.get().put(blockDef.tag, blockDef);
			tagCache.add(blockDef.tag);
			
			spec = String.format("get %s for %s %%0", field.label, def.label);
			
			blockDef = new Definition
			(
				Definition.Category.CUSTOM,
				String.format("ds-%s-get-%s", def.name, field.name),
				new Type[] { Definition.Type.OBJECT },
				new BasicCodeMap().setCode(CodeMap.HX, String.format("~.%s", field.name)),
				spec,
				Block.BlockType.NORMAL,
				Definition.getTypeFromString(Types.fromXML(field.type).stencylType)
			);
			
			blockDef.guiTemplate = spec;
			blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
			
			Definitions.get().put(blockDef.tag, blockDef);
			tagCache.add(blockDef.tag);
		}
	}
	*/
	
	public void saveChanges() throws IOException
	{
		for(Folder dsfolder : baseFolders.keySet())
		{
			if(dsfolder.isDirty())
			{
				File fsfolder = baseFolders.get(dsfolder);
				File temp = new File(Locations.getTemporaryDirectory() + File.separator + "data structure defs save");
				temp.mkdirs();
				
				FileUtils.deleteDirectory(temp);
				temp.mkdirs();
				
				for(Leaf<DataItem> d : dsfolder.getItems())
					save(d, temp);
				
				FileUtils.deleteDirectory(fsfolder);
				fsfolder.mkdirs();
				FileUtils.copyDirectory(temp, fsfolder);
			}
		}
		root.setDirty(false);
	}
	
	public void save(Leaf<DataItem> item, File file) throws IOException
	{
		if(item instanceof Folder)
		{
			File saveDir = new File(file, item.getName());
			if(!saveDir.exists())
				saveDir.mkdirs();
			
			for(Leaf<DataItem> d : ((Folder) item).getItems())
				save(d, saveDir);
		}
		else
		{
			StructureDefinition def = (StructureDefinition) ((DataItem) item).getObject();
			
			Document doc = FileHelper.newDocument();
			Element e = doc.createElement("structure");
			XML.writeDefinition(doc, e, def);
			doc.appendChild(e);
			FileHelper.writeXMLToFile(doc, new File(file, def.getName() + ".xml"));
			if(def.getIconImg() != null)
				ImageIO.write(def.getIconImg(), "png", new File(file, def.getName() + ".png"));
			if(!def.customCode.isEmpty())
				FileUtils.writeStringToFile(new File(file, def.getName() + ".hx"), def.customCode);
		}
	}
	
	public static void dispose()
	{
		for(StructureDefinition def : defMap.values())
			def.dispose();
		defMap.clear();
		baseFolders.clear();
		_instance = null;
		root = null;
	}
	
	class UniqueRootPolicy extends FolderPolicy
	{
		public UniqueRootPolicy()
		{
			duplicateItemNamesAllowed = false;
			folderCreationEnabled = false;
			itemCreationEnabled = true;
			itemEditingEnabled = true;
			itemRemovalEnabled = true;
		}
		
		@Override
		public boolean canAcceptItem(Folder folder, DataItem item)
		{
			Folder fromFolder = (item instanceof Folder) ?
						(Folder) item :
						(Folder) item.getParent();
			
			boolean sameRoot = (fromFolder.getPolicy() == this);
			
			return super.canAcceptItem(folder, item) && sameRoot;
		}
	}

	public void removeFolder(File fsfolder)
	{
		for(Entry<Folder, File> f : baseFolders.entrySet())
		{
			if(f.getValue().equals(f))
			{
				root.removeItem(f.getKey());
				baseFolders.remove(f.getKey());
			}
		}
	}
}