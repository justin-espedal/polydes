package com.polydes.datastruct.updates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.general.StructureType;
import com.polydes.datastruct.io.Text;

import stencyl.sw.util.FileHelper;

public class TypenameUpdater
{
	private static final Logger log = Logger.getLogger(TypenameUpdater.class);
	
	HashMap<String, String> typeBackMap = new HashMap<>();
	
	public void addTypes(HashMap<String, String> types)
	{
		typeBackMap.putAll(types);
	}
	
	public void addType(String old, String newType)
	{
		typeBackMap.put(old, newType);
	}
	
	public void convert()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		File data = new File(dse.getExtrasFolder(), "data");
		File defs = new File(dse.getDataFolder(), "defs");
		
		if(dse.isInitialized())
		{
			for(Entry<String, String> entry : typeBackMap.entrySet())
			{
				String oldTypeName = entry.getKey();
				String newTypeName = entry.getValue();
				DataType<?> loadedType = Types.fromXML(newTypeName);
				
				//move any data that was loaded into unknown structure definitions into new definitions
				if(StructureDefinitions.defMap.containsKey(oldTypeName))
				{
					StructureDefinition unknown = StructureDefinitions.defMap.get(oldTypeName);
					StructureDefinition known = ((StructureType) loadedType).def;
					
					ArrayList<Structure> oldList = Structures.structures.remove(unknown);
					ArrayList<Structure> newList = Structures.structures.get(known);
					
					for(Structure s : oldList)
					{
						s.realizeTemplate(known);
						newList.add(s);
					}
					StructureDefinitions.defMap.remove(oldTypeName);
					Structure.removeType(unknown);
				}
			}
		}
		else
		{
			for(File xmlFile : FileHelper.listFiles(defs, "xml"))
			{
				try
				{
					Document doc = FileHelper.readXMLFromFile(xmlFile);
					applyToDocument(doc);
					FileHelper.writeXMLToFile(doc, xmlFile);
				}
				catch (IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
			for(File dataFile : FileHelper.listFiles(data, ""))
			{
				if(dataFile.getName().endsWith(".txt"))
					continue;
				
				applyToData(dataFile);
			}
		}
	}
	
	public void applyToDocument(Document doc)
	{
		NodeList nl = doc.getElementsByTagName("field");
		for(int i = 0; i < nl.getLength(); ++i)
		{
			Element e = (Element) nl.item(i);
			String type = e.getAttribute("type");
			e.setAttribute("type", Lang.or(typeBackMap.get(type), type));
		}
	}
	
	public void applyToData(File dataFile)
	{
		HashMap<String, String> props = Text.readKeyValues(dataFile);
		String type = props.get("struct_type");
		props.put("struct_type", Lang.or(typeBackMap.get(type), type));
		Text.writeKeyValues(dataFile, props);
	}
}
