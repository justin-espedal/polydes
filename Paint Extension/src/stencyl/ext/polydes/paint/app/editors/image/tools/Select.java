package stencyl.ext.polydes.paint.app.editors.image.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.app.editors.image.ImageUtils;
import stencyl.ext.polydes.paint.data.transfer.ImageTransfer;

public class Select implements Tool, KeyListener
{
	public static final float TRANSPARENCY = .2f;
	public static final Color SELECT_COLOR = Color.BLUE;
	public static final AlphaComposite ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY);
	
	private DrawArea area;
	private Point beginPress;
	private Rectangle oldDraw;
	private Rectangle selectionArea;
	
	private boolean selectMode;

	public Select()
	{
		area = null;
		beginPress = null;
		oldDraw = null;
		selectionArea = null;
		selectMode = true;
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		selectMode = true;
		
		if(area.floating != null)
		{
			if(area.floatRect.contains(x, y))
				selectMode = false;
			else
			{
				ImageUtils.drawImage(area.img, area.floatRect.getLocation(), area.floating);
				area.floating = null;
				area.floatRect = null;
				selectMode = true;
				area.setDirty();
			}
		}
		else if(selectionArea != null)
		{
			if(selectionArea.contains(x, y))
				selectMode = false;
			else
				selectMode = true;
		}
		else
			selectMode = true;
		
		beginPress = new Point(x, y);
		
		if(selectMode)
		{
			if(selectionArea != null)
				area.repaint(selectionArea.x, selectionArea.y, selectionArea.width + 1, selectionArea.height + 1);
			
			selectionArea = null;
		}
	}

	@Override
	public void drag(int x, int y)
	{
		if(selectMode)
		{
			if(selectionArea == null)
				selectionArea = new Rectangle(0, 0, 1, 1);
			
			selectionArea.x = Math.min(beginPress.x, x);
			selectionArea.y = Math.min(beginPress.y, y);
			selectionArea.width = Math.max(beginPress.x, x) - selectionArea.x + 1;
			selectionArea.height = Math.max(beginPress.y, y) - selectionArea.y + 1;
		}
		else
		{
			if(area.floating == null)
			{
				area.floating = ImageUtils.cutSubImage(area.img, selectionArea);
				area.floatRect = new Rectangle(selectionArea.x, selectionArea.y, area.floating.getWidth(), area.floating.getHeight());
			}
			
			area.floatRect.x += (x - area.lastUpdated.x);
			area.floatRect.y += (y - area.lastUpdated.y);
			
			selectionArea = (Rectangle) area.floatRect.clone();
		}
		
		Rectangle r;
		if(oldDraw != null)
			r = selectionArea.union(oldDraw);
		else
			r = selectionArea;
		
		area.repaint(r.x, r.y, r.width + 1, r.height + 1);
		
		oldDraw = (Rectangle) selectionArea.clone();
	}

	@Override
	public void release(int x, int y)
	{
		if(selectMode)
		{
			beginPress = null;
			if(selectionArea == null)
				oldDraw = null;
		}
	}

	@Override
	public void enter(int x, int y)
	{
	}

	@Override
	public void exit(int x, int y)
	{
	}

	@Override
	public ToolOptions getOptions()
	{
		return null;
	}

	@Override
	public void move(int x, int y)
	{
	}

	@Override
	public void render(Graphics g, int x, int y, int w, int h)
	{
		if(selectionArea != null)
		{
			int x1 = selectionArea.x;
			int y1 = selectionArea.y;
			int w1 = selectionArea.width;
			int h1 = selectionArea.height;
			
			int s = (int) area.scale;
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(SELECT_COLOR);
			g2d.setComposite(ALPHA);
			g2d.fillRect(x1 * s, y1 * s, w1 * s, h1 * s);
			g2d.setComposite(AlphaComposite.SrcOver);
			g2d.drawRect(x1 * s, y1 * s, w1 * s, h1 * s);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		if(e.isControlDown())
		{
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_A:
					selectAll();
					break;
				case KeyEvent.VK_C:
					copySelection();
					break;
				case KeyEvent.VK_X:
					cutSelection();
					break;
				case KeyEvent.VK_V:
					paste();
					break;
			}
		}
	}

	public void selectAll()
	{
		selectionArea = new Rectangle(0, 0, area.img.getWidth(), area.img.getHeight());
		oldDraw = (Rectangle) selectionArea.clone();
		area.repaint(0, 0, area.img.getWidth(), area.img.getHeight());
	}
	
	public void copySelection()
	{
		if(selectionArea != null)
		{
			ImageTransfer.copy(ImageUtils.getSubImage(area.img, selectionArea));
		}
	}
	
	public void cutSelection()
	{
		if(selectionArea != null)
		{
			ImageTransfer.copy(ImageTransfer.imgCopy(area.img, selectionArea));
			
			ImageUtils.fill(area.img, selectionArea, DrawArea.TRANSPARENT);
			area.setDirty();
		}
	}
	
	public void paste()
	{
		BufferedImage img = ImageTransfer.paste();
		if(img != null)
		{
			area.floating = img;
			area.floatRect = new Rectangle(0, 0, img.getWidth(), img.getHeight());
			selectionArea = (Rectangle) area.floatRect.clone();
		}
		area.repaint(0, 0, img.getWidth() + 1, img.getHeight() + 1);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}
