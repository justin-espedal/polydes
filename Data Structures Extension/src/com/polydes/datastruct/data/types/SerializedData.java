package com.polydes.datastruct.data.types;

import java.io.Serializable;

public class SerializedData implements Serializable
{
	public String type;
	public String data;
	
	public SerializedData(String type, String data)
	{
		this.type = type;
		this.data = data;
	}
}