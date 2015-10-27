package com.polydes.datastruct.data.types;

import java.util.HashMap;

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
}