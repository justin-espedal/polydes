package com.polydes.datastruct.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class DelayedInitialize
{
	private static HashMap<String, ArrayList<DelayedSet>> initList = new HashMap<String, ArrayList<DelayedSet>>();
	
	public static void addMethod(Object o, String name, Object[] args, String prop)
	{
		if(!initList.containsKey(prop))
			initList.put(prop, new ArrayList<DelayedSet>());
		
		initList.get(prop).add(new DelayedMethodSet(o, name, args));
	}
	
	public static void addObject(Object o, String name, String prop)
	{
		if(!initList.containsKey(prop))
			initList.put(prop, new ArrayList<DelayedSet>());
		
		initList.get(prop).add(new DelayedSet(o, name));
	}
	
	public static void initProp(String prop, Object value)
	{
		ArrayList<DelayedSet> toInit = initList.remove(prop);
		if(toInit == null)
			return;
		
		for(DelayedSet set : toInit)
			set.write(value);
	}
	
	public static final int CALL_FIELDS = 0x01;
	public static final int CALL_METHODS = 0x02;
	
	public static void initPropPartial(String prop, Object value, int callFlag)
	{
		ArrayList<DelayedSet> toInit = initList.get(prop);
		if(toInit == null)
			return;
		
		for(DelayedSet set : toInit)
		{
			if((callFlag & CALL_FIELDS) > 0 && !(set instanceof DelayedMethodSet))
				set.write(value);
			if((callFlag & CALL_METHODS) > 0 && (set instanceof DelayedMethodSet))
				set.write(value);
		}
	}
	
	public static void clearProps()
	{
		initList.clear();
	}
	
	static class DelayedSet
	{
		Object o;
		String field;
		
		public DelayedSet(Object o, String field)
		{
			this.o = o;
			this.field = field;
		}
		
		public void write(Object value)
		{
			try
			{
				FieldUtils.writeDeclaredField(o, field, value, true);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	static class DelayedMethodSet extends DelayedSet
	{
		Object[] args;
		
		public DelayedMethodSet(Object o, String field, Object[] args)
		{
			super(o, field);
			this.args = args;
		}
		
		@Override
		public void write(Object value)
		{
			try
			{
				MethodUtils.invokeMethod(o, field, args);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
	}
}
