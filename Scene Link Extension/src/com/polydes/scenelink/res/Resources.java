package com.polydes.scenelink.res;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.polydes.scenelink.SceneLinkExtension;
import com.polydes.scenelink.ui.combos.ImageReferenceComboModel;

public class Resources
{
	private static final Logger log = Logger.getLogger(Resources.class);
	
	private static Resources _instance;
	private static ArrayList<String> resourceNames;
	private static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
	private static HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();
	
	private Resources()
	{

	}
	
	public static Resources get()
	{
		if (_instance == null)
			_instance = new Resources();
		return _instance;
	}
	
	public static InputStream getUrlStream(String name)
	{
		return Resources.get().getClass().getResourceAsStream(name);
	}

	public static URL getUrl(String name)
	{
		return Resources.get().getClass().getResource(name);
	}
	
	public static BufferedImage readImage(String name)
	{
		if(name.equals(""))
			return null;
		
		File imgPath = new File(SceneLinkExtension.resourcesFolder, name + ".png");
		
		try
		{
			return ImageIO.read(imgPath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage getImage(String name)
	{
		if(name.equals(""))
			return null;
		
		if(!imageCache.containsKey(name))
			imageCache.put(name, readImage(name));
		
		return imageCache.get(name);
	}
	
	public static String load(String url)
	{
		InputStream in = getUrlStream(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String contents = "";
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				contents = contents + line + "\n";
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return contents;
	}
	
	public static ImageIcon loadIcon(String url)
	{
		ImageIcon result = iconCache.get(url);

		if (result == null)
		{
			URL u = getUrl(url);

			try
			{
				ImageIcon icon = new ImageIcon(u);
				iconCache.put(url, icon);
				return icon;
			}

			catch (Exception e)
			{
				log.error(e.getMessage(), e);
			}

			return new ImageIcon();
		}

		else
		{
			return result;
		}
	}
	
	public static ArrayList<String> getResourceNames()
	{
		return resourceNames;
	}

	public static void loadResourceNames()
	{
		resourceNames = new ArrayList<String>();
		for(String s : SceneLinkExtension.resourcesFolder.list())
		{
			if(s.endsWith(".png"))
				s = s.substring(0, s.length() - 4);
			
			resourceNames.add(s);
		}
		
		ImageReferenceComboModel.updateImages();
	}
}
