package stencyl.ext.polydes.datastruct.data.core;

public class ExtrasImage implements Comparable<ExtrasImage>
{
	public String name;

	@Override
	public int compareTo(ExtrasImage img)
	{
		if(img == null)
			return 0;
		else
			return name.compareTo(img.name);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
