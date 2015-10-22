package com.polydes.datastruct.res;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class Resources
{
	private static final Logger log = Logger.getLogger(Resources.class);
	
	private static Resources _instance;
	private static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
	
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
	
	public static ImageIcon thumb(String loc, int size)
	{
		Image img = Resources.loadIcon(loc).getImage();
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(img, 0, 0, bi.getWidth(), bi.getHeight(), null);
		return new ImageIcon(GraphicsUtilities.createThumbnail(bi, size));
	}
}
