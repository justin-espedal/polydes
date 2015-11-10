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
	
	@SafeVarargs
	public static final <T> ArrayList<T> arraylist(T... a)
	{
		ArrayList<T> list = new ArrayList<T>(a.length);
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
	
	@SuppressWarnings({ "unchecked" })
	public static final <T> HashSet<T> hashset(T... a)
	{
		HashSet<T> set = new HashSet<T>();
		for(T o : a)
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
	
	public static final <T> void fori(Iterable<T> it, IteratorFunction<T> fun)
	{
		int i = 0;
		for(T e : it)
			fun.apply(i++, e);
	}
	
	@FunctionalInterface
	public static interface IteratorFunction<T>
	{
		void apply(int i, T t);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T[] asArray(Collection<T> t, Class<T> c)
	{
		T[] a = (T[]) Array.newInstance(c, t.size());
		
		int i = 0;
		for(T child : t)
			a[i++] = child;
		
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T[] newarray(Class<T> c, int size)
	{
		return (T[]) Array.newInstance(c, size);
	}
}
