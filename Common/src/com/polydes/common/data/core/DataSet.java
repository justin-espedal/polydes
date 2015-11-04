package com.polydes.common.data.core;

import java.util.HashSet;

import com.polydes.common.data.types.DataType;

public class DataSet extends HashSet<Object>
{
	public DataType<?> genType;
	
	public DataSet(DataType<?> genType)
	{
		this.genType = genType;
	}
}