package com.polydes.datastruct.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Text
{
	private static HashMap<File, FileOutputStream> outstreams = new HashMap<File, FileOutputStream>();
	private static HashMap<File, OutputStreamWriter> writers = new HashMap<File, OutputStreamWriter>();
	
	public static List<String> readLines(File file)
	{
		try
		{
			return FileUtils.readLines(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	public static List<String> readLines(InputStream stream)
	{
		try
		{
			return IOUtils.readLines(stream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	public static String readString(File file)
	{
		try
		{
			return FileUtils.readFileToString(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public static String readString(InputStream stream)
	{
		try
		{
			return IOUtils.toString(stream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public static HashMap<String, String> readKeyValues(File file)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(String s : readLines(file))
		{
			if(s.indexOf("=") == -1)
				continue;
			
			String[] parts = s.split("=");
			map.put(parts[0], parts.length > 1 ? parts[1] : "");
		}
		
		return map;
	}
	
	public static String sectionMark = ">";
	public static final String folderStartMark = ">>";
	public static final String folderEndMark = "<<";
	
	public static class TextObject
	{
		public String name;
		
		public TextObject(String name)
		{
			this.name = name;
		}
	}
	
	public static class TextFolder extends TextObject
	{
		public LinkedHashMap<String, TextObject> parts;
		
		public TextFolder(String name)
		{
			super(name);
			parts = new LinkedHashMap<String, Text.TextObject>();
		}
		
		public void add(TextObject object)
		{
			parts.put(object.name, object);
		}
	}
	
	public static class TextSection extends TextObject
	{
		public ArrayList<String> parts;
		
		public TextSection(String name)
		{
			super(name);
			parts = new ArrayList<String>();
		}
		
		public void add(String line)
		{
			parts.add(line);
		}
	}
	
	public static TextFolder readSectionedText(File file, String sectionMark)
	{
		Text.sectionMark = sectionMark;
		TextFolder toReturn = new TextFolder("root");
		TextSection section = null;
		
		Stack<TextFolder> folderStack = new Stack<TextFolder>();
		folderStack.push(toReturn);
		
		for(String line : readLines(file))
		{
			if(line.startsWith(folderStartMark))
			{
				folderStack.push(new TextFolder(line.substring(folderStartMark.length())));
			}
			else if(line.startsWith(folderEndMark))
			{
				TextFolder newFolder = folderStack.pop();
				folderStack.peek().add(newFolder);
			}
			else if(line.startsWith(sectionMark))
			{
				section = new TextSection(line.substring(sectionMark.length()));
				folderStack.peek().add(section);
			}
			else if(section != null && line.trim().length() != 0)
				section.add(line);
		}
		
		return toReturn;
	}
	
	public static void writeSectionedText(File file, TextFolder folder, String sectionMark)
	{
		Text.sectionMark = sectionMark;
		ArrayList<String> toWrite = new ArrayList<String>();
		for(TextObject o : folder.parts.values())
			addSection(toWrite, o);
		writeLines(file, toWrite);
	}
	
	public static void addSection(ArrayList<String> lines, TextObject object)
	{
		if(object instanceof TextFolder)
		{
			TextFolder folder = (TextFolder) object;
			
			lines.add(folderStartMark + folder.name);
			for(String key : folder.parts.keySet())
				addSection(lines, folder.parts.get(key));
			lines.add(folderEndMark);
		}
		else
		{
			TextSection section = (TextSection) object;
			
			lines.add(sectionMark + section.name);
			lines.add("");
			lines.addAll(section.parts);
			lines.add("");
		}
	}
	
	public static void writeLines(File file, Collection<String> lines)
	{
		try
		{
			FileUtils.writeLines(file, lines);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void startWriting(File file)
	{
		FileOutputStream os = null;
		try
		{
			os = new FileOutputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		outstreams.put(file, os);
		writers.put(file, new OutputStreamWriter(os, Charset.forName("UTF-8")));
	}
	
	public static void writeLine(File file, String s)
	{
		try
		{
			writers.get(file).write(s + "\n");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void closeOutput(File file)
	{
		try
		{
			writers.get(file).close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		writers.remove(file);
		outstreams.remove(file);
	}
}
