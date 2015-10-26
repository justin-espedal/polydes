package com.polydes.datastruct.data.core;

import java.util.Arrays;

public class HaxeObject
{
	public HaxeObjectDefinition type;
	public Object[] values;
	
	public HaxeObject(HaxeObjectDefinition type, Object[] values)
	{
		this.type = type;
		this.values = values;
	}
	
	public HaxeObject(HaxeObject o)
	{
		this.type = o.type;
		this.values = o.values.clone();
	}

	@Override
	public String toString()
	{
		return type.haxeClass + " " + Arrays.toString(values);
	}
}
