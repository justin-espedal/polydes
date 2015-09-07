package stencyl.ext.polydes.common.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.common.ext.GameExtension;
import stencyl.sw.util.VerificationHelper;

public class CommonLoader
{
	public static void loadPropertiesFromFile(String loc, HashMap<String, Object> props)
	{
		try
		{
			loadPropertiesFromString(FileUtils.readFileToString(new File(loc)), props);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadPropertiesForExtension(GameExtension ext, HashMap<String, Object> props)
	{
		loadPropertiesFromString(ext.readInternalData(), props);
	}
	
	public static void loadPropertiesFromString(String data, HashMap<String, Object> props)
	{
		if(data == null)
			data = "";
		
		for(String s : StringUtils.split(data, "\n"))
		{
			if(s.trim().isEmpty())
				continue;
			
			int i = s.indexOf("=");
			if(i == -1)
				continue;
			
			String key = s.substring(0, i);
			String value = s.substring(i + 1);
			if(VerificationHelper.isInteger(value))
				props.put(key, Integer.parseInt(value));
			else if(VerificationHelper.isFloat(value))
				props.put(key, Float.parseFloat(value));
			else if(value.equals("true") || value.equals("false"))
				props.put(key, value.equals("true"));
			else
				props.put(key, value);
		}
	}
}
