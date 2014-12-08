package stencyl.ext.polydes.dialog.data;

public class RatioInt
{
	private String data;
	
	public RatioInt(String data)
	{
		this.data = data.trim();
	}
	
	public void set(String data)
	{
		this.data = data.trim();
	}
	
	public String get()
	{
		return data;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof RatioInt))
			return false;
		
		return(((RatioInt) o).get().equals(get()));
	}
	
	@Override
	public int hashCode()
	{
		return get().hashCode();
	}
	
	@Override
	public String toString()
	{
		return data;
	}
}