package stencyl.ext.polydes.paint.app.editors.image.colors;

import java.awt.Color;

public class AlphaModel implements ColorSlideModel
{
	private float hue;
	private float saturation;
	private float value;
	
	public AlphaModel()
	{
		hue = 0;
		saturation = 0;
		value = 0;
	}
	
	public void updateHue(float newHue)
	{
		hue = newHue;
	}
	
	public void updateSaturation(float newSaturation)
	{
		saturation = newSaturation;
	}
	
	public void updateValue(float newValue)
	{
		value = newValue;
	}
	
	@Override
	public int[] getGradient(int size)
	{
		int[] values = new int[size];
		
		int color = (0x00FFFFFF) & Color.HSBtoRGB(hue, saturation, value);
		int alpha;
		
		for(int i = 0; i < size; ++i)
		{
			alpha = (int) ((i / (float) (size - 1)) * 255);
			values[i] = (alpha<<24)|color;
		}
		
		return values;
	}

	@Override
	public int getDisplayValue(float value)
	{
		return (int) (value * 100);
	}
}
