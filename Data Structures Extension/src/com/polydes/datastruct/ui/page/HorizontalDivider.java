package com.polydes.datastruct.ui.page;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class HorizontalDivider extends JComponent
{
	public int height;
	public Color color;
	
	public HorizontalDivider(int height)
	{
		color = new Color(0x4F4F4F);
		this.height = height;
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(1, height);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(1, height);
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Short.MAX_VALUE, height);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}