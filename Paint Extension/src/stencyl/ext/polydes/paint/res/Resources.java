package stencyl.ext.polydes.paint.res;

import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class Resources
{
	private static final Logger log = Logger.getLogger(Resources.class);
	
	private static Resources _instance;
	private static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

	private Resources()
	{

	}

	public static URL getUrl(String name)
	{
		if (_instance == null)
			_instance = new Resources();

		return _instance.getClass().getResource(name);
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
}
