package stencyl.ext.polydes.datastruct;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.sw.util.VerificationHelper;

public class Prefs
{
	public static String DEFPAGE_X = "defpage.x";
	public static String DEFPAGE_Y = "defpage.y";
	public static String DEFPAGE_WIDTH = "defpage.width";
	public static String DEFPAGE_HEIGHT = "defpage.height";
	public static String DEFPAGE_SIDEWIDTH = "defpage.sidewidth";
	public static String DEFPAGE_SIDEDL = "defpage.sidedl";
	
	public static boolean initialized = false;
	
	public static HashMap<String, Object> props;
	
	public static void loadProperties()
	{
		initialized = true;
		
		String data = Main.get().readInternalData();
		if(data == null)
			data = "";
		props = new HashMap<String, Object>();
		
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
		
		setDefaultProperty(DEFPAGE_X, -1);
		setDefaultProperty(DEFPAGE_Y, -1);
		setDefaultProperty(DEFPAGE_WIDTH, 640);
		setDefaultProperty(DEFPAGE_HEIGHT, 480);
		setDefaultProperty(DEFPAGE_SIDEWIDTH, DTree.DEF_WIDTH);
		setDefaultProperty(DEFPAGE_SIDEDL, 150);
	}
	
	private static void setDefaultProperty(String name, Object value)
	{
		if(!props.containsKey(name) || props.get(name) == null)
			props.put(name, value);
	}
	
	public static void save()
	{
		if(!initialized)
			return;
		
		String s = "";
		for(String key : props.keySet())
			s += key + "=" + props.get(key) + "\n";
		
		Main.get().writeInternalData(s);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key)
	{
		if(!initialized)
			loadProperties();
		
		return (T) props.get(key);
	}
	
	public static void set(String key, Object value)
	{
		if(!initialized)
			loadProperties();
		
		props.put(key, value);
	}
}