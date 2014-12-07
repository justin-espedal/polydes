package stencyl.ext.polydes.paint.app.editors.image.colors;

public class HueModel implements ColorSlideModel
{
	@Override
	public int getDisplayValue(float value)
	{
		return (int) (value * 360);
	}
	
	public int getRGB(int value)
	{
		int rv, gv, bv;
		rv = gv = bv = value;
		
		//red
		
		if(rv > 240)
			rv -= 360;
		
		rv = Math.abs(rv);
		
		if(rv <= 60)
			rv = 255;
		else if(rv >= 120)
			rv = 0;
		else
			rv = (int) (255 - 255 * (rv - 60) / 60);
		
		//green
		
		gv -= 120;
		gv = Math.abs(gv);
		
		if(gv <= 60)
			gv = 255;
		else if(gv >= 120)
			gv = 0;
		else
			gv = (int) (255 - 255 * (gv - 60) / 60);
		
		//blue

		bv -= 240;
		bv = Math.abs(bv);
		
		if(bv <= 60)
			bv = 255;
		else if(bv >= 120)
			bv = 0;
		else
			bv = (int) (255 - 255 * (bv - 60) / 60);
		
		//
		
		return (rv<<16)|(gv<<8)|(bv);
	}

	@Override
	public int[] getGradient(int size)
	{
		int[] values = new int[size];
		float max = size - 1;
		
		for(int i = 0; i < size; ++i)
		{
			values[i] = getRGB((int) (i / max * 360));
		}
		
		return values;
	}
}
