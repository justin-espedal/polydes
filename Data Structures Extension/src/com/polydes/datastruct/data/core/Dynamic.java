package com.polydes.datastruct.data.core;

import com.polydes.datastruct.data.types.HaxeDataType;

public class Dynamic
{
	public HaxeDataType type;
	public Object value;
	
	public Dynamic(Object value, HaxeDataType type)
	{
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return type.dataType.checkToDisplayString(value);
	}
}