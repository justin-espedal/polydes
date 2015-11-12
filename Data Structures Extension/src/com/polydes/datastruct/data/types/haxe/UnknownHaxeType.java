package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.builtin.UnknownDataType;
import com.polydes.datastruct.data.types.HaxeDataType;

public class UnknownHaxeType extends HaxeDataType
{
	public UnknownHaxeType(String name)
	{
		super(new UnknownDataType(name), name, "OBJECT");
	}
}
