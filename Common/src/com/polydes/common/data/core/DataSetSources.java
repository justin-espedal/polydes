package com.polydes.common.data.core;

import java.util.ArrayList;

import com.polydes.common.data.types.Types;
import com.polydes.common.ext.ObjectRegistry;

public class DataSetSources extends ObjectRegistry<DataSetSource>
{
	private static DataSetSources instance = new DataSetSources();
	
	public static DataSetSources get()
	{
		return instance;
	}
	
	@Override
	public DataSetSource generatePlaceholder(String key)
	{
		ArrayList<String> empty = new ArrayList<>();
		return new DataSetSource(key, Types._String, ()->empty);
	}
}