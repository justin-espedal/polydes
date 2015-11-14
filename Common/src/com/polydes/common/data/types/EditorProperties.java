package com.polydes.common.data.types;

import java.util.HashMap;

public class EditorProperties extends HashMap<String, Object>
{
	@SuppressWarnings("unchecked")
	public <T> T get(String key)
	{
		return (T) super.get(key);
	}
}
