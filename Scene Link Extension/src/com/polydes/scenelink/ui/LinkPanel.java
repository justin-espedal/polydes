package com.polydes.scenelink.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.polydes.scenelink.data.Link;
import com.polydes.scenelink.data.LinkModel;


public class LinkPanel extends JComponent implements PropertyChangeListener
{
	private static Font linkFont = new Font("Verdana", Font.BOLD, 10);
	
	private LinkModel model;
	
	private LinkPage page;
	
	private String label;
	private Color color;
	
	private Rectangle drawRect;
	
	private Link link;
	
	private boolean hovered;
	private boolean selected;
	
	public LinkPanel
	(
		LinkModel model
	)
	{
		this.model = model;
		model.addPropertyChangeListener(this);
		
		refresh();
		
		hovered = false;
		selected = false;
	}
	
	public void refresh()
	{
		label = model.getLabel();
		color = model.getColor();
		drawRect = model.getPos();
		setBounds(drawRect);
		link = model.getLink();
		revalidate();
		if(page != null)
			page.repaint(this);
	}
	
	public LinkPanel
	(
		LinkPage page,
		Color color,
		Rectangle drawRect
	)
	{
		model = null;
		this.page = page;
		label = "";
		this.color = color;
		this.drawRect = drawRect;
		setBounds(drawRect);
		link = Link.createBlank();
	}
	
	public LinkModel getModel()
	{
		return model;
	}
	
	public void setParent(LinkPage page)
	{
		this.page = page;
	}
	
	public void setDrawRect(Rectangle drawRect)
	{
		this.drawRect = drawRect;
		setBounds(drawRect);
	}
	
	public void openLink()
	{
		if(link != null)
			link.open();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(selected)
			g.setColor(brighter(color, 1f));
		else if(hovered)
			g.setColor(brighter(color, .2f));
		else
			g.setColor(color);
		g.fillRect(0, 0, drawRect.width, drawRect.height);
		
//		if(selected)
//		{
//			g.setColor(alpha(Color.WHITE, .5f));
//			g.fillRect(0, 0, drawRect.width, drawRect.height);
//		}
		if(!label.equals(""))
		{
			int center = drawRect.width / 2;
			g.setFont(linkFont);
			int labelX = center - getStringWidth(g, label) / 2;
			g.setColor(Color.WHITE);
			g.drawString(label, labelX, 15);
		}
		
		paintBorder(g);
	}

	public Link getLink()
	{
		return link;
	}

	public void setLink(Link link)
	{
		this.link = link;
	}

	@Override
	protected void paintBorder(Graphics g)
	{
		//why subtract by 1??
		if(selected)
			g.setColor(Color.WHITE);
		else if(hovered)
			g.setColor(darker(color, .1f));
		else
			g.setColor(darker(color, .2f));
		g.drawRect(0, 0, drawRect.width - 1, drawRect.height - 1);
		g.drawRect(1, 1, drawRect.width - 3, drawRect.height - 3);
	}
	
	public void paintHoveredBorders(Graphics g, boolean top, boolean bottom, boolean left, boolean right)
	{
		g.setColor(Color.GREEN);
		if(top)
			g.drawRect(0, 0, drawRect.width - 1, 1);
		if(bottom)
			g.drawRect(0, drawRect.height - 1, drawRect.width - 1, 1);
		if(left)
			g.drawRect(0, 0, 1, drawRect.height - 1);
		if(right)
			g.drawRect(drawRect.width - 1, 0, 1, drawRect.height - 1);
	}
	
	public static Color brighter(Color c, float d)
	{
		float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		Color newC = Color.getHSBColor(hsbVals[0], hsbVals[1], hsbVals[2] + d * (1f - hsbVals[2]));
		return new Color(newC.getRed(), newC.getGreen(), newC.getBlue(), c.getAlpha());
	}
	
	public static Color darker(Color c, float d)
	{
		float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		Color newC = Color.getHSBColor(hsbVals[0], hsbVals[1], (1f - d) * hsbVals[2]);
		return new Color(newC.getRed(), newC.getGreen(), newC.getBlue(), c.getAlpha());
	}
	
	public static Color alpha(Color c, float a)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) a * 255);
	}
	
	public static int getStringWidth(Graphics g, String s)
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
		Rectangle2D rect = fm.getStringBounds(s, g);
		return (int)Math.round(rect.getWidth());
	}

	public void showProperties(Point p)
	{
		p = SwingUtilities.convertPoint(page, p, this);
		JWindow w = PropertiesPage.generatePropertiesWindow(model);
		SwingUtilities.convertPointToScreen(p, this);
		w.setLocation(p);
	}

	public void setModel(LinkModel model)
	{
		if(this.model != null)
			this.model.removePropertyChangeListener(this);
		this.model = model;
		if(model != null)
			model.addPropertyChangeListener(this);
		refresh();
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public boolean isHovered()
	{
		return hovered;
	}

	public void setHovered(boolean hovered)
	{
		this.hovered = hovered;
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		String prop = e.getPropertyName();
		if(prop.equals("label"))
			label = model.getLabel();
		else if(prop.equals("color"))
			color = model.getColor();
		else if(prop.equals("pos"))
		{
			drawRect = model.getPos();
			setBounds(drawRect);
		}
		else if(prop.equals("link"))
			link = model.getLink();
		
		revalidate();
		if(page != null)
			page.repaint(this);
	}
}
