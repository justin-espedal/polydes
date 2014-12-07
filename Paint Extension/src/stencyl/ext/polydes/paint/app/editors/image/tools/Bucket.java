package stencyl.ext.polydes.paint.app.editors.image.tools;

import java.awt.Graphics;

import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.app.editors.image.ImageUtils;

public class Bucket implements Tool
{

	private DrawArea area;

	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		java.awt.Rectangle r = ImageUtils.fillPixels(area.img, x, y, area.currentRGB);
		area.repaint(r.x, r.y, r.width, r.height);
		area.setDirty();
	}

	@Override
	public void drag(int x, int y)
	{
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
