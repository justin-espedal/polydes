package com.polydes.common.data.core;

import java.util.ArrayList;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;

public class DataList extends ArrayList<Object>
{
	public DataType<?> genType;
	
	public DataList(DataType<?> genType)
	{
		if(genType == null)
			throw new IllegalArgumentException("DataList.genType cannot be null.");
		this.genType = genType;
	}
	
	public static DataList fromStrings(String[] s)
	{
		DataList a = new DataList(Types._String);
		for(String s2 : s)
			a.add(s2);
		
		return a;
	}
	
	public static String[] toStrings(DataList dataList)
	{
		String[] s = new String[dataList.size()];
		for(int i = 0; i < dataList.size(); ++i)
			s[i] = dataList.genType.checkEncode(dataList.get(i));
		
		return s;
	}

	public static DataList fromStrings(String[] s, DataType<?> genType)
	{
		DataList a = new DataList(genType);
		for(String s2 : s)
			a.add(genType.decode(s2));
		
		return a;
	}
}