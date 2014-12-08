package stencyl.ext.polydes.paint.app.editors.image.tools;

import java.awt.Color;
import java.awt.Graphics;

import stencyl.ext.polydes.common.comp.colors.ColorDisplay;
import stencyl.ext.polydes.paint.app.editors.image.DrawArea;

public class Pick implements Tool
{
	private DrawArea area;
	private ColorDisplay display;
	
	public void setColorDisplay(ColorDisplay display)
	{
		this.display = display;
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		area.currentRGB = area.img.getRGB(x, y);
		area.currentColor = new Color(area.currentRGB, true);
		display.setColor(area.currentColor);
		display.repaint();
	}

	@Override
	public void drag(int x, int y)
	{
		area.currentRGB = area.img.getRGB(x, y);
		area.currentColor = new Color(area.currentRGB, true);
		display.setColor(area.currentColor);
		display.repaint();
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
