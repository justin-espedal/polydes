package stencyl.ext.polydes.dialog.data;

public class RatioPoint
{
	protected RatioInt x;
	protected RatioInt y;
	
	public RatioPoint(String xData, String yData)
	{
		x = new RatioInt(xData);
		y = new RatioInt(yData);
	}
	
	public String getX()
	{
		return x.get();
	}
	
	public String getY()
	{
		return y.get();
	}
	
	public void setX(String data)
	{
		x.set(data);
	}
	
	public void setY(String data)
	{
		y.set(data);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof RatioPoint))
			return false;
		
		RatioPoint p = (RatioPoint) o;
		return (p.x.equals(x) && p.y.equals(y));
	}
	
	@Override
	public String toString()
	{
		return "[" + x.get() + ", " + y.get() + "]";
	}
}