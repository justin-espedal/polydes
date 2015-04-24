package stencyl.ext.polydes.extrasmanager.app.list;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import stencyl.sw.app.lists.AbstractListIcon;
import stencyl.sw.util.Loader;
import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class FileListIcon implements Icon 
{
	public static final int DELETE_ICON_Y_PAD = 11;
	public static final int DELETE_ICON_X_PAD = 17;
	private BufferedImage mask;
	private Image img;
	private boolean selected;
	private boolean rollover;
	private boolean rolloverDelete;
	private boolean drawDelete = true;
	
	private int oldWidth;
	private int oldHeight;

	private static int BORDER_THICKNESS = 2;
	private static int DOUBLE_THICKNESS = 2 * BORDER_THICKNESS;
	private int ARC_SIZE = 25;
	
	public int DEFAULT_WIDTH;
	public int DEFAULT_HEIGHT;
	public int TOP_PADDING = 15;
	
	private final Color SELECTED_COLOR = new Color(220, 155, 55);
	private final Color BORDER_COLOR = new Color(50, 50, 50);
	private final Color ROLLOVER_COLOR = new Color(100, 170, 190);//SELECTED_COLOR.darker(); //new Color(100, 160, 190);
	private static Color FILL_COLOR = Color.GRAY;
	
	public static final ImageIcon MY_ICON = Loader.loadIcon("res/libraries/upload.png");
	public static final ImageIcon LOCK_ICON = Loader.loadIcon("res/libraries/lock.png");
	public static final ImageIcon STAR_ICON = Loader.loadIcon("res/menu/edit/default.png");
	public static final ImageIcon CLOSE_ICON = Loader.loadIcon("res/common/close-overlay.png");
    public static final ImageIcon CLOSE_HOVER_ICON = Loader.loadIcon("res/common/close-overlay-hover.png");
	
    public static final ImageIcon ACTOR_ICON = Loader.loadIcon("res/forge/actor-overlay.png");
    public static final ImageIcon SCENE_ICON = Loader.loadIcon("res/forge/scene-overlay.png");
    public static final ImageIcon FEATURED_ICON = Loader.loadIcon("res/forge/featured-overlay.png");
    
    public static final int START_ICON_TOPLEFT = 1;
    public static final int MY_ICON_TOPRIGHT = 2;
    public static final int MY_ICON_TOPLEFT = 3;
    
	public FileListIcon(int width, int height) 
	{
		DEFAULT_WIDTH = width + BORDER_THICKNESS * 2;
		DEFAULT_HEIGHT = height + BORDER_THICKNESS * 2;
	}

	public void setRollover(boolean isRollover, boolean rolloverDelete, boolean drawDelete)
	{
		this.rollover = isRollover;
		this.rolloverDelete = rolloverDelete;
		this.drawDelete = drawDelete;
	}
	
	public void setSelected(boolean isSelected)
	{
		this.selected = isSelected;
	}
	
	public void setImage(Image image, int width, int height)
	{
		img = image;
		oldWidth = width;
		oldHeight = height;
		
		if(mask == null || width != oldWidth || height != oldHeight)
		{
			mask = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = mask.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.fillRoundRect
			(
				BORDER_THICKNESS, 
				BORDER_THICKNESS, 
				DEFAULT_WIDTH - DOUBLE_THICKNESS, 
				DEFAULT_HEIGHT - DOUBLE_THICKNESS, 
				ARC_SIZE, 
				ARC_SIZE
			);
			g2d.dispose();
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public int getIconHeight() 
	{
		return DEFAULT_HEIGHT;
	}

	@Override
	public int getIconWidth() 
	{
		return DEFAULT_WIDTH;
	}
	
	public void setArcSize(int size)
	{
		ARC_SIZE = size;
	}
	
	public Image prerenderIcon(Image img)
	{
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
	
		if(img.getWidth(null) > width)
		{
			img = GraphicsUtilities.createThumbnail((BufferedImage) img, width);
		}
		
		if(img.getHeight(null) > height)
		{
			img = GraphicsUtilities.createThumbnail((BufferedImage) img, height);
		}
		
		// Create a translucent intermediate image in which we can perform
		// the soft clipping
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gd.getDefaultConfiguration();

		BufferedImage tempImg = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D g2 = tempImg.createGraphics();

		// Clear the image so all pixels have zero alpha
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, width, height);

		// Render our clip shape into the image.  Note that we enable
		// antialiasing to achieve the soft clipping effect.  Try
		// commenting out the line that enables antialiasing, and
		// you will see that you end up with the usual hard clipping.
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(FILL_COLOR);

		g2.fill(new RoundRectangle2D.Double
		(
			BORDER_THICKNESS + 2, 
			BORDER_THICKNESS + 2, width - 
			DOUBLE_THICKNESS - 4, height - 
			DOUBLE_THICKNESS - 4, 
			ARC_SIZE - 2, 
			ARC_SIZE - 2
		));

		// Here's the trick... We use SrcAtop, which effectively uses the
		// alpha value as a coverage value for each pixel stored in the
		// destination.  For the areas outside our clip shape, the destination
		// alpha will be zero, so nothing is rendered in those areas.  For
		// the areas inside our clip shape, the destination alpha will be fully
		// opaque, so the full color is rendered.  At the edges, the original
		// antialiasing is carried over to give us the desired soft clipping
		// effect.
		g2.setComposite(AlphaComposite.SrcAtop);
		
		if(img != null)
		{
			g2.drawImage(img, width/2 - img.getWidth(null)/2, height/2 - img.getHeight(null)/2, null);
		}
		
		g2.dispose();
		
		return tempImg;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) 
	{
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
		
		//XXX: We switched to top alignment due to some drawing issues with
		//using vertical center alignment. This makes it look like it did before.
		//Mike: Changed to object variable
		//int TOP_PADDING = 15;

		//---
		
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		g.translate(x, y + TOP_PADDING);

		if(selected || rollover) 
		{
			if(selected)
			{
				((Graphics2D) g).setPaint(new GradientPaint(0, 0, SELECTED_COLOR, width*2, height*2, SELECTED_COLOR.brighter()));
			}
			
			else if(rollover)
			{
				((Graphics2D) g).setPaint(new GradientPaint(0, 0, ROLLOVER_COLOR, width*2, height*2, ROLLOVER_COLOR.brighter()));
			}
			
			g.fillRoundRect(0, 0, width, height, ARC_SIZE, ARC_SIZE);
			((Graphics2D) g).setPaint(null);
		} 

		g.setColor(BORDER_COLOR);
		g.fillRoundRect
		(
			BORDER_THICKNESS + 1, 
			BORDER_THICKNESS + 1, 
			width - DOUBLE_THICKNESS - 2, 
			height - DOUBLE_THICKNESS - 2, 
			ARC_SIZE, 
			ARC_SIZE
		);

		g.setColor(FILL_COLOR);

		g.fillRoundRect
		(
			BORDER_THICKNESS + 2, 
			BORDER_THICKNESS + 2, 
			width - DOUBLE_THICKNESS - 4, 
			height - DOUBLE_THICKNESS - 4, 
			ARC_SIZE - 2, 
			ARC_SIZE - 2
		);

		if(img != null)
		{
			g.drawImage(img, 0, 0, null);
		}

		if(rollover && drawDelete)
		{
			if(rolloverDelete)
			{
				g.drawImage
				(
					CLOSE_HOVER_ICON.getImage(), 
					width - AbstractListIcon.DELETE_ICON_X_PAD, 
					-AbstractListIcon.DELETE_ICON_Y_PAD, 
					null
				);
			}
			
			else
			{
				g.drawImage
				(
					CLOSE_ICON.getImage(), 
					width - AbstractListIcon.DELETE_ICON_X_PAD, 
					-AbstractListIcon.DELETE_ICON_Y_PAD, 
					null
				);
			}
		}
		
		g.translate(-x, -y);		
	}
}
