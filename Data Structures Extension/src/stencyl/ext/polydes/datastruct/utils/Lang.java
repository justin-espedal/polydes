package stencyl.ext.polydes.datastruct.utils;

import java.util.HashMap;
import java.util.HashSet;

import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.types.DataType;

public class Lang
{
	public static final <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue : item;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <K, V> HashMap<K, V> hashmap(Object... a)
	{
		HashMap map = new HashMap();
		for(int i = 0; i < a.length; i += 2)
			map.put(a[i], a[i + 1]);
		return (HashMap<K, V>) map;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> HashSet<T> hashset(Object... a)
	{
		HashSet set = new HashSet();
		for(Object o : a)
			set.add(o);
		return (HashSet<T>) set;
	}
	
	public static final DataList datalist(DataType<?> type, Object... a)
	{
		DataList list = new DataList(type);
		for(Object o : a)
			list.add(o);
		return list;
	}
}
