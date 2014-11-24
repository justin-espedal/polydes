package stencyl.ext.polydes.scenelink.ui.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stencyl.ext.polydes.scenelink.util.SwingUtil;
import stencyl.sw.SW;


public class ColorDisplay extends JPanel
{
	public Color color;
	public int height;
	public int width;
	private CheckerboardPaint checkerboard;
	private ColorDialog colorDialog;
	
	private ArrayList<ActionListener> listeners;
	
	public ColorDisplay(int width, int height, Color color, final JWindow owner)
	{
		this.height = height;
		this.width = width;
		
		listeners = new ArrayList<ActionListener>();
		
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		
		checkerboard = new CheckerboardPaint(height / 4);
		this.color = color;
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				colorDialog = new ColorDialog(ColorDisplay.this, owner);
				colorDialog.setVisible(true);
				Point p = e.getPoint();
				SwingUtilities.convertPointToScreen(p, ColorDisplay.this);
				p = SwingUtil.getPositionWithinWindow(colorDialog, SW.get(), p);
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
		});
	}
	
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(checkerboard);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(color);
		g2d.fillRect(0, 0, width, height);
	}
}
