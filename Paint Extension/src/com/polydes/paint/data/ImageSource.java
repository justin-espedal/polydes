package com.polydes.paint.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageSource extends LinkedDataItem
{
	private BufferedImage img;
	
	public ImageSource(String name)
	{
		super(name);
		contents = img = null;
	}
	
	public void loadFromFile(File loc)
	{
		try
		{
			img = ImageIO.read(loc);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		contents = img;
	}
	
	public BufferedImage getImage()
	{
		return img;
	}
	
	@Override
	public void setContents(Object o)
	{
		if(o instanceof BufferedImage)
		{
			img = (BufferedImage) o;
			super.setContents(o);
		}
	}
}
