package stencyl.ext.polydes.datastruct.ui.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class SnappingDialog extends JDialog
{
	public static final int NOT_SNAPPED = 0;
	
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 4;
	public static final int LEFT = 8;
	
	public static final int TOP_RIGHT = 3;
	public static final int BOTTOM_RIGHT = 6;
	public static final int TOP_LEFT = 9;
	public static final int BOTTOM_LEFT = 12;
	
	public static final int HORIZONTAL = 5;
	public static final int VERTICAL = 10;
	
	private int snapped = NOT_SNAPPED;
	private Component snapTo;
	private Component root;
	
	private int sd = 10;
	
	private Integer xPos;
	private Integer yPos;

	public SnappingDialog(JDialog owner)
	{
		super(owner);
	}
	
	public void snapToComponent(Component snapTo, int newSnap)
	{
		this.snapTo = snapTo;
		this.snapped = newSnap == -1 ? this.snapped : newSnap;
		if(snapTo == null)
			return;
		
		xPos = 0;
		yPos = 0;
		
		snapTo.addComponentListener(snapToChanged);
		snapTo.addHierarchyListener(snapToHierarchyChanged);
		root = SwingUtilities.getRoot(snapTo);
		if(root != null)
			root.addComponentListener(snapToChanged);
		update();
	}
	
	public void setSnap(int newSnap)
	{
		snapped = newSnap;
		update();
	}
	
	private ComponentAdapter snapToChanged = new ComponentAdapter()
	{
		@Override
		public void componentMoved(ComponentEvent e)
		{
			update();
		}
		
		@Override
		public void componentResized(ComponentEvent e)
		{
			update();
		}
		
		@Override
		public void componentHidden(ComponentEvent e)
		{
			
		};
		
		@Override
		public void componentShown(ComponentEvent e)
		{
			
		}; 
	};
	
	private HierarchyListener snapToHierarchyChanged = new HierarchyListener()
	{
		@Override
		public void hierarchyChanged(HierarchyEvent e)
		{
			Component newRoot = SwingUtilities.getRoot(snapTo);
			if(root == newRoot)
				return;
			
			if(root != null)
				root.removeComponentListener(snapToChanged);
			if(newRoot != null)
				newRoot.addComponentListener(snapToChanged);
			
			root = newRoot;
			
			update();
		}
	};
	
	private void update()
	{
		if(snapped == NOT_SNAPPED)
			return;
		
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, snapTo);
		
		Rectangle snapToBounds = snapTo.getBounds();
		snapToBounds.setLocation(p);
		
		int bx1 = snapToBounds.x;
		int by1 = snapToBounds.y;
		int bx2 = snapToBounds.x + snapToBounds.width;
		int by2 = snapToBounds.y + snapToBounds.height;
		
		int x = getX();
		int y = getY();
		
		if ((snapped & TOP) == TOP)
			y = by1;
		else if ((snapped & BOTTOM) == BOTTOM)
			y = (int) by2 - getHeight();
		else
			y = by1 + yPos;
		
		if ((snapped & LEFT) == LEFT)
			x = bx1;
		else if ((snapped & RIGHT) == RIGHT)
			x = (int) bx2 - getWidth();
		else
			x = xPos + bx1;
		
		super.setLocation(x, y);
	}
	
	@Override
	public void setLocation(int x, int y)
	{
		if(snapTo == null)
			super.setLocation(x, y);
		else
		{
			Point p = getSnapPoint(x, y);
			super.setLocation(p.x, p.y);
		}
	}
	
	@Override
	public void setLocation(Point p)
	{
		if(snapTo == null)
			super.setLocation(p);
		else
			super.setLocation(getSnapPoint(p.x, p.y));
	}
	
	@Override
	public void setSize(Dimension d)
	{
		super.setSize(d);
		if(snapTo != null)
			update();
	}
	
	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		if(snapTo != null)
			update();
	}
	
	@Override
	public void pack()
	{
		super.pack();
		if(snapTo != null)
			update();
		repaint();
	}
	
	public Point getSnapPoint(int x, int y)
	{
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, snapTo);
		
		Rectangle snapToBounds = snapTo.getBounds();
		snapToBounds.setLocation(p);
		
		int bx1 = snapToBounds.x;
		int by1 = snapToBounds.y;
		int bx2 = snapToBounds.x + snapToBounds.width;
		int by2 = snapToBounds.y + snapToBounds.height;
		
		snapped = NOT_SNAPPED;
		
		// top
		if (Math.abs(y - by1) < sd)
		{
			y = by1;
			snapped += TOP;
		}
		// bottom
		else if (Math.abs(y + getHeight() - by2) < sd)
		{
			y = (int) by2 - getHeight();
			snapped += BOTTOM;
		}
		
		// left
		if (Math.abs(x - bx1) < sd)
		{
			x = bx1;
			snapped += LEFT;
		}
		// right
		else if (Math.abs(x + getWidth() - bx2) < sd)
		{
			x = (int) bx2 - getWidth();
			snapped += RIGHT;
		}
		
		if((HORIZONTAL&snapped) == 0)
			yPos = y - by1;
		if((VERTICAL&snapped) == 0)
			xPos = x - bx1;
		
		// make sure we don't get into a recursive loop when the
		// set location generates more events
		return new Point(x, y);
	}
}
