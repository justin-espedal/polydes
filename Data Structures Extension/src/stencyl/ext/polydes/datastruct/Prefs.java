package stencyl.ext.polydes.datastruct;

import java.util.HashMap;

import stencyl.ext.polydes.common.ui.darktree.DarkTree;
import stencyl.ext.polydes.common.util.CommonLoader;

public class Prefs
{
	public static String DEFPAGE_X = "defpage.x";
	public static String DEFPAGE_Y = "defpage.y";
	public static String DEFPAGE_WIDTH = "defpage.width";
	public static String DEFPAGE_HEIGHT = "defpage.height";
	public static String DEFPAGE_SIDEWIDTH = "defpage.sidewidth";
	public static String DEFPAGE_SIDEDL = "defpage.sidedl";
	
	public static boolean initialized = false;
	
	public static HashMap<String, Object> props = new HashMap<String, Object>();
	
	public static void loadProperties()
	{
		initialized = true;
		
		CommonLoader.loadPropertiesForExtension(Main.get(), props);
		
		setDefaultProperty(DEFPAGE_X, -1);
		setDefaultProperty(DEFPAGE_Y, -1);
		setDefaultProperty(DEFPAGE_WIDTH, 640);
		setDefaultProperty(DEFPAGE_HEIGHT, 480);
		setDefaultProperty(DEFPAGE_SIDEWIDTH, DarkTree.DEF_WIDTH);
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