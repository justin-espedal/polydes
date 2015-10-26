package com.polydes.datastruct.data.core;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtrasMap;

public class HaxeField
{
	public String name;
	public DataType<?> type;
	public ExtrasMap editorData;
	public String defaultValue;
	
	public HaxeField(String name, DataType<?> type, ExtrasMap editorData)
	{
		this.name = name;
		this.type = type;
		defaultValue = StringUtils.EMPTY;
		
		this.editorData = editorData == null ?
			new ExtrasMap() :
			editorData;
	}

	@Override
	public String toString()
	{
		return "HaxeField [name=" + name + ", type=" + type + ", editorData="
				+ editorData + ", defaultValue=" + defaultValue + "]";
	}
}
