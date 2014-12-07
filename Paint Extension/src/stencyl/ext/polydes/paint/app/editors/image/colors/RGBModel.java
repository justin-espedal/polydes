package stencyl.ext.polydes.paint.app.editors.image.colors;

public class RGBModel implements ColorGridModel
{
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	
	private int mode;
	private int value;
	
	@Override
	public void setPrimary(int mode, float value)
	{
		this.mode = mode;
		this.value = (int) value;
	}

	@Override
	public int[][] getGradient(int width, int height)
	{
		int[][] values = new int[height][width];
		
		float w = (width - 1) / 255f;
		float h = (height - 1) / 255f;
		
		switch(mode)
		{
			case RED:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] =
							((value           & 0x0ff) << 16) |
							((((int) (y / h)) & 0x0ff) <<  8) |
							((((int) (x / w)) & 0x0ff));
					}
				}
				break;
			case GREEN:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] =
								((((int) (y / h)) & 0x0ff) << 16) |
								((value           & 0x0ff) <<  8) |
								((((int) (x / w)) & 0x0ff));
					}
				}
				break;
			case BLUE:
				for(int x = 0; x < width; ++x)
				{
					for(int y = 0; y < height; ++y)
					{
						values[y][x] =
								((((int) (y / h)) & 0x0ff) << 16) |
								((((int) (x / w)) & 0x0ff) <<  8) |
								((value           & 0x0ff));
					}
				}
				break;
		}
		
		return values;
	}
}
