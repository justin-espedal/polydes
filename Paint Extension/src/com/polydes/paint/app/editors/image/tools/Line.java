package com.polydes.paint.app.editors.image.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import com.polydes.paint.app.editors.image.DrawArea;
import com.polydes.paint.app.editors.image.ImageUtils;
import com.polydes.paint.app.editors.image.ShapeUtils;

public class Line implements Tool
{
	private DrawArea area;
	private Point beginPress;
	private Rectangle oldDraw;

	public Line()
	{
		beginPress = null;
		oldDraw = new java.awt.Rectangle(0, 0, 1, 1);
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		beginPress = new Point(x, y);
	}

	@Override
	public void drag(int x, int y)
	{
		area.repaint(oldDraw.x, oldDraw.y, oldDraw.width, oldDraw.height);
		oldDraw.x = Math.min(beginPress.x, x);
		oldDraw.y = Math.min(beginPress.y, y);
		oldDraw.width = Math.max(beginPress.x, x) - oldDraw.x + 2;
		oldDraw.height = Math.max(beginPress.y, y) - oldDraw.y + 2;
		area.repaint(oldDraw.x, oldDraw.y, oldDraw.width, oldDraw.height);
	}

	@Override
	public void release(int x, int y)
	{
		ShapeUtils.clip(0, 0, area.img.getWidth(), area.img.getHeight());
		ImageUtils.drawPoints(area.img, ShapeUtils.getLine(beginPress.x, beginPress.y, x, y), area.currentRGB);
		area.setDirty();
		
		beginPress = null;
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
		if(beginPress == null)
			return;
		
		int s = (int) area.scale;
		
		g.setColor(area.currentColor);
		ShapeUtils.clip(x, y, w, h);
		for(Point p : ShapeUtils.getLine(beginPress.x, beginPress.y, area.lastUpdated.x, area.lastUpdated.y))
		{
			g.fillRect(p.x * s, p.y * s, s, s);
		}
	}
}
