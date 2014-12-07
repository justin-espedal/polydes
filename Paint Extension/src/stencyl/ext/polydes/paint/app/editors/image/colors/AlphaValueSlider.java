package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import stencyl.ext.polydes.paint.app.editors.image.CheckerboardPaint;

@SuppressWarnings("serial")
public class AlphaValueSlider extends ColorValueSlider
{
	private CheckerboardPaint checkerboard;

	public AlphaValueSlider(int width, int height, ColorSlideModel model)
	{
		super(width, height, model);
		
		checkerboard = new CheckerboardPaint(height / 3);
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		updateImg();
	}

	@Override
	public void updateImg()
	{
		int[] gradient = model.getGradient(vertical ? height : width);
		if(vertical)
		{
			for(int y = 0; y < height; ++y)
			{
				int argb = gradient[(height - 1) - y];
				
				for(int x = 0; x < width; ++x)
				{
					img.setRGB(x, y, argb);
				}
			}
		}
		else
		{
			for(int x = 0; x < width; ++x)
			{
				int argb = gradient[x];
				
				for(int y = 0; y < height; ++y)
				{
					img.setRGB(x, y, argb);
				}
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(checkerboard);
		g2d.fillRect(0, 0, width, height);
		g2d.drawImage(img, 0, 0, null);
		g2d.setColor(Color.BLACK);
		if(vertical)
			g2d.fillRect(0, (int) (getHeight() - (value * getHeight())), getWidth(), 1);
		else
			g2d.fillRect((int) (value * getWidth()), 0, 1, getHeight());;
	}
}
