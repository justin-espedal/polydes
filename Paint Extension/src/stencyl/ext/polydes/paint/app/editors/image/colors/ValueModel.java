package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;

public class ValueModel implements ColorSlideModel
{
	private float hue;
	private float saturation;
	
	public ValueModel()
	{
		hue = 0;
		saturation = 0;
	}
	
	public void updateHue(float newHue)
	{
		hue = newHue;
	}
	
	public void updateSaturation(float newSaturation)
	{
		saturation = newSaturation;
	}
	
	@Override
	public int[] getGradient(int size)
	{
		int[] values = new int[size];
		
		for(int i = 0; i < size; ++i)
		{
			values[i] = Color.HSBtoRGB(hue, saturation, i / (float) (size - 1));
		}
		
		return values;
	}

	@Override
	public int getDisplayValue(float value)
	{
		return (int) (value * 100);
	}
}
