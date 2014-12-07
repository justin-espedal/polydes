package stencyl.ext.polydes.paint.app.editors.bitmapfont.tools;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import stencyl.ext.polydes.paint.app.editors.bitmapfont.FontDrawArea;
import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.app.editors.image.tools.Tool;
import stencyl.ext.polydes.paint.app.editors.image.tools.ToolOptions;
import stencyl.ext.polydes.paint.data.BitmapFont;
import stencyl.ext.polydes.paint.data.BitmapGlyph;
import stencyl.ext.polydes.paint.res.Resources;

public class LineSpacing implements Tool, MouseListener, MouseMotionListener
{
	public static final float TRANSPARENCY = .2f;
	public static final Color SELECT_COLOR = Color.ORANGE;
	public static final Color HOVER_COLOR = Color.BLUE;
	
	public static final AlphaComposite ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY);
	
	public static final int NONE = -1;
	public static final int TOP = 0;
	public static final int MIDDLE = 1;
	public static final int BOTTOM = 2;
	
	FontDrawArea area;
	BitmapFont font;
	ArrayList<BitmapGlyph> glyphs;
	BitmapGlyph hoveredGlyph;
	public BitmapGlyph selectedGlyph;
	int hoveredControl;
	int activeControl;
	
	int beginPos;
	int beginOffset;
	int beginBase;
	int beginLineHeight;
	Point beginPress;
	Point activePress;
	
	public LineSpacing()
	{
		hoveredControl = NONE;
		activeControl = NONE;
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = (FontDrawArea) area;
		font = this.area.font;
		glyphs = font.chars;
	}

	@Override
	public void press(int x, int y)
	{
	}

	@Override
	public void drag(int x, int y)
	{
	}

	@Override
	public void release(int x, int y)
	{
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
	public void move(int x, int y)
	{
	}

	@Override
	public void render(Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2d = (Graphics2D) g;
		int s = (int) area.scale;
		
		if(hoveredGlyph != null && hoveredGlyph != selectedGlyph)
		{
			Rectangle r = hoveredGlyph.r;
			
			//drawRect(g2d, r, HOVER_COLOR);
			
			int y1 = hoveredGlyph.y - hoveredGlyph.yoffset;
			int y2 = y1 + font.base;
			int y3 = y1 + font.lineHeight;
			
			g2d.setColor(SELECT_COLOR);
			g2d.drawLine(r.x * s, y1 * s, (r.x + r.width) * s, y1 * s);
			g2d.drawLine(r.x * s, y2 * s, (r.x + r.width) * s, y2 * s);
			g2d.drawLine(r.x * s, y3 * s, (r.x + r.width) * s, y3 * s);
		}
		if(selectedGlyph != null)
		{
			Rectangle r = selectedGlyph.r;
			
			drawRect(g2d, r, HOVER_COLOR);
			
			int y1 = selectedGlyph.y - selectedGlyph.yoffset;
			int y2 = y1 + font.base;
			int y3 = y1 + font.lineHeight;
			
			Stroke oldStroke = g2d.getStroke();
			
			g2d.setColor(SELECT_COLOR);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawLine(r.x * s, y1 * s, (r.x + r.width) * s, y1 * s);
			g2d.drawLine(r.x * s, y2 * s, (r.x + r.width) * s, y2 * s);
			g2d.drawLine(r.x * s, y3 * s, (r.x + r.width) * s, y3 * s);
			g2d.setStroke(oldStroke);
		}
		if(activeControl == MIDDLE || activeControl == BOTTOM)
		{
			int offset = (activeControl == MIDDLE) ? font.base : font.lineHeight;
			
			for(BitmapGlyph glyph : glyphs)
			{
				int drawY = (glyph.y - glyph.yoffset + offset) * s;
				g2d.drawLine(glyph.x * s, drawY, (glyph.x + glyph.width) * s, drawY);
			}
		}
	}
	
	public void drawRect(Graphics2D g2d, Rectangle r, Color c)
	{
		int x1 = r.x;
		int y1 = r.y;
		int w1 = r.width;
		int h1 = r.height;
		
		int s = (int) area.scale;
		
		g2d.setColor(c);
		g2d.setComposite(ALPHA);
		g2d.fillRect(x1 * s, y1 * s, w1 * s, h1 * s);
		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.drawRect(x1 * s, y1 * s, w1 * s, h1 * s);
	}

	@Override
	public ToolOptions getOptions()
	{
		return null;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		if(activeControl == NONE)
			return;
		
		int x = e.getX() - beginPress.x;
		int y = e.getY() - beginPress.y;
		
		int s = (int) area.scale;
		
		if(x < 0)
			x -= s;
		if(y < 0)
			y -= s;
		
		Point p = new Point(x / s, y / s);
		
		if(p.y == activePress.y)
			return;
		
		activePress.setLocation(p);
		
		if(activeControl != TOP)
			area.repaint();
		else
			repaint(selectedGlyph.r2, selectedGlyph.r);
		
		switch(activeControl)
		{
			case TOP:
				selectedGlyph.yoffset = beginOffset - p.y;
				break;
			case MIDDLE:
				font.base = beginBase + p.y;
				break;
			case BOTTOM:
				font.lineHeight = beginLineHeight + p.y;
				break;
		}
		selectedGlyph.updateRect();
		
		if(activeControl != TOP)
			area.repaint();
		else
			repaint(selectedGlyph.r2, selectedGlyph.r);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		updateHoveredControl(e.getX(), e.getY());
		updateHoveredGlyph(e.getX(), e.getY());
	}
	
	public void updateHoveredControl(int x, int y)
	{
		hoveredControl = NONE;
		
		if(selectedGlyph != null)
		{
			int s = (int) area.scale;
			BitmapGlyph g = selectedGlyph;
			final int TOLERANCE = 8;
			
			Rectangle boundsRect = new Rectangle(g.x * s, (g.y - g.yoffset) * s - TOLERANCE, g.width * s, font.lineHeight * s + TOLERANCE);
			if(boundsRect.contains(x, y))
			{
				int[] bounds = new int[3];
				bounds[TOP] = Math.abs(y - (g.y - g.yoffset) * s);
				bounds[MIDDLE] = Math.abs(y - (g.y - g.yoffset + font.base) * s);
				bounds[BOTTOM] = Math.abs(y - (g.y - g.yoffset + font.lineHeight) * s);
				
				if(bounds[TOP] < TOLERANCE && bounds[TOP] < bounds[MIDDLE] && bounds[TOP] < bounds[BOTTOM])
					hoveredControl = TOP;
				else if(bounds[MIDDLE] < TOLERANCE && bounds[MIDDLE] < bounds[TOP] && bounds[MIDDLE] < bounds[BOTTOM])
					hoveredControl = MIDDLE;
				else if(bounds[BOTTOM] < TOLERANCE && bounds[BOTTOM] < bounds[TOP] && bounds[BOTTOM] < bounds[MIDDLE])
					hoveredControl = BOTTOM;
			}
		}
		
		updateCursor();
	}
	
	public void updateCursor()
	{
		switch(hoveredControl)
		{
			case TOP: case MIDDLE: case BOTTOM:
				setCursor("ud");
				break;
			case NONE:
				setCursor("default");
				break;
		}
	}
	
	private HashMap<String, Cursor> cursors;
	
	public void setCursor(String url)
	{
		if(cursors == null)
		{
			cursors = new HashMap<String, Cursor>();
			cursors.put("default", area.getCursor());
		}
		
		if(!cursors.containsKey(url))
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = Resources.loadIcon("arrows/" + url + ".png").getImage();
			Cursor c = toolkit.createCustomCursor(image , new Point(8, 8), url);
			cursors.put(url, c);
		}
		
		area.setCursor(cursors.get(url));
	}
	
	public void updateHoveredGlyph(int x, int y)
	{
		x /= (int) area.scale;
		y /= (int) area.scale;
		
		BitmapGlyph oldHovered = hoveredGlyph;
		
		if(hoveredControl != NONE)
			hoveredGlyph = selectedGlyph;
		else
		{
			if(hoveredGlyph != null)
			{
				if(hoveredGlyph.r.contains(new Point(x, y)))
					return;
				else
					hoveredGlyph = null;
			}
			if(hoveredGlyph == null)
			{
				Point p = new Point(x, y);
				
				for(BitmapGlyph g : glyphs)
				{
					if(g.r.contains(p))
					{
						hoveredGlyph = g;
					}
				}
			}
		}
		
		if(oldHovered != hoveredGlyph)
		{
			if(oldHovered != null)
				repaint(oldHovered.r2, oldHovered.r);
			if(hoveredGlyph != null)
				repaint(hoveredGlyph.r2, hoveredGlyph.r);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		if(selectedGlyph != null && selectedGlyph != hoveredGlyph)
			repaint(selectedGlyph.r2, selectedGlyph.r);
		
		selectedGlyph = hoveredGlyph;
		
		if(selectedGlyph == null)
		{
			beginPress = null;
			beginPos = beginOffset = beginBase = beginLineHeight = 0;
			activeControl = NONE;
		}
		else
		{
			beginPress = new Point(e.getX(), e.getY());
			activePress = new Point(e.getX() / (int) area.scale, e.getY() / (int) area.scale);
			
			updateHoveredControl(e.getX(), e.getY());
			activeControl = hoveredControl;
			if(activeControl == TOP)
			{
				beginPos = selectedGlyph.y - selectedGlyph.yoffset;
				beginOffset = selectedGlyph.yoffset;
				beginBase = font.base;
				beginLineHeight = font.lineHeight;
			}
			else if(activeControl == MIDDLE)
			{
				beginPos = selectedGlyph.y - selectedGlyph.yoffset + font.base;
				beginOffset = selectedGlyph.yoffset;
				beginBase = font.base;
				beginLineHeight = font.lineHeight;
			}
			else if(activeControl == BOTTOM)
			{
				beginPos = selectedGlyph.y - selectedGlyph.yoffset + font.lineHeight;
				beginOffset = selectedGlyph.yoffset;
				beginBase = font.base;
				beginLineHeight = font.lineHeight;
			}
			else
				beginPos = beginOffset = beginBase = beginLineHeight = 0;
			
			if(activeControl == MIDDLE || activeControl == BOTTOM)
				area.repaint();
			else
				repaint(selectedGlyph.r2, selectedGlyph.r);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		if(selectedGlyph != null && activeControl != NONE)
		{
			if(activeControl != TOP)
				for(BitmapGlyph g : glyphs)
					g.updateRect();
			else
				selectedGlyph.updateRect();
			
			area.setDirty();
		}
		
		if(activeControl == MIDDLE || activeControl == BOTTOM)
			area.repaint();
		
		beginPress = null;
		activePress = null;
		beginPos = beginOffset = beginBase = beginLineHeight = 0;
		activeControl = NONE;
	}
	
	public void repaint(Rectangle yBounds, Rectangle xBounds)
	{
		area.repaint(xBounds.x - 1, yBounds.y - 1, xBounds.width + 2, yBounds.height + 2);
	}
}
