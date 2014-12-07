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
public class ColorValueSlider extends JPanel implements MouseListener, MouseMotionListener
{
	public float value;
	
	public ArrayList<ActionListener> listeners;
	
	protected int height;
	protected int width;
	protected boolean vertical;
	public ColorSlideModel model;
	
	protected BufferedImage img;
	
	public ColorValueSlider(int width, int height, ColorSlideModel model)
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.height = height;
		this.width = width;
		
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		
		vertical = (width <= height);
		
		this.model = model;
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		updateImg();
		
		listeners = new ArrayList<ActionListener>();
	}
	
	public int getDisplayValue()
	{
		return model.getDisplayValue(value);
	}
	
	public void updateImg()
	{
		int[] gradient = model.getGradient(vertical ? height : width);
		if(vertical)
		{
			for(int y = 0; y < height; ++y)
			{
				int rgb = gradient[(height - 1) - y];
				
				for(int x = 0; x < width; ++x)
				{
					img.setRGB(x, y, rgb);
				}
			}
		}
		else
		{
			for(int x = 0; x < width; ++x)
			{
				int rgb = gradient[x];
				
				for(int y = 0; y < height; ++y)
				{
					img.setRGB(x, y, rgb);
				}
			}
		}
	}
	
	public void updateValue(float ratio)
	{
		if(value != ratio)
		{
			value = ratio;
			
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
		if(vertical)
			g.fillRect(0, (int) (getHeight() - (value * getHeight())), getWidth(), 1);
		else
			g.fillRect((int) (value * getWidth()), 0, 1, getHeight());;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(!contains(e.getPoint()))
			return;
		
		updateValue(vertical ? 1 - (e.getY() / (float) (height - 1)) : e.getX() / (float) (width - 1));
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
		if(!contains(e.getPoint()))
			return;
		
		updateValue(vertical ? 1 - (e.getY() / (float) (height - 1)) : e.getX() / (float) (width - 1));
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
}