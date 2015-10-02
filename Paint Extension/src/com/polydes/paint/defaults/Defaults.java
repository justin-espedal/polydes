package com.polydes.paint.defaults;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class Defaults
{
	private static Defaults _instance;

	private Defaults()
	{

	}

	public static InputStream getUrlStream(String name)
	{
		if (_instance == null)
			_instance = new Defaults();

		return _instance.getClass().getResourceAsStream(name);
	}

	public static String load(String url)
	{
		InputStream in = getUrlStream(url);
		try
		{
			return IOUtils.toString(in, "UTF-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static URL getUrl(String name)
	{
		if (_instance == null)
			_instance = new Defaults();

		return _instance.getClass().getResource(name);
	}

	public static BufferedImage loadImage(String url)
	{
		try
		{
			return ImageIO.read(getUrl(url));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	}
}
