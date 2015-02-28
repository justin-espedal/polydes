package stencyl.ext.polydes.dialog.defaults;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class Defaults
{
	private static final Logger log = Logger.getLogger(Defaults.class);
	
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

	public static URL getUrl(String name)
	{
		if (_instance == null)
			_instance = new Defaults();

		return _instance.getClass().getResource(name);
	}

	public static BufferedImage loadImage(String url)
	{
		URL u = getUrl(url);
		ImageIcon icon = null;

		try
		{
			icon = new ImageIcon(u);
		}

		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		BufferedImage bi = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();

		return bi;
	}
}
