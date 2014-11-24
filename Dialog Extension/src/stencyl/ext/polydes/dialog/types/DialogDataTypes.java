package stencyl.ext.polydes.dialog.types;

import java.util.ArrayList;

import stencyl.ext.polydes.datastruct.data.types.DataType;

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
