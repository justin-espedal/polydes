package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ColorValueGrid extends JPanel implements MouseListener, MouseMotionListener
{
	public float xValue;
	public float yValue;
	
	public ArrayList<ActionListener> listeners;
	
	private int height;
	private int width;
	public ColorGridModel model;
	
	private BufferedImage img;
	
	public ColorValueGrid(int width, int height, ColorGridModel model)
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.width = width;
		this.height = height;
		
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		
		this.model = model;
		model.setPrimary(0, 0);
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		updateImg();
		
		listeners = new ArrayList<ActionListener>();
	}
	
	public void updateImg()
	{
		int[][] gradient = model.getGradient(width, height);
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				img.setRGB(x, y, gradient[y][x]);
			}
		}
	}
	
	public void updateValue(float xRatio, float yRatio)
	{
		if(xValue != xRatio || yValue != yRatio)
		{
			xValue = xRatio;
			yValue = yRatio;
			
			ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
			for(ActionListener l : listeners)
			{
				l.actionPerformed(e);
			}
		}
	}
	
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.BLACK);
		g.fillRect(0, (int) (getHeight() - (yValue * getHeight())), getWidth(), 1);
		g.fillRect((int) (xValue * getWidth()), 0, 1, getHeight());;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(!contains(e.getPoint()))
			return;
		
		updateValue(e.getX() / (float) (width - 1), 1 - (e.getY() / (float) (height - 1)));
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(!contains(e.getPoint()))
			return;
		
		updateValue(e.getX() / (float) (width - 1), 1 - (e.getY() / (float) (height - 1)));
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

}
