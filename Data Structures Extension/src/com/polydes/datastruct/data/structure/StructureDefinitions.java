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

import com.polydes.common.data.types.Types;
import com.polydes.common.ext.ObjectRegistry;
import com.polydes.common.io.XML;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.folder.FolderPolicy;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.data.types.haxe.StructureHaxeType;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.io.read.StructureDefinitionReader;
import com.polydes.datastruct.io.write.StructureDefinitionWriter;
import com.polydes.datastruct.res.Resources;

import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class StructureDefinitions extends ObjectRegistry<StructureDefinition>
{
	public Folder root;
	private HashMap<Folder, File> baseFolders;
	
	public StructureDefinitions()
	{
		root = new Folder("Structure Definitions");
		baseFolders = new HashMap<Folder, File>();
		
		FolderPolicy policy = new FolderPolicy()
		{
			@Override
			public boolean canAcceptItem(Folder folder, DefaultLeaf item)
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
	
	//If the definition already exists as an unknown definition,
	//it will simply have its data set
	public StructureDefinition loadDefinition(File fsfile)
	{
		String fname = fsfile.getName();
		
		if(!fname.endsWith(".xml"))
			return null;
		
		String defname = fname.substring(0, fname.length() - 4);
		Element structure = XML.getFile(fsfile.getAbsolutePath());
		String classname = structure.getAttribute("classname");
		
		StructureDefinition def = isUnknown(classname) ?
			getItem(classname) :
			new StructureDefinition(defname, classname);
		if(def.isUnknown())
			def.realize(defname, classname);
		
		StructureDefinitionReader.read(structure, def);
		
		File parent = fsfile.getParentFile();
		
		File haxeFile = new File(parent, defname + ".hx");
		if(haxeFile.exists())
			def.customCode = Text.readString(haxeFile);
		
		try
		{
			def.setImage(ImageIO.read(new File(parent, defname + ".png")));
		}
		catch (IOException e)
		{
			System.out.println("Couldn't load icon for Structure Definition " + def.getName());
		}
		
		registerItem(def);
		
		return def;
	}
	
	@Override
	public StructureDefinition generatePlaceholder(String key)
	{
		StructureDefinition def = StructureDefinition.newUnknown(key);
		try
		{
			def.setImage(ImageIO.read(Resources.getUrl("question-32.png")));
		}
		catch (IOException e){}
		return def;
	}
	
	private void registerWithLists(StructureDefinition def)
	{
		if(Structures.structures.containsKey(def))
			return;
		
		Structures.structures.put(def, new ArrayList<Structure>());
		
		StructureType structureType = new StructureType(def);
		StructureHaxeType newHaxeType = new StructureHaxeType(structureType);
		
		Types.get().registerItem(structureType);
		DataStructuresExtension.get().getHaxeTypes().registerItem(newHaxeType);
	}
	
	@Override
	protected void preregisterItem(StructureDefinition def)
	{
		registerWithLists(def);
		super.preregisterItem(def);
	}
	
	@Override
	public void registerItem(StructureDefinition def)
	{
		registerWithLists(def);
		super.registerItem(def);
	}
	
	@Override
	public void unregisterItem(StructureDefinition def)
	{
		super.unregisterItem(def);
		DataStructuresExtension.get().getHaxeTypes().unregisterItem(def.getKey());
		Types.get().unregisterItem(def.getKey());
		
		Structures.structures.remove(def);
		def.dispose();
	}
	
	@Override
	public void renameItem(StructureDefinition value, String newName)
	{
		String oldKey = value.getKey();
		
		super.renameItem(value, newName);
		DataStructuresExtension.get().getHaxeTypes().renameItem(oldKey, newName);
		Types.get().renameItem(oldKey, newName);
	}
	
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
				
				for(DefaultLeaf d : dsfolder.getItems())
					save(d, temp);
				
				FileUtils.deleteDirectory(fsfolder);
				fsfolder.mkdirs();
				FileUtils.copyDirectory(temp, fsfolder);
			}
		}
		root.setDirty(false);
	}
	
	public void save(DefaultLeaf item, File file) throws IOException
	{
		if(item instanceof DefaultBranch)
		{
			File saveDir = new File(file, item.getName());
			if(!saveDir.exists())
				saveDir.mkdirs();
			
			for(DefaultLeaf d : ((DefaultBranch) item).getItems())
				save(d, saveDir);
		}
		else
		{
			StructureDefinition def = (StructureDefinition) item.getUserData();
			
			Document doc = FileHelper.newDocument();
			Element e = doc.createElement("structure");
			StructureDefinitionWriter.write(doc, e, def);
			doc.appendChild(e);
			FileHelper.writeXMLToFile(doc, new File(file, def.getName() + ".xml"));
			if(def.getIconImg() != null)
				ImageIO.write(def.getIconImg(), "png", new File(file, def.getName() + ".png"));
			if(!def.customCode.isEmpty())
				FileUtils.writeStringToFile(new File(file, def.getName() + ".hx"), def.customCode);
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		baseFolders.clear();
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
		public boolean canAcceptItem(Folder folder, DefaultLeaf item)
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