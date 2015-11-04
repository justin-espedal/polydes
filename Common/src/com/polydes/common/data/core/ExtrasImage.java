package com.polydes.common.data.core;

//TODO: Maybe turn this into a "file" datatype, with optional root directory and filetype filters
//Remember, .png plus .PNG

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
