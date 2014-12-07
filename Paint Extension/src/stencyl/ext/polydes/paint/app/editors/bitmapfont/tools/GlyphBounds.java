package stencyl.ext.polydes.paint.app.editors.bitmapfont.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
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
import stencyl.ext.polydes.paint.data.BitmapGlyph;
import stencyl.ext.polydes.paint.res.Resources;

public class GlyphBounds implements Tool, MouseListener, MouseMotionListener
{
	public static final float TRANSPARENCY = .2f;
	public static final Color SELECT_COLOR = Color.ORANGE;
	public static final Color HOVER_COLOR = Color.BLUE;
	
	public static final AlphaComposite ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY);
	
	public static final int NONE = -1;
	public static final int RIGHT = 0;
	public static final int UPRIGHT = 1;
	public static final int UP = 2;
	public static final int UPLEFT = 3;
	public static final int LEFT = 4;
	public static final int DOWNLEFT = 5;
	public static final int DOWN = 6;
	public static final int DOWNRIGHT = 7;
	public static final int CENTER = 8;
	
	FontDrawArea area;
	ArrayList<BitmapGlyph> glyphs;
	BitmapGlyph hoveredGlyph;
	public BitmapGlyph selectedGlyph;
	int hoveredControl;
	int activeControl;
	
	Point beginPress;
	Point activePress;
	Rectangle activeBounds;
	
	public GlyphBounds()
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
		
		if(activeBounds == null && hoveredGlyph != null && hoveredGlyph != selectedGlyph)
			drawRect(g2d, hoveredGlyph.r, HOVER_COLOR);
		
		if(activeBounds != null)
			drawRect(g2d, activeBounds, SELECT_COLOR);
		else if(selectedGlyph != null)
			drawRect(g2d, selectedGlyph.r, SELECT_COLOR);
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
		
		if(p.equals(activePress))
			return;
		
		activePress.setLocation(p);
		
		Rectangle r = (Rectangle) selectedGlyph.r.clone();
		
		repaint(activeBounds);
		
		switch(activeControl)
		{
			case RIGHT:
				activeBounds.width = r.width + p.x;
				break;
			case UPRIGHT:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				activeBounds.width = r.width + p.x;
				break;
			case UP:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				break;
			case UPLEFT:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case LEFT:
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case DOWNLEFT:
				activeBounds.height = r.height + p.y;
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case DOWN:
				activeBounds.height = r.height + p.y;
				break;
			case DOWNRIGHT:
				activeBounds.height = r.height + p.y;
				activeBounds.width = r.width + p.x;
				break;
			case CENTER:
				activeBounds.x = r.x + p.x;
				activeBounds.y = r.y + p.y;
				break;
		}
		
		repaint(activeBounds);
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
			final int TOLERANCE = 8;
			
			Rectangle boundsRect = new Rectangle((r.x * s) - TOLERANCE, (r.y * s) - TOLERANCE, (r.width * s) + TOLERANCE * 2, (r.height * s) + TOLERANCE * 2);
			if(boundsRect.contains(x, y))
			{
				boolean[] bounds = new boolean[]
				{
					Math.abs(x - (r.x + r.width) * s) <= TOLERANCE,
					false,
					Math.abs(y - r.y * s) <= TOLERANCE,
					false,
					Math.abs(x - r.x * s) <= TOLERANCE,
					false,
					Math.abs(y - (r.y + r.height) * s) <= TOLERANCE,
					false
				};
				
				if(bounds[UP] && bounds[DOWN])
				{
					bounds[UP] = Math.abs(y - r.y * s) < Math.abs(y - (r.y + r.height) * s);
					bounds[DOWN] = !bounds[UP];
				}
				if(bounds[LEFT] && bounds[RIGHT])
				{
					bounds[LEFT] = Math.abs(x - r.x * s) < Math.abs(x - (r.x + r.width) * s);
					bounds[RIGHT] = !bounds[LEFT];
				}
				
				bounds[UPRIGHT] = bounds[UP] && bounds[RIGHT];
				bounds[UPLEFT] = bounds[UP] && bounds[LEFT];
				bounds[DOWNRIGHT] = bounds[DOWN] && bounds[RIGHT];
				bounds[DOWNLEFT] = bounds[DOWN] && bounds[LEFT];
				
				if(bounds[UPRIGHT])
					hoveredControl = UPRIGHT;
				else if(bounds[UPLEFT])
					hoveredControl = UPLEFT;
				else if(bounds[DOWNRIGHT])
					hoveredControl = DOWNRIGHT;
				else if(bounds[DOWNLEFT])
					hoveredControl = DOWNLEFT;
				else if(bounds[RIGHT])
					hoveredControl = RIGHT;
				else if(bounds[UP])
					hoveredControl = UP;
				else if(bounds[LEFT])
					hoveredControl = LEFT;
				else if(bounds[DOWN])
					hoveredControl = DOWN;
				else
					hoveredControl = CENTER;
			}
		}
		
		updateCursor();
	}
	
	public void updateCursor()
	{
		switch(hoveredControl)
		{
			case UP: case DOWN:
				setCursor("ud");
				break;
			case RIGHT: case LEFT:
				setCursor("rl");
				break;
			case UPRIGHT: case DOWNLEFT:
				setCursor("ur");
				break;
			case UPLEFT: case DOWNRIGHT:
				setCursor("dr");
				break;
			case CENTER:
				setCursor("udrl");
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
				repaint(oldHovered.r);
			if(hoveredGlyph != null)
				repaint(hoveredGlyph.r);
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
			repaint(selectedGlyph.r);
		
		selectedGlyph = hoveredGlyph;
		
		if(selectedGlyph == null)
		{
			beginPress = null;
			activePress = null;
			activeBounds = null;
			activeControl = NONE;
		}
		else
		{
			beginPress = new Point(e.getX(), e.getY());
			activePress = (Point) beginPress.clone();
			activeBounds = (Rectangle) selectedGlyph.r.clone();
			
			updateHoveredControl(e.getX(), e.getY());
			activeControl = hoveredControl;
			
			repaint(selectedGlyph.r);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		
		if(selectedGlyph != null && activeBounds != null)
		{
			selectedGlyph.x = activeBounds.x;
			selectedGlyph.y = activeBounds.y;
			selectedGlyph.width = activeBounds.width;
			selectedGlyph.height = activeBounds.height;
			selectedGlyph.updateRect();
			
			area.setDirty();
		}
		
		beginPress = null;
		activePress = null;
		activeBounds = null;
		activeControl = NONE;
	}
	
	public void repaint(Rectangle r)
	{
		area.repaint(r.x, r.y, r.width + 1, r.height + 1);
	}
}
