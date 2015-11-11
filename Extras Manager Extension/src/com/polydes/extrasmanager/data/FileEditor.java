package com.polydes.extrasmanager.data;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.polydes.common.sys.FileMonitor;
import com.polydes.common.sys.Mime;
import com.polydes.extrasmanager.app.FileRenameDialog;

import stencyl.sw.SW;

public class FileEditor
{
	public static HashMap<String, String> typeProgramMap = new HashMap<String, String>();
	
	public static void edit(File f)
	{
		String exec = typeProgramMap.get(Mime.get(f));
		try
		{
			if(exec == null || exec.length() <= 2)
			{
				try
				{
					Desktop.getDesktop().edit(f);
				}
				catch(Exception ex)
				{
					try
					{
						Desktop.getDesktop().open(f);
					}
					catch(Exception ex2)
					{
						ex2.printStackTrace();
					}
				}
			}
			else
				Runtime.getRuntime().exec(new String[] {exec, f.getAbsolutePath()});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void rename(File file)
	{
		FileRenameDialog dg = new FileRenameDialog(SW.get(), file);
		String result = dg.getString();
		if(result != null)
		{
			file.renameTo(new File(file.getParentFile(), result));
			FileMonitor.refresh();
		}
	}
}
