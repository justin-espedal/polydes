package com.polydes.datastruct.ui.utils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

public class WindowDragger implements MouseListener, MouseMotionListener
{
	private Component comp;
	private Point beginPos;
	private Point beginDrag;
	private Point lastMousePos;
	
	public WindowDragger(Component comp)
	{
		this.comp = comp;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		Point curPoint = e.getPoint();
		SwingUtilities.convertPointToScreen(curPoint, (Component) e.getSource());
		
		if(curPoint.equals(lastMousePos))
			return;
		lastMousePos.setLocation(curPoint);
		
		int x = beginPos.x + curPoint.x - beginDrag.x;
		int y = beginPos.y + curPoint.y - beginDrag.y;
		comp.setLocation(x, y);
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
		beginPos = comp.getLocation();
		beginDrag = e.getPoint();
		SwingUtilities.convertPointToScreen(beginDrag, (Component) e.getSource());
		
		lastMousePos = new Point(beginDrag);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		beginPos = null;
		beginDrag = null;
	}
}
