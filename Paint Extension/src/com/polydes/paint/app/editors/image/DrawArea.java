package com.polydes.paint.app.editors.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.polydes.paint.app.editors.DataItemEditor;
import com.polydes.paint.app.editors.image.tools.Tool;
import com.polydes.paint.data.ImageSource;

@SuppressWarnings("serial")
public class DrawArea extends JPanel implements DataItemEditor, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	public static final Color DRAW_AREA_COLOR = Color.WHITE;
	public static final int TRANSPARENT = new Color(0, 0, 0, 0).getRGB();
	
	private boolean changed;
	
	public BufferedImage floating;
	public Rectangle floatRect;
	public BufferedImage img; 
	public int width;
	public int height;
	public float scale;
	
	public Color currentColor;
	public int currentRGB;
	public int brushSize;
	
	public Point lastUpdated;
	
	public Tool currentTool;
	public boolean usingTool;
	
	protected Paint bgPaint;
	private CheckerboardPaint checkerboard;
	
	protected DrawArea()
	{
	}
	
	public DrawArea(ImageSource source)
	{
		load(source);
		init();
	}
	
	protected void init()
	{
		changed = false;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		
		setFocusable(true);
		
		currentColor = Color.BLACK;
		currentRGB = currentColor.getRGB();
		dim = new Dimension(0, 0);
		setScale(4);
		lastUpdated = new Point(0, 0);
		brushSize = 3;
		
		floating = null;
		floatRect = null;
		
		checkerboard = new CheckerboardPaint(8);
		bgPaint = checkerboard;
	}

	private void load(ImageSource source)
	{
		loadImg(source.getImage());
		
		source.setEditor(this);
	}
	
	protected void loadImg(BufferedImage img)
	{
		width = img.getWidth();
		height = img.getHeight();
		this.img = img;
	}
	
	@Override
	public boolean isDirty()
	{
		return changed;
	}

	@Override
	public Object getContents()
	{
		return img;
	}

	@Override
	public void setClean()
	{
		changed = false;
	}

	@Override
	public void setDirty()
	{
		changed = true;
	}

	@Override
	public void nameChanged(String name)
	{
	}
	
	@Override
	public void paint(Graphics g)
	{
		if(g.getClipBounds() != null)
		{
			Rectangle r = (Rectangle) g.getClipBounds().clone();
			if(r.width > width * scale || r.height > height * scale)
			{
				r.width = (int) (width * scale);
				r.height = (int) (height * scale);
			}
			
			int x1 = Math.max((int)(r.x / scale), 0);
			int y1 = Math.max((int)(r.y / scale), 0);
			int x2 = Math.min((int) Math.ceil((r.x + r.width) / scale), width);
			int y2 = Math.min((int) Math.ceil((r.y + r.height) / scale), height);
			
			paint(g, x1, y1, x2 - x1, y2 - y1, true);
		}
		else
		{
			super.paint(g);
			paint(g, 0, 0, width, height, false);
		}
	}
	
	public void paint(Graphics g, int x, int y, int width, int height, boolean paintBackground)
	{
		paintImage(g, x, y, width, height, paintBackground);
		paintFloating(g, x, y, width, height);
		paintTool(g, x, y, width, height);
	}
	
	public void paintImage(Graphics g, int x, int y, int width, int height, boolean paintBackground)
	{
		int s = (int) scale;
		
		if(paintBackground)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(bgPaint);
			g2d.fillRect(x * s, y * s, width * s, height * s);
		}
		
		for(int i = x; i < x + width; ++i)
		{
			for(int j = y; j < y + height; ++j)
			{
				//img.get
				g.setColor(new Color(img.getRGB(i, j), true));
				g.fillRect(i * s, j * s, s, s);
			}
		}
	}
	
	public void paintFloating(Graphics g, int x, int y, int width, int height)
	{
		int s = (int) scale;
		
		if(floating != null)
		{
			Rectangle r = floatRect.intersection(new Rectangle(x, y, width, height));
			int xOff = floatRect.x;
			int yOff = floatRect.y;
			
			for(int i = r.x; i < r.x + r.width; ++i)
			{
				for(int j = r.y; j < r.y + r.height; ++j)
				{
					g.setColor(new Color(floating.getRGB(i - xOff, j - yOff), true));
					g.fillRect(i * s, j * s, s, s);
				}
			}
		}
	}
	
	public void paintTool(Graphics g, int x, int y, int width, int height)
	{
		if(currentTool != null)
			currentTool.render(g, x, y, width, height);
	}
	
	@Override
	public void repaint(int x, int y, int width, int height)
	{
		super.repaint(
			(int) (x * scale),
			(int) (y * scale),
			(int) (width * scale),
			(int) (height * scale)
		);
	}
	
	@Override
	public void repaint(Rectangle r)
	{
		repaint(r.x, r.y, r.width, r.height);
	}
	
	private Dimension dim;
	
	@Override
	public Dimension getMaximumSize()
	{
		return dim;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return dim;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		requestFocus();
		
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		currentTool.enter(mouseX, mouseY);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		currentTool.exit(mouseX, mouseY);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		currentTool.press(mouseX, mouseY);
		
		lastUpdated.x = mouseX;
		lastUpdated.y = mouseY;
		
		usingTool = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		currentTool.release(mouseX, mouseY);
		
		lastUpdated.x = mouseX;
		lastUpdated.y = mouseY;
		
		usingTool = false;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		if(mouseX == lastUpdated.x && mouseY == lastUpdated.y)
			return;
		
		currentTool.drag(mouseX, mouseY);
		
		lastUpdated.x = mouseX;
		lastUpdated.y = mouseY;
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(currentTool == null)
			return;
		
		int mouseX = (int) (e.getX() / scale);
		int mouseY = (int) (e.getY() / scale);
		
		if(mouseX < 0 || mouseY < 0 || mouseX >= width || mouseY >= height)
			return;
		
		currentTool.move(mouseX, mouseY);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		setScale(scale - e.getWheelRotation());
	}
	
	public void setScale(float newScale)
	{
		if(newScale < 1)
			newScale = 1;
		if(newScale > 16)
			newScale = 16;
		
		if(newScale == scale)
			return;
		
		scale = newScale;
		dim.width = (int) (width * scale);
		dim.height = (int) (height * scale);
		
		if(getParent() != null && getParent() instanceof JComponent)
		{
			JComponent c = (JComponent) getParent();
			c.revalidate();
			c.repaint();
		}
	}
	
	public void setTool(Tool newTool)
	{
		if(newTool == null)
			return;
		
		currentTool = newTool;
		newTool.setArea(this);
	}
}