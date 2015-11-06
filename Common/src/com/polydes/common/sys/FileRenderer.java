package com.polydes.common.sys;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.polydes.common.res.ResourceLoader;

import stencyl.sw.util.gfx.ImageUtil;
import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class FileRenderer
{
	public static final int MINI_SIZE = 16;
	
	public static final int DEFAULT_WIDTH = 92;
	public static final int DEFAULT_HEIGHT = 80;
	
	public static ImageIcon folderThumb = ResourceLoader.loadIcon("filelist/folder.png");
	public static ImageIcon fileThumb = ResourceLoader.loadIcon("filelist/file.png");

	protected int fontSize = 3;
	
	public static ImageIcon fetchMiniIcon(SysFile file)
	{
		System.out.println(file.getFile().getAbsolutePath());
		ImageIcon img = generateThumb(file.getFile());
		int small = Math.min(img.getImage().getWidth(null), img.getImage().getHeight(null));
		img = new ImageIcon(GraphicsUtilities.createThumbnail(ImageUtil.getBufferedImage(img.getImage()), Math.min(MINI_SIZE, small)));
		return img;
	}
	
	//---
	
	public Image fetchFileImage(File file)
	{
		System.out.println("Fetch File Icon: " + file.getName());
		
		return ImageUtil.getBufferedImage(generateThumb(file).getImage());
	}
	
	public static ImageIcon generateThumb(File file)
	{
		String type = Mime.get(file);
		
		if(file.isDirectory())
			return folderThumb;
		else if(type.startsWith("image"))
			return generateImageThumb(file);
		else if (type.startsWith("text") || type.equals("application/octet-stream"))
			return generateTextThumb(file);
		else
			return fileThumb;
	}

	public static ImageIcon generateTextThumb(File file)
	{
		String[] renderLines = getLines(file, 7);
		
		Font font = new Font("Tahoma", Font.PLAIN, 11);
		
		int lineHeight = 12;
		
		BufferedImage image = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(font);
		
		int y = 0;
		for(String line : renderLines)
		{
			if(line == null)
			{
				++y;
				continue;
			}
			
			g.drawString(line, 0, y++ * lineHeight);
		}
		
		// releasing resources
		g.dispose();
		
		return new ImageIcon(image);
	}
	
	public static String[] getLines(File f, int lines)
	{
		String[] renderLines = new String[lines];
		
		InputStream inputStream;
		try
		{
			inputStream = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			int i = 0;
			while((renderLines[i++] = reader.readLine()) != null)
			{
				if(i == lines)
					break;
			}

			reader.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return renderLines;
	}
	
	public static ImageIcon generateImageThumb(File file)
	{
		BufferedImage in = null;
		try
		{
			in = ImageIO.read(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if(in != null)
		{
			//BufferedImage thumbnail = Scalr.resize(ImageIO.read(file), Scalr.Method.SPEED, DEFAULT_WIDTH, DEFAULT_HEIGHT, Scalr.OP_ANTIALIAS);
			if(in.getWidth() > DEFAULT_WIDTH && in.getHeight() > DEFAULT_HEIGHT)
				in = GraphicsUtilities.createThumbnail(in, DEFAULT_WIDTH, DEFAULT_HEIGHT);
			ImageIcon icon = new ImageIcon(in);
			return icon;
		}
		else
			return fileThumb;
	}
}
