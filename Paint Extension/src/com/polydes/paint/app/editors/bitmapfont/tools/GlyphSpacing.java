package com.polydes.paint.app.editors.bitmapfont.tools;

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

import com.polydes.paint.app.editors.bitmapfont.FontDrawArea;
import com.polydes.paint.app.editors.image.DrawArea;
import com.polydes.paint.app.editors.image.tools.Tool;
import com.polydes.paint.app.editors.image.tools.ToolOptions;
import com.polydes.paint.data.BitmapGlyph;
import com.polydes.paint.res.Resources;

public class GlyphSpacing implements Tool, MouseListener, MouseMotionListener
{
	public static final float TRANSPARENCY = .2f;
	public static final Color HOVER_COLOR = Color.BLUE;
	public static final Color SELECT_COLOR = Color.ORANGE;
	
	public static final AlphaComposite ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY);
	
	public static final int NONE = -1;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	FontDrawArea area;
	ArrayList<BitmapGlyph> glyphs;
	BitmapGlyph hoveredGlyph;
	BitmapGlyph selectedGlyph;
	
	int hoveredControl;
	int activeControl;
	int beginPos;
	int beginOffset;
	int beginAdvance;
	Point beginPress;
	Point activePress;
	
	public GlyphSpacing()
	{
		hoveredControl = NONE;
		activeControl = NONE;
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = (FontDrawArea) area;
		glyphs = this.area.font.chars;
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
		
		if(hoveredGlyph != null && hoveredGlyph != selectedGlyph)
		{
			Rectangle r = hoveredGlyph.r;
			
			//drawRect(g2d, r, HOVER_COLOR);
			
			int s = (int) area.scale;
			int x1 = hoveredGlyph.x - hoveredGlyph.xoffset;
			int x2 = x1 + hoveredGlyph.xadvance;
			
			g2d.setColor(SELECT_COLOR);
			g2d.drawLine(x1 * s, r.y * s, x1 * s, (r.y + r.height) * s);
			g2d.drawLine(x2 * s, r.y * s, x2 * s, (r.y + r.height) * s);
		}
		if(selectedGlyph != null)
		{
			Rectangle r = selectedGlyph.r;
			
			drawRect(g2d, r, HOVER_COLOR);
			
			int s = (int) area.scale;
			int x1 = selectedGlyph.x - selectedGlyph.xoffset;
			int x2 = x1 + selectedGlyph.xadvance;
			
			Stroke oldStroke = g2d.getStroke();
			
			g2d.setColor(SELECT_COLOR);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawLine(x1 * s, r.y * s, x1 * s, (r.y + r.height) * s);
			g2d.drawLine(x2 * s, r.y * s, x2 * s, (r.y + r.height) * s);
			g2d.setStroke(oldStroke);
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
		
		if(p.x == activePress.x)
			return;
		
		activePress.setLocation(p);
		
		repaint(selectedGlyph.r2, selectedGlyph.r);
		
		switch(activeControl)
		{
			case RIGHT:
				selectedGlyph.xadvance = beginAdvance + p.x;
				break;
			case LEFT:
				selectedGlyph.xoffset = beginOffset - p.x;
				selectedGlyph.xadvance = beginAdvance - p.x;
				break;
		}
		selectedGlyph.updateRect();
		
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
			Rectangle r = (Rectangle) selectedGlyph.r;
			Rectangle r2 = (Rectangle) selectedGlyph.r2;
			final int TOLERANCE = 8;
			
			Rectangle boundsRect = new Rectangle((r2.x * s) - TOLERANCE, r.y * s, (r2.width * s) + TOLERANCE * 2, r.height * s);
			if(boundsRect.contains(x, y))
			{
				boolean[] bounds = new boolean[2];
				bounds[LEFT] = Math.abs(x - r2.x * s) <= TOLERANCE;
				bounds[RIGHT] = Math.abs(x - (r2.x + r2.width) * s) <= TOLERANCE;
				
				if(bounds[LEFT] && bounds[RIGHT])
				{
					bounds[LEFT] = Math.abs(x - r2.x * s) < Math.abs(x - (r2.x + r2.width) * s);
					bounds[RIGHT] = !bounds[LEFT];
				}
				
				if(bounds[RIGHT])
					hoveredControl = RIGHT;
				else if(bounds[LEFT])
					hoveredControl = LEFT;
			}
		}
		
		updateCursor();
	}
	
	public void updateCursor()
	{
		switch(hoveredControl)
		{
			case RIGHT: case LEFT:
				setCursor("rl");
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
			beginPos = beginOffset = beginAdvance = 0;
			activeControl = NONE;
		}
		else
		{
			beginPress = new Point(e.getX(), e.getY());
			activePress = new Point(e.getX() / (int) area.scale, e.getY() / (int) area.scale);
			
			updateHoveredControl(e.getX(), e.getY());
			activeControl = hoveredControl;
			if(activeControl == LEFT)
			{
				beginPos = selectedGlyph.x - selectedGlyph.xoffset;
				beginOffset = selectedGlyph.xoffset;
				beginAdvance = selectedGlyph.xadvance;
			}
			else if(activeControl == RIGHT)
			{
				beginPos = selectedGlyph.x - selectedGlyph.xoffset + selectedGlyph.xadvance;
				beginOffset = selectedGlyph.xoffset;
				beginAdvance = selectedGlyph.xadvance;
			}
			else
				beginPos = beginOffset = beginAdvance = 0;
			
			
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
			selectedGlyph.updateRect();
			area.setDirty();
		}
		
		beginPress = null;
		activePress = null;
		beginPos = beginOffset = beginAdvance = 0;
		activeControl = NONE;
	}
	
	public void repaint(Rectangle xBounds, Rectangle yBounds)
	{
		area.repaint(xBounds.x - 1, yBounds.y - 1, xBounds.width + 2, yBounds.height + 2);
	}
}
