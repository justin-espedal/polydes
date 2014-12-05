package stencyl.ext.polydes.extrasmanager.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import stencyl.ext.polydes.extrasmanager.app.FileCreateDialog;
import stencyl.ext.polydes.extrasmanager.app.utils.ExtrasUtil;
import stencyl.ext.polydes.extrasmanager.io.FileMonitor;
import stencyl.sw.SW;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.dg.YesNoQuestionDialog;

//TODO: Move this to IO and move interface stuff to somewhere in app.
//TODO: Add copy, cut, paste, etc, make this the universal callpoint.

public class FileOperations
{
	public static File[] templates = {};
	
	public static void createFile(File template, File targetDir)
	{
		String name = ExtrasUtil.getUnusedName(template.getName(), targetDir);
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
		String name = ExtrasUtil.getUnusedName("New Folder", targetDir);
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
		
		String ext = ExtrasUtil.getNameParts(template.getName())[1];
		
		if(!name.endsWith(ext))
			name += ext;
		
		name = ExtrasUtil.getUnusedName(name, targetDir);
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
		if(dg.getResult() == JOptionPane.YES_OPTION)
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
			String name = ExtrasUtil.getUnusedName(newName, parent);
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
}
