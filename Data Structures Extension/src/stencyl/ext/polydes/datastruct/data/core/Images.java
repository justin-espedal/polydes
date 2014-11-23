package stencyl.ext.polydes.datastruct.data.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;


public class Images
{
	private static Images instance;
	private static HashMap<String, ExtrasImage> images;
	
	public static Images get()
	{
		if(instance == null)
			instance = new Images();
		
		return instance;
	}
	
	private Images()
	{
		images = new HashMap<String, ExtrasImage>();
	}
	
	public void load(File file)
	{
		for(String s : file.list())
		{
			if(s.endsWith(".png") || s.endsWith(".PNG"))
			{
				ExtrasImage img = new ExtrasImage();
				img.name = s.substring(0, s.length() - 4);
				images.put(img.name, img);
			}
		}
	}
	
	public Collection<ExtrasImage> getList()
	{
		return images.values();
	}

	public ExtrasImage getImage(String s)
	{
		return images.get(s);
	}
	
	public static void dispose()
	{
		images.clear();
		instance = null;
	}
}
