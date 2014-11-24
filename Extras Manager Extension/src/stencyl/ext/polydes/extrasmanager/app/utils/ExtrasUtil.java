package stencyl.ext.polydes.extrasmanager.app.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExtrasUtil
{
	private static HashSet<String> blankSet = new HashSet<String>();
	
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
	
	public static String getUnusedName(String name, File targetDir)
	{
		String[] nameParts = getNameParts(name);
		
		HashSet<String> fnames = new HashSet<String>();
		for(String s : targetDir.list())
		{
			fnames.add(s);
		}
		if(!fnames.contains(name))
			return name;
		else
		{
			int i = 1;
			String newName = "";
			while(fnames.contains(newName = (nameParts[0] + " (" + i++ + ")" + nameParts[1]))){}
			return newName;
		}
	}
	
	public static String[] getNameParts(String name)
	{
		int lastDot = name.lastIndexOf('.');
		if(lastDot == -1)
			return new String[] {name, ""};
		else
			return new String[] {name.substring(0, lastDot), name.substring(lastDot)};
	}
}
