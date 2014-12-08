package stencyl.ext.polydes.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Lang
{
	public static final <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue : item;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> ArrayList<T> arraylist(Object... a)
	{
		ArrayList list = new ArrayList(a.length);
		for(int i = 0; i < a.length; ++i)
			list.add(a[i]);
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <K, V> HashMap<K, V> hashmap(Object... a)
	{
		HashMap map = new HashMap();
		for(int i = 0; i < a.length; i += 2)
			map.put(a[i], a[i + 1]);
		return map;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> HashSet<T> hashset(Object... a)
	{
		HashSet set = new HashSet();
		for(Object o : a)
			set.add(o);
		return set;
	}
}
