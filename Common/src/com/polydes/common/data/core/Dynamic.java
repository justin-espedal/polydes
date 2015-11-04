package com.polydes.common.data.core;

import com.polydes.common.data.types.DataType;

public class Dynamic
{
	public DataType<?> type;
	public Object value;
	
	public Dynamic(Object value, DataType<?> type)
	{
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return type.checkToDisplayString(value);
	}
}