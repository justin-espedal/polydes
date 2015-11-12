package com.polydes.datastruct.utils;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataType;

public class DLang
{
	public static final DataList datalist(DataType<?> type, Object... a)
	{
		DataList list = new DataList(type);
		for(Object o : a)
			list.add(o);
		return list;
	}
}
