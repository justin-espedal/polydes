package com.polydes.paint.app.editors.image.tools;

import java.awt.Graphics;

import com.polydes.paint.app.editors.image.DrawArea;

public interface Tool
{
	public void setArea(DrawArea area);
	public void press(int x, int y);
	public void drag(int x, int y);
	public void release(int x, int y);
	public void enter(int x, int y);
	public void exit(int x, int y);
	public void move(int x, int y);
	public void render(Graphics g, int x, int y, int w, int h);
	public ToolOptions getOptions();
}