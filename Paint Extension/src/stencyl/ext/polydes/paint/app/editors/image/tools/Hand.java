package stencyl.ext.polydes.paint.app.editors.image.tools;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import stencyl.ext.polydes.paint.app.editors.image.DrawArea;

public class Hand implements Tool, MouseListener, MouseMotionListener
{
	private DrawArea area;
	private Point beginPress;
	private Rectangle beginRect;
	private JComponent immobilePane;
	
	public void setImmobilePane(JComponent c)
	{
		immobilePane = c;
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
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
	public void mouseDragged(MouseEvent e)
	{
		if(area == null || area.currentTool != this)
			return;
		if(!area.contains(e.getPoint()))
			return;
		
		MouseEvent e2 = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, immobilePane);
		
		java.awt.Rectangle r = (java.awt.Rectangle) beginRect.clone();
		r.x += (beginPress.x - e2.getX());
		r.y += (beginPress.y - e2.getY());
		
		area.scrollRectToVisible(r);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
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
		
		MouseEvent e2 = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, immobilePane);
		
		beginPress = new Point(e2.getX(), e2.getY());
		beginRect = (java.awt.Rectangle) area.getVisibleRect().clone();
	}

	@Override
	public void mouseReleased(MouseEvent e)
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
	}
}