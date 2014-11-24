package stencyl.ext.polydes.extrasmanager.data;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import stencyl.ext.polydes.extrasmanager.app.FileCreateDialog;
import stencyl.ext.polydes.extrasmanager.app.utils.ExtrasUtil;
import stencyl.sw.SW;

public class FileCreator
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
	}
	
	public static void createFolder(File targetDir)
	{
		String name = ExtrasUtil.getUnusedName("New Folder", targetDir);
		File f = new File(targetDir, name);
		f.mkdir();
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
	}
}
