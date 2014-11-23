package stencyl.ext.polydes.datastruct.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.Main;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.res.Resources;

public class HXGenerator
{
	public static List<String> generateFileList()
	{
		List<String> toWrite = new ArrayList<String>();
		
		if(Main.dataFolder != null)
			for(File f : Main.dataFolder.listFiles())
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
	
	public static List<String> generateDataStructure()
	{
		return Text.readLines(Resources.getUrlStream("code/DataStructure.hx"));
	}
	
	public static List<String> generateReader()
	{
		String s = Text.readString(Resources.getUrlStream("code/DataStructureReader.hx"));
		
		String imports = "";
		String classmap = "";
		String datatypes = "";
		for(StructureDefinition def : StructureDefinitions.defMap.values())
		{
			imports += String.format("import %s;%s", def.classname, IOUtils.LINE_SEPARATOR_WINDOWS);
			classmap += String.format("\t\tclassmap.set(\"%s\", Type.resolveClass(\"%s\"));%s", def.name, def.classname, IOUtils.LINE_SEPARATOR_WINDOWS);
			datatypes += String.format("\t\tDataStructures.types.set(\"%s\", \"\");%s", def.classname, IOUtils.LINE_SEPARATOR_WINDOWS);
		}
		
		s = StringUtils.replace(s, "[IMPORTS]", imports, 1);
		s = StringUtils.replace(s, "[CLASSMAP]", classmap, 1);
		s = StringUtils.replace(s, "[DATATYPES]", datatypes, 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split(IOUtils.LINE_SEPARATOR_WINDOWS))
			lines.add(s2);
		
		return lines;
	}
	
	public static List<String> generateAccessFile()
	{
		return Text.readLines(Resources.getUrlStream("code/DataStructures.hx"));
	}
	
	public static List<String> generateEncoder()
	{
		String s = Text.readString(Resources.getUrlStream("code/StringData.hx"));
		
		String datareaders = "";
		
		for(DataType<?> type : Types.typeFromXML.values())
		{
			List<String> lines = type.generateHaxeReader();
			if(lines != null)
			for(String line : lines)
				datareaders += line + IOUtils.LINE_SEPARATOR_WINDOWS;
		}
		
		s = StringUtils.replace(s, "[DATAREADERS]", datareaders, 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split(IOUtils.LINE_SEPARATOR_WINDOWS))
			lines.add(s2);
		
		return lines;
	}
}