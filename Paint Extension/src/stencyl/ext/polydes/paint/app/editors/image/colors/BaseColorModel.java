package stencyl.ext.polydes.paint.app.editors.image.colors;

public class BaseColorModel implements ColorSlideModel
{
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	
	private int mode;
	private int value1;
	private int value2;
	
	public BaseColorModel(int mode)
	{
		this.mode = mode;
	}
	
	public void setValues(float value1, float value2)
	{
		this.value1 = (int) value1;
		this.value2 = (int) value2;
	}
	
	@Override
	public int[] getGradient(int size)
	{
		int[] values = new int[size];
		
		int baseColor;
		int modeColor;
		
		switch(mode)
		{
			case RED:
				baseColor = (value1<<8)|value2;
				for(int i = 0; i < size; ++i)
				{
					modeColor = (int) (i / (float) (size - 1) * 255);
					values[i] = baseColor|(modeColor<<16);
				}
				break;
			case GREEN:
				baseColor = (value1<<16)|value2;
				for(int i = 0; i < size; ++i)
				{
					modeColor = (int) (i / (float) (size - 1) * 255);
					values[i] = baseColor|(modeColor<<8);
				}
				break;
			case BLUE:
				baseColor = (value1<<16)|(value2<<8);
				for(int i = 0; i < size; ++i)
				{
					modeColor = (int) (i / (float) (size - 1) * 255);
					values[i] = baseColor|modeColor;
				}
				break;
		}
		
		return values;
	}

	@Override
	public int getDisplayValue(float value)
	{
		return (int) (value * 255);
	}

}
