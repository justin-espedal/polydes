package stencyl.ext.polydes.points;

import java.util.HashMap;

import stencyl.ext.polydes.common.util.CommonLoader;

public class Prefs
{
	public static String POINTWIN_X = "pointwin.x";
	public static String POINTWIN_Y = "pointwin.y";
	public static String POINTWIN_WIDTH = "pointwin.width";
	public static String POINTWIN_HEIGHT = "pointwin.height";
	public static String POINTWIN_SIDEWIDTH = "pointwin.sidewidth";
	
	public static boolean initialized = false;
	
	public static HashMap<String, Object> props = new HashMap<String, Object>();
	
	public static void loadProperties()
	{
		initialized = true;
		
		CommonLoader.loadPropertiesForExtension(Main.get(), props);
		
		setDefaultProperty(POINTWIN_X, -1);
		setDefaultProperty(POINTWIN_Y, -1);
		setDefaultProperty(POINTWIN_WIDTH, 640);
		setDefaultProperty(POINTWIN_HEIGHT, 480);
		setDefaultProperty(POINTWIN_SIDEWIDTH, 265);
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