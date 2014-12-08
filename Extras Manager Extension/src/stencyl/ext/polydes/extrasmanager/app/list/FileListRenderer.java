package stencyl.ext.polydes.extrasmanager.app.list;

import java.awt.Color;
import java.awt.Component;
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
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import stencyl.ext.polydes.extrasmanager.data.Mime;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFile;
import stencyl.ext.polydes.extrasmanager.res.Resources;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;
import stencyl.sw.util.gfx.GraphicsUtilities;
import stencyl.sw.util.gfx.ImageUtil;

public class FileListRenderer extends JLabel implements ListCellRenderer 
{
	public static final int MINI_SIZE = 16;
	
	public static final int DEFAULT_WIDTH = 92;
	public static final int DEFAULT_HEIGHT = 80;
	
	public static final int DEFAULT_ICON_WIDTH = 80;
	public static final int DEFAULT_ICON_HEIGHT = 80;
	
	public static ImageIcon folderThumb = Loader.loadIcon("res/libraries/folder.png");
	public static ImageIcon fileThumb = Resources.loadIcon("page/file.png");

	public static HashMap<String, Image> renderedIconCache = new HashMap<String, Image>();
	public static HashMap<String, ImageIcon> miniRenderedIconCache = new HashMap<String, ImageIcon>();
	
	private static ImageIcon img = Loader.loadIcon(Locations.PROGRESS_SPINNER);
	
	protected FileListIcon icon;
	private Image cache;
	
	private int iconWidth;
	private int iconHeight;
	
	protected int fontSize = 3;
	
	public FileListRenderer(int iconWidth, int iconHeight) 
	{
		if(iconWidth == -1)
		{
			iconWidth = DEFAULT_ICON_WIDTH;
		}
		
		if(iconHeight == -1)
		{
			iconHeight = DEFAULT_ICON_HEIGHT;
		}
		
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		
		icon = new FileListIcon(iconWidth, iconHeight);
		
		setOpaque(false);
		
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(TOP);
		
		setHorizontalTextPosition(CENTER);
		setVerticalTextPosition(BOTTOM);
		
		setForeground(Theme.TEXT_COLOR);
		
		setIconTextGap(3);
		setFont(Fonts.getNormalFont());
	}

	@Override
	public Component getListCellRendererComponent
	(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus
	)
	{
		cache = fetchIcon(value);
		
		if(cache != null)
		{
			FileList flist = (FileList) list;
			
			boolean rollover = flist.rolloverIndex == index;
			
			icon.setRollover(rollover, false, false);
			
			icon.setSelected(isSelected);
			icon.setImage(cache, iconWidth, iconHeight);
				
			setIcon(icon);
		}
		
		else
		{
			setIcon(img);
		}
		
		setText(formatText(((SysFile) value).getName()));
		
		return this;
	}
	
	public void refreshIcon()
	{
		cache = null;
	}
	
	public Image fetchIcon(Object value)
	{
		if(!(value instanceof SysFile))
			return null;
		
		SysFile file = (SysFile) value; 
		
		Image img = null;

		img = renderedIconCache.get(file.getFile().getAbsolutePath());
		
		if(img == null)
		{
			img = fetchFileIcon(file.getFile());
			renderedIconCache.put(file.getFile().getAbsolutePath(), img);
		}
		
		return img;
	}
	
	public static ImageIcon fetchMiniIcon(Object value)
	{
		if(!(value instanceof SysFile))
			return null;
		
		SysFile file = (SysFile) value; 
		
		ImageIcon img = null;
		
		img = miniRenderedIconCache.get(file.getFile().getAbsolutePath());
		
		if(img == null)
		{
			img = generateThumb(file.getFile());
			img = new ImageIcon(GraphicsUtilities.createThumbnail(ImageUtil.getBufferedImage(img.getImage()), MINI_SIZE));
			miniRenderedIconCache.put(file.getFile().getAbsolutePath(), img);
		}
		
		return img;
	}
	
	public String formatText(String s)
	{
		setToolTipText(s);
		
		if(s.length() > 40)
		{
			s = s.substring(0, 39) + "...";
		}
		
		return "<html><center><b><font size=" + fontSize + ">" + s + "</font></b></center></html>";
	}
	
	//---
	
	public Image fetchFileIcon(File file)
	{
		System.out.println("Fetch File Icon: " + file.getName());
		
		return icon.prerenderIcon(ImageUtil.getBufferedImage(generateThumb(file).getImage()));
	}
	
	public static ImageIcon generateThumb(File file)
	{
		String type = Mime.get(file);
		
		if(file.isDirectory())
			return folderThumb;
		else if(type.startsWith("image"))
			return generateImageThumb(file);
		else if (type.startsWith("text"))
			return generateTextThumb(file);
		else
			return fileThumb;
	}

	public static ImageIcon generateTextThumb(File file)
	{
		final int MAX_RENDER_STRINGS = 7;
		
		String[] renderLines = new String[MAX_RENDER_STRINGS];
		
		InputStream inputStream;
		try
		{
			inputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			int i = 0;
			while((renderLines[i++] = reader.readLine()) != null)
			{
				if(i == MAX_RENDER_STRINGS)
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
	
	public static void clearThumbnail(File file)
	{
		renderedIconCache.remove(file.getAbsolutePath());
		miniRenderedIconCache.remove(file.getAbsolutePath());
	}

	public static void clearThumbnails()
	{
		renderedIconCache.clear();
		miniRenderedIconCache.clear();
	}
}
