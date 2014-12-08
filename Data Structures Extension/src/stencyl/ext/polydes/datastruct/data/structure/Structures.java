package stencyl.ext.polydes.datastruct.data.structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.io.FolderInfo;
import stencyl.ext.polydes.datastruct.io.Text;

public class Structures
{
	private static int nextID = 0;
	private static Structures _instance;
	public static StructureFolder root;
	
	private Structures()
	{
		root = new StructureFolder("Structures");
	}
	
	public static Structures get()
	{
		if(_instance == null)
			_instance = new Structures();
		
		return _instance;
	}
	
	//Loading is done in two passes.
	
	//Lightload: The first pass just gets id and type of each object
	//Maps are populated with this data
	
	//Deepload: The second pass reads in the key-value pairs, which might include refs to other structures.
	
	//TODO: The two-pass loading mechanism can be replaced by a promise system.
	
	private HashMap<String, HashMap<String, String>> fmaps;
	
	public void load(File folder)
	{
		fmaps = new HashMap<String, HashMap<String,String>>();
		
		FolderInfo info = new FolderInfo(folder);
		ArrayList<String> order = info.getFileOrder();
		for(String fname : order)
		{
			File file = new File(folder, fname);
			lightload(file);
		}
		for(String fname : order)
		{
			File file = new File(folder, fname);
			deepload(root, file);
		}
		order.clear();
		info.clear();
		root.setDirty(false);
		
		fmaps.clear();
		fmaps = null;
	}
	
	public void lightload(File file)
	{
		if(!file.exists())
			return;
		
		if(file.isDirectory())
		{
			for(String fname : file.list())
				if(!fname.equals(FolderInfo.FOLDER_INFO_FILENAME))
					lightload(new File(file, fname));
		}
		else
		{
			HashMap<String, String> map = Text.readKeyValues(file);
			fmaps.put(file.getAbsolutePath(), map);
			
			String name = file.getName();
			int id = Integer.parseInt(map.get("struct_id"));
			String type = map.remove("struct_type");
			nextID = Math.max(nextID, id + 1);
			
			Structure model = new Structure(id, name, StructureDefinitions.defMap.get(type));
			structures.get(model.getTemplate()).add(model);
			structuresByID.put(model.getID(), model);
		}
	}
	
	public void deepload(StructureFolder folder, File file)
	{
		if(!file.exists())
			return;
		
		if(file.isDirectory())
		{
			StructureFolder newFolder = new StructureFolder(file.getName());
			FolderInfo info = new FolderInfo(file);
			ArrayList<String> order = info.getFileOrder();
			if(info.containsKey("childType"))
				newFolder.childType = StructureDefinitions.defMap.get(info.get("childType"));
			for(String fname : order)
				deepload(newFolder, new File(file, fname));
			folder.addItem(newFolder);
		}
		else
		{
			HashMap<String, String> map = fmaps.get(file.getAbsolutePath());
			Structure model = structuresByID.get(Integer.parseInt(map.remove("struct_id")));
			
			for(String key : map.keySet())
			{
				StructureField f = model.getField(key);
				if(f == null)
					continue;
				model.setPropertyFromString(f, map.get(key));
				model.setPropertyEnabled(f, true);
			}
			
			folder.addItem(model.dref);
		}
	}
	
	public void saveChanges(File file) throws IOException
	{
		if(root.isDirty())
		{
			FolderInfo info = new FolderInfo();
			
			for(Leaf<DataItem> d : root.getItems())
			{
				save(d, file);
				info.addFilenameToOrder(d.getName());
			}
			
			info.writeToFolder(file);
			info.clear();
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
			
			FolderInfo info = new FolderInfo();
			
			for(Leaf<DataItem> d : ((Folder) item).getItems())
			{
				save(d, saveDir);
				info.addFilenameToOrder(d.getName());
			}
			
			if(((StructureFolder) item).childType != null)
				info.put("childType", ((StructureFolder) item).childType.getName());
			
			info.writeToFolder(saveDir);
			info.clear();
		}
		else
		{
			Structure s = (Structure) ((DataItem) item).getObject();
			ArrayList<String> toWrite = new ArrayList<String>();
			toWrite.add("struct_id=" + s.getID());
			toWrite.add("struct_type=" + s.getDefname());
			for(StructureField field : s.getFields())
			{
				if(field.isOptional() && !s.isPropertyEnabled(field))
					continue;
				toWrite.add(field.getVarname() + "=" + field.getType().checkEncode(s.getProperty(field)));
			}
			FileUtils.writeLines(new File(file, item.getName()), toWrite);
		}
	}

	public static HashMap<StructureDefinition, ArrayList<Structure>> structures = new HashMap<StructureDefinition, ArrayList<Structure>>();
	public static HashMap<Integer, Structure> structuresByID = new HashMap<Integer, Structure>();
	
	public static Collection<Structure> getList(StructureDefinition type)
	{
		return structures.get(type);
	}
	
	public static Structure getStructure(int i)
	{
		return structuresByID.get(i);
	}

	public static int newID()
	{
		return nextID++;
	}

	public static void dispose()
	{
		for(StructureDefinition key : structures.keySet())
			for(Structure s : structures.get(key))
				s.dispose();
		structures.clear();
		structuresByID.clear();
		_instance = null;
		root = null;
		nextID = 0;
	}
}
