package stencyl.ext.polydes.datastruct.data.core;

import java.util.HashSet;

import stencyl.ext.polydes.datastruct.data.types.DataType;

public class DataSet extends HashSet<Object>
{
	public DataType<?> genType;
	
	public DataSet(DataType<?> genType)
	{
		this.genType = genType;
	}
}