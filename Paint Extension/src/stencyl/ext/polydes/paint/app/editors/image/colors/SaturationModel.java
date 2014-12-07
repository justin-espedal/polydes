package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;

public class SaturationModel implements ColorSlideModel
{
	private float hue;
	private float value;
	
	public SaturationModel()
	{
		hue = 0;
		value = 0;
	}
	
	public void updateHue(float newHue)
	{
		hue = newHue;
	}
	
	public void updateValue(float newValue)
	{
		value = newValue;
	}
	
	@Override
	public int[] getGradient(int size)
	{
		int[] values = new int[size];
		
		for(int i = 0; i < size; ++i)
		{
			values[i] = Color.HSBtoRGB(hue, i / (float) (size - 1), value);
		}
		
		return values;
	}

	@Override
	public int getDisplayValue(float value)
	{
		return (int) (value * 100);
	}
}
