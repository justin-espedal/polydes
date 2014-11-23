package stencyl.ext.polydes.datastruct.data.core;

import java.util.ArrayList;

import stencyl.ext.polydes.datastruct.data.types.DataType;

public class DataList extends ArrayList<Object>
{
	public DataType<?> genType;
	
	public DataList(DataType<?> genType)
	{
		this.genType = genType;
	}
}