package stencyl.ext.polydes.datastruct.utils;

import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.types.DataType;

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
