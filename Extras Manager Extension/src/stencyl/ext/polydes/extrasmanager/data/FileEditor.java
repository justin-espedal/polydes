package stencyl.ext.polydes.extrasmanager.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileEditor
{
	public static HashMap<String, String> typeProgramMap = new HashMap<String, String>();
	
	public static void edit(File f)
	{
		String exec = typeProgramMap.get(Mime.get(f));
		try
		{
			Runtime.getRuntime().exec(new String[] {exec, f.getAbsolutePath()});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
