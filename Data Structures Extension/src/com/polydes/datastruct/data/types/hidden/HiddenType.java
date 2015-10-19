package com.polydes.datastruct.data.types.hidden;

import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;

public abstract class HiddenType<T> extends DataType<T>
{
	public HiddenType(Class<T> cls, String haxeType)
	{
		super(cls, haxeType, "");
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return null;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
}