package com.polydes.datastruct.utils;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;

public class ListElementArrays
{
	public static DataList fromStrings(String[] s)
	{
		DataList a = new DataList(Types.fromXML("String"));
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