package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;

public class HSVModel implements ColorGridModel
{
	public static final int HUE = 0;
	public static final int SATURATION = 1;
	public static final int VALUE = 2;
	
	public int mode;
	private float value;
	
	@Override
	public void setPrimary(int mode, float value)
	{
		this.mode = mode;
		this.value = value;
	}

	@Override
	public int[][] getGradient(int width, int height)
	{
		int[][] values = new int[height][width];
		
		float w = width - 1;
		float h = height - 1;
		
		switch(mode)
		{
			case HUE:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] = Color.HSBtoRGB(value, 1 - (y / h), x / w);
					}
				}
				break;
			case SATURATION:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] = Color.HSBtoRGB(1 - (y / h), value, x / w);
					}
				}
				break;
			case VALUE:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] = Color.HSBtoRGB(1 - (y / h), x / w, value);
					}
				}
				break;
		}
		
		return values;
	}
}
