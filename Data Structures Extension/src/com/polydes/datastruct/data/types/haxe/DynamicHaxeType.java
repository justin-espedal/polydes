package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.Types;
import com.polydes.datastruct.data.types.HaxeDataType;

public class DynamicHaxeType extends HaxeDataType
{
	public DynamicHaxeType()
	{
		super(Types._Dynamic, "Dynamic", "OBJECT");
	}
}