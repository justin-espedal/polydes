package com.polydes.common.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class VerticalDivider extends JComponent
{
	public int width;
	public Color color;
	
	public VerticalDivider(int width)
	{
		color = new Color(0x4F4F4F);
		this.width = width;
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(width, 1);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(width, 1);
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(width, Short.MAX_VALUE);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}