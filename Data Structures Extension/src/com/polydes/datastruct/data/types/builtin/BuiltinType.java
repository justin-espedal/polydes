package com.polydes.datastruct.data.types.builtin;

import java.io.File;
import java.util.List;

import stencyl.sw.util.Locations;

import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.io.Text;

public abstract class BuiltinType<T> extends DataType<T>
{
	public BuiltinType(Class<T> javaType, String haxeType, String stencylType, String xml)
	{
		super(javaType, haxeType, stencylType, xml);
	}
		
	@Override
	public List<String> generateHaxeClass()
	{
		return null;
	}

	@Override
	public List<String> generateHaxeReader()
	{
		File f = new File(Locations.getGameExtensionLocation("com.polydes.datastruct"), "types/" + xml + ".hx");
		return Text.readLines(f);
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
}