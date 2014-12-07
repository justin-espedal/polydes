package stencyl.ext.polydes.paint.app.editors.image.tools;

import java.awt.Graphics;
import java.awt.Point;

import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.app.editors.image.ImageUtils;
import stencyl.ext.polydes.paint.app.editors.image.ShapeUtils;

public class Pencil implements Tool
{
	DrawArea area;
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		ImageUtils.drawPoints(area.img, new Point[] {new Point(x, y)}, area.currentRGB);
		area.repaint(x, y, 1, 1);
		area.setDirty();
	}

	@Override
	public void drag(int x, int y)
	{
ShapeUtils.clip(0, 0, area.img.getWidth(), area.img.getHeight());
		
		if(Math.abs(area.lastUpdated.x - x) + Math.abs(area.lastUpdated.y - y) >= 2)
		{
			ImageUtils.drawPoints(area.img, ShapeUtils.getLine(area.lastUpdated.x, area.lastUpdated.y, x, y), area.currentRGB);
			int x1 = Math.min(area.lastUpdated.x, x);
			int y1 = Math.min(area.lastUpdated.y, y);
			int w = Math.max(area.lastUpdated.x, x) - x1 + 1;
			int h = Math.max(area.lastUpdated.y, y) - y1 + 1;
			
			area.repaint(x1, y1, w, h);
		}
		else
		{
			ImageUtils.drawPoints(area.img, new Point[] {new Point(x, y)}, area.currentRGB);
			area.repaint(x, y, 1, 1);
		}
		
		area.setDirty();
	}

	@Override
	public void release(int x, int y)
	{
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
	}

}
