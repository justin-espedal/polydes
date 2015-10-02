package com.polydes.dialog.types;

import java.util.ArrayList;

import com.polydes.datastruct.data.types.DataType;

public class DialogDataTypes
{
	public static ArrayList<DataType<?>> types = new ArrayList<DataType<?>>();
	static
	{
		types.add(new AnimationType());
		types.add(new RatioIntType());
		types.add(new RatioPointType());
	}
}
