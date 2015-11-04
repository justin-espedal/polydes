package com.polydes.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Lang
{
	public static final <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue : item;
	}
	
	@SafeVarargs
	public static final <T> T[] array(T... items)
	{
		return items;
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
	
	@SuppressWarnings("unchecked")
	public static final <T,U> U[] map(T[] t, Class<U> c, Function<? super T, ? extends U> mapper)
	{
		U[] mapped = (U[]) Array.newInstance(c, t.length);
		
		for(int i = 0; i < t.length; ++i)
			mapped[i] = mapper.apply(t[i]);
		
		return mapped;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T,U> U[] mapCA(Collection<T> t, Class<U> c, Function<? super T, ? extends U> mapper)
	{
		U[] mapped = (U[]) Array.newInstance(c, t.size());
		
		int i = 0;
		for(T child : t)
			mapped[i++] = mapper.apply(child);
		
		return mapped;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T,U> U[] mapi(T[] t, Class<U> c, BiFunction<Integer, ? super T, ? extends U> mapper)
	{
		U[] mapped = (U[]) Array.newInstance(c, t.length);
		
		for(int i = 0; i < t.length; ++i)
			mapped[i] = mapper.apply(i, t[i]);
		
		return mapped;
	}
}
