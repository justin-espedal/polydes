package com.polydes.common.sys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

import stencyl.sw.util.FileHelper;

public class SysFileOperations
{
	public static void moveFiles(List<File> files, File targetParent)
	{
		for(File f : files)
		{
			try
			{
				String newName = getUnusedName(f.getName(), targetParent);
				File target = new File(targetParent, newName);
				if(f.isDirectory())
				{
					ArrayList<File> exclude = new ArrayList<File>();
					exclude.add(target);
					FileHelper.copyDirectory(f, target, exclude);
				}
				else
					FileUtils.copyFile(f, target);
				
				FileHelper.delete(f);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		};
		
		FileMonitor.refresh();
	}
	
	/**
	 * @param name Desired name, extension included. (New Document.txt)
	 * @param targetDir Folder to create the new file in
	 * @return String: the original name plus any numbering needed to make it unique (New Document (1).txt)
	 */
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
	
	/**
	 * For "some.name.ext", returns ["some.name", "ext"]
	 * For "some folder name", returns ["some folder name", ""]
	 */
	public static String[] getNameParts(String name)
	{
		int lastDot = name.lastIndexOf('.');
		if(lastDot == -1)
			return new String[] {name, ""};
		else
			return new String[] {name.substring(0, lastDot), name.substring(lastDot)};
	}
}
