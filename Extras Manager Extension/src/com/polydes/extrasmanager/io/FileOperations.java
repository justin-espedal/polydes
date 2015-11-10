package com.polydes.extrasmanager.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.polydes.common.sys.FileMonitor;
import com.polydes.extrasmanager.app.FileCreateDialog;
import com.polydes.extrasmanager.data.FileClipboard;

import stencyl.sw.SW;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.UI;
import stencyl.sw.util.dg.YesNoQuestionDialog;

//TODO: Move this to IO and move interface stuff to somewhere in app.

public class FileOperations
{
	public static File[] templates = {};
	
	public static void copy(List<File> files)
	{
		FileClipboard.clear();
		for(File f : files)
			FileClipboard.add(f);
		FileClipboard.op = FileClipboard.COPY;
	}
	
	public static void cut(List<File> files)
	{
		FileClipboard.clear();
		for(File f : files)
			FileClipboard.add(f);
		FileClipboard.op = FileClipboard.CUT;
	}
	
	public static void paste(File targetParent)
	{
		for(File f : FileClipboard.list())
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
				
				if(FileClipboard.op == FileClipboard.CUT)
					FileHelper.delete(f);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		};
		
		FileClipboard.clear();
		FileMonitor.refresh();
	}
	
	public static void createFile(File template, File targetDir)
	{
		String name = getUnusedName(template.getName(), targetDir);
		try
		{
			FileUtils.copyFile(template, new File(targetDir, name));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		FileMonitor.refresh();
	}
	
	public static void createFolder(File targetDir)
	{
		String name = getUnusedName("New Folder", targetDir);
		File f = new File(targetDir, name);
		f.mkdir();
		
		FileMonitor.refresh();
	}
	
	public static void createFile(File targetDir)
	{
		FileCreateDialog create = new FileCreateDialog(SW.get());
		if(create.getString() == null)
			return;
		
		String name = create.getString();
		File template = create.getTemplate();
		
		String ext = getNameParts(template.getName())[1];
		
		if(!name.endsWith(ext))
			name += ext;
		
		name = getUnusedName(name, targetDir);
		try
		{
			FileUtils.copyFile(template, new File(targetDir, name));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		FileMonitor.refresh();
	}
	
	public static void deleteFiles(List<File> files)
	{
		YesNoQuestionDialog dg = new YesNoQuestionDialog("Delete Files", "Are you sure you want to delete the selected files?", "", new String[] {"Yes", "No"}, true);
		if(dg.getResult() == UI.Choice.YES)
		{
			for(Object o : files)
				FileHelper.delete((File) o);
			
			FileMonitor.refresh();
		}
	}
	
	public static void renameFiles(List<File> files, String newName)
	{
		for(File f : files)
		{
			File parent = f.getParentFile();
			String name = getUnusedName(newName, parent);
			f.renameTo(new File(parent, name));
		}
		
		FileMonitor.refresh();
	}
	
	public static List<File> asFiles(Object[] a)
	{
		ArrayList<File> files = new ArrayList<File>(a.length);
		for(Object o : a)
			files.add((File) o);
		return files;
	}
	
	/**
	 * @param name Desired name, extension included. (New Document.txt)
	 * @param targetDir Folder to create the new file in
	 * @return String: the original name plus any numbering needed to make it unique (New Document (1).txt)
	 */
	private static String getUnusedName(String name, File targetDir)
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
