package com.polydes.datastruct.data.types.builtin;

import com.polydes.datastruct.data.types.DataType;

public abstract class BuiltinType<T> extends DataType<T>
{
	public BuiltinType(Class<T> javaType, String haxeType, String stencylType)
	{
		super(javaType, haxeType, stencylType);
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
}