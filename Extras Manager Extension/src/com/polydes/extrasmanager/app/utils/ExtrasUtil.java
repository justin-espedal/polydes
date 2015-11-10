package com.polydes.extrasmanager.app.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExtrasUtil
{
	private static HashSet<String> blankSet = new HashSet<String>();
	
	/**
	 * @param toOrder
	 * @param toExclude
	 * @return A list of files, minus OS and hidden files, with directories first and files second.
	 */
	public static List<File> orderFiles(File[] toOrder, HashSet<String> toExclude)
	{
		if(toExclude == null)
			toExclude = blankSet;
		
		ArrayList<File> folders = new ArrayList<File>();
		ArrayList<File> files = new ArrayList<File>();
		
		for(File f : toOrder)
		{
			if(f.isHidden())
				continue;
			if(f.getName().startsWith("."))
				continue;
			if(f.getName().equals("desktop.ini"))
				continue;
			if(toExclude.contains(f.getName()))
				continue;
			
			if(f.isDirectory())
				folders.add(f);
			else
				files.add(f);
		}
		
		folders.addAll(files);
		return folders;
	}
}
