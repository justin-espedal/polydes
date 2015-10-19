package com.polydes.datastruct.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.sw.util.Locations;

import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;

public class HXGenerator
{
	public static List<String> generateFileList(File dir)
	{
		List<String> toWrite = new ArrayList<String>();
		
		for(File f : dir.listFiles())
			addFile("", f, toWrite);
		
		return toWrite;
	}
	
	public static void addFile(String prepend, File file, List<String> toWrite)
	{
		if(file.isDirectory())
		{
			prepend += file.getName() + "/";
			for(File f : file.listFiles())
				addFile(prepend, f, toWrite);
		}
		else
		{
			if(!file.getName().equals("folderinfo.txt"))
				toWrite.add(prepend + file.getName());
		}
	}
	
	public static List<String> generateReader()
	{
		File f = new File(Locations.getGameExtensionLocation("com.polydes.datastruct"), "templates/DataStructureReader.hx");
		String s = Text.readString(f);
		
		String imports = "";
		String classmap = "";
		String datatypes = "";
		for(StructureDefinition def : StructureDefinitions.defMap.values())
		{
			imports += String.format("import %s;%s", def.getClassname(), IOUtils.LINE_SEPARATOR_WINDOWS);
			classmap += String.format("\t\tclassmap.set(\"%s\", Type.resolveClass(\"%s\"));%s", def.getName(), def.getClassname(), IOUtils.LINE_SEPARATOR_WINDOWS);
			datatypes += String.format("\t\tDataStructures.types.set(\"%s\", \"\");%s", def.getClassname(), IOUtils.LINE_SEPARATOR_WINDOWS);
		}
		
		s = StringUtils.replace(s, "[IMPORTS]", imports, 1);
		s = StringUtils.replace(s, "[CLASSMAP]", classmap, 1);
		s = StringUtils.replace(s, "[DATATYPES]", datatypes, 1);
		
		String readers = "";
		
		for(DataType<?> type : Types.typeFromXML.values())
		{
			List<String> lines = type.generateHaxeReader();
			if(lines != null)
				for(String line : lines)
					readers += line + IOUtils.LINE_SEPARATOR_WINDOWS;
		}
		
		s = StringUtils.replace(s, "[READERS]", readers, 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split(IOUtils.LINE_SEPARATOR_WINDOWS))
			lines.add(s2);
		
		return lines;
	}
}