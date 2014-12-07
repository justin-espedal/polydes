package stencyl.ext.polydes.paint.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Text
{
	private static HashMap<File, FileInputStream> instreams = new HashMap<File, FileInputStream>();
	private static HashMap<File, FileOutputStream> outstreams = new HashMap<File, FileOutputStream>();
	private static HashMap<File, BufferedReader> readers = new HashMap<File, BufferedReader>();
	private static HashMap<File, OutputStreamWriter> writers = new HashMap<File, OutputStreamWriter>();

	public static void startReading(File file)
	{
		FileInputStream is = null;
		try
		{
			is = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		instreams.put(file, is);
		readers.put(file, new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))));
	}

	public static String getNextLine(File file)
	{
		try
		{
			return readers.get(file).readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static void closeInput(File file)
	{
		try
		{
			readers.get(file).close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		readers.remove(file);
		instreams.remove(file);
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
