package stencyl.ext.polydes.datastruct.ui.comp.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil;
import stencyl.sw.SW;

public class ColorDisplay extends JPanel
{
	private Color color;
	private CheckerboardPaint checkerboard;
	private ColorDialog colorDialog;
	
	private ArrayList<ActionListener> listeners;
	
	private Window owner;
	
	public ColorDisplay(int width, int height, Color color, final Window owner)
	{
		this.color = color;
		this.owner = owner;
		
		listeners = new ArrayList<ActionListener>();
		
		if(width != 0 && height != 0)
		{
			setMinimumSize(new Dimension(width, height));
			setPreferredSize(new Dimension(width, height));
			setMaximumSize(new Dimension(width, height));
			
			checkerboard = new CheckerboardPaint(height / 4);
		}
		else
			checkerboard = new CheckerboardPaint(5);
		
		this.color = color;
		
		addMouseListener(colorDisplayClicked);
	}
	
	public ColorDisplay()
	{
		this(0, 0, null, null);
	}
	
	private MouseAdapter colorDisplayClicked = new MouseAdapter()
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			colorDialog = new ColorDialog(ColorDisplay.this, owner);
			colorDialog.setVisible(true);
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, ColorDisplay.this);
			p = PopupUtil.getPositionWithinWindow(colorDialog, SW.get(), p);
			colorDialog.setLocation(p.x, p.y);
			colorDialog.setDisplayColor(getColor());
			
			colorDialog.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					for(ActionListener l : listeners)
					{
						l.actionPerformed(new ActionEvent(this, 0, ""));
					}
				}
			});
		}
	};
	
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
	
	public void setColor(Color c)
	{
		color = c;
		repaint();
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void dispose()
	{
		removeMouseListener(colorDisplayClicked);
		listeners.clear();
	}
	
	@Override
	public void paint(Graphics g)
	{
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(checkerboard);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(color);
		g2d.fillRect(0, 0, width, height);
	}

	public void setOwner(Window owner)
	{
		this.owner = owner;
	}
}
