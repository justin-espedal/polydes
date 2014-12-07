package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import stencyl.ext.polydes.paint.app.editors.image.CheckerboardPaint;

@SuppressWarnings("serial")
public class ColorDisplay extends JPanel
{
	public Color color;
	public int height;
	public int width;
	private CheckerboardPaint checkerboard;
	
	public ColorDisplay(int width, int height)
	{
		this.height = height;
		this.width = width;
		
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		
		checkerboard = new CheckerboardPaint(height / 4);
		color = Color.BLACK;
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(checkerboard);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(color);
		g2d.fillRect(0, 0, width, height);
	}
}
