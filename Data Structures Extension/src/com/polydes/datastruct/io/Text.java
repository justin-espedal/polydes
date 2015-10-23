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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public static void writeKeyValues(File file, Map<String, String> map)
	{
		List<String> lines = new ArrayList<String>();
		
		for(Entry<String, String> entry : map.entrySet())
			lines.add(entry.getKey() + "=" + entry.getValue());
		
		writeLines(file, lines);
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
