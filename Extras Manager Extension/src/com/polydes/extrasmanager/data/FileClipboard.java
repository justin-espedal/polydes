package com.polydes.extrasmanager.data;

import java.io.File;
import java.util.ArrayList;

public class FileClipboard
{
	private static ArrayList<File> files = new ArrayList<File>();
	public static ArrayList<Listener> listeners = new ArrayList<Listener>();
	public static int COPY = 0;
	public static int CUT = 1;
	public static int op = COPY;
	
	public static void clear()
	{
		files.clear();
		for(Listener l : listeners)
			l.clipboardContentsUpdated();
	}
	
	public static void add(File f)
	{
		files.add(f);
		for(Listener l : listeners)
			l.clipboardContentsUpdated();
	}
	
	public static ArrayList<File> list()
	{
		return files;
	}
	
	public interface Listener
	{
		public void clipboardContentsUpdated();
	}
}