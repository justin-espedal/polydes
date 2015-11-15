package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.builtin.UnknownDataType;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;

public class UnknownHaxeType extends HaxeDataType
{
	public UnknownHaxeType(String name)
	{
		super(new UnknownDataType(name), name, "OBJECT");
	}
	
	public static final String EXTRAS_MAP = "extrasMap";
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		return new UnknownProperties(extras);
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		return ((UnknownProperties) props).getMap();
	}
	
	public static class UnknownProperties extends EditorProperties
	{
		public UnknownProperties(ExtrasMap extras)
		{
			put(EXTRAS_MAP, extras);
		}
		
		public ExtrasMap getMap()
		{
			return get(EXTRAS_MAP);
		}
	}
}
