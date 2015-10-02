package com.polydes.datastruct.ui.utils;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import stencyl.sw.lnf.Theme;

public class Geometry
{
	public static Point getCenteredStringTopLeft(String draw, Graphics g, Font font, Component c)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		FontRenderContext context = g2d.getFontRenderContext();
		TextLayout txt = new TextLayout(draw, font, context);
		
		Rectangle2D bounds = txt.getBounds();
		int x = (int) ((c.getWidth() - (int) bounds.getWidth()) / 2);
		int y = (int) ((c.getHeight() - (bounds.getHeight() - txt.getDescent())) / 2);
		y += txt.getAscent() - txt.getDescent();
		
		return new Point(x, y);
	}
	
	public static void drawCenteredString(Component c, Graphics g, String draw, Font font)
	{
		Point drawAt = Geometry.getCenteredStringTopLeft(draw, (Graphics2D) g, font, c);
		
		g.setFont(font);
		g.setColor(Theme.TEXT_COLOR);
		g.drawString(draw, drawAt.x, drawAt.y);
	}
}
