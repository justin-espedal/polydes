package com.polydes.datastruct.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.sw.util.Locations;

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
		for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
		{
			imports += String.format("import %s;\n", def.getFullClassname());
			classmap += String.format("\t\tclassmap.set(\"%s\", Type.resolveClass(\"%s\"));\n", def.getFullClassname(), def.getFullClassname());
			datatypes += String.format("\t\tDataStructures.types.set(\"%s\", \"\");\n", def.getFullClassname());
		}
		
		s = StringUtils.replace(s, "[IMPORTS]", imports, 1);
		s = StringUtils.replace(s, "[CLASSMAP]", classmap, 1);
		s = StringUtils.replace(s, "[DATATYPES]", datatypes, 1);
		
		String readers = "";
		
		for(HaxeDataType type : DataStructuresExtension.get().getHaxeTypes().values())
		{
			List<String> lines = type.generateHaxeReader();
			if(lines != null)
				for(String line : lines)
					readers += line + "\n";
		}
		
		s = StringUtils.replace(s, "[READERS]", readers, 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split("\n"))
			lines.add(s2);
		
		return lines;
	}
}