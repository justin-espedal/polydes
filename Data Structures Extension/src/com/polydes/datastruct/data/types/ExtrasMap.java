package com.polydes.datastruct.data.types;

import java.util.HashMap;

import com.polydes.common.data.types.DataType;
import com.polydes.common.ext.RORealizer;
import com.polydes.datastruct.DataStructuresExtension;

public class ExtrasMap extends HashMap<String, Object>
{
	public <T> T get(String key, DataType<T> type, T defaultValue)
	{
		String s = (String) get(key);
		if(s == null)
			return defaultValue;
		else
			return type.decode(s);
	}
	
	public <T> T get(String key, DataType<T> type)
	{
		String s = (String) get(key);
		if(s == null)
			return null;
		else
			return type.decode(s);
	}
	
	public String get(String key, String defaultValue)
	{
		String s = (String) get(key);
		return (s == null) ? defaultValue : s;
	}
	
	/*-------------------------------------*\
	 * Special Cases: Haxe Types
	\*-------------------------------------*/ 
	
	@SuppressWarnings("unchecked")
	public <T> T getTyped(String key, DataType<T> type, T defaultValue)
	{
		String s = (String) get(key);
		if(s == null)
			return defaultValue;
		else
			return (T) HaxeTypeConverter.decode(type, s);
	}
	
	public Object putTyped(String key, DataType<?> type, Object value)
	{
		return put(key, HaxeTypeConverter.encode(type, value));
	}
	
	/*-------------------------------------*\
	 * Special Cases: Map, Enum, DataType
	\*-------------------------------------*/ 
	
	public ExtrasMap getMap(String key)
	{
		return (ExtrasMap) get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T get(String key, Enum<T> enm)
	{
		if(key == null || key.isEmpty())
			return (T) enm;
		
		try
		{
			return (T) Enum.valueOf(enm.getClass(), (String) get(key));
		}
		catch(IllegalArgumentException ex)
		{
			return (T) enm;
		}
		catch(NullPointerException ex)
		{
			return (T) enm;
		}
	}
	
	public void requestDataType(String key, HaxeDataType defaultType, RORealizer<HaxeDataType> tr)
	{
		String s = (String) get(key);
		if(s == null)
			tr.realizeRO(defaultType);
		else
			DataStructuresExtension.get().getHaxeTypes().requestValue(s, tr);
	}
}