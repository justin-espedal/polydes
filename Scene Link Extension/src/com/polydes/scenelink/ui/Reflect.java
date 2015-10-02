package com.polydes.scenelink.ui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polydes.scenelink.data.ModelProperty;


public class Reflect
{
	public static class Property
	{
		public String name;
		public Class<?> type;
		public ModelProperty annotation;

		public Property(String name, Class<?> type, ModelProperty annotation)
		{
			this.name = name;
			this.type = type;
			this.annotation = annotation;
		}
	}
	
	public static HashMap<String, Field> getDeclaredFieldMap(Object o)
	{
		HashMap<String, Field> toReturn = new HashMap<String, Field>();
		Class<?> cls = (o instanceof Class) ? ((Class<?>) o) : o.getClass();

		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields)
			toReturn.put(f.getName(), f);

		return toReturn;
	}

	public static Object newInstance(Class<?> cls)
	{
		Constructor<?> ctor = null;
		try
		{
			ctor = cls.getDeclaredConstructor();
		}
		catch (SecurityException e1)
		{
			e1.printStackTrace();
		}
		catch (NoSuchMethodException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			return ctor.newInstance();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
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

		return null;
	}

	public static void setField(Field f, Object o, Object value)
	{
		try
		{
			if(!f.isAccessible())
			{
				f.setAccessible(true);
				f.set(o, value);
				f.setAccessible(false);
			}
			else
				f.set(o, value);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	public static Object getFieldValue(Field f, Object o)
	{
		try
		{
			if(!f.isAccessible())
			{
				f.setAccessible(true);
				Object toReturn = f.get(o);
				f.setAccessible(false);
				return toReturn;
			}
			else
				return f.get(o);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public static Class<?>[] getGenericTypes(Field f)
	{
		ParameterizedType type = (ParameterizedType) f.getGenericType();
		Type[] types = type.getActualTypeArguments();
		Class<?>[] toReturn = new Class<?>[types.length];
		for (int i = 0; i < types.length; ++i)
		{
			toReturn[i] = (Class<?>) types[i];
		}
		return toReturn;
	}

	public static Field[] getFields(Object o, String[] fieldNames)
	{
		try
		{
			Class<?> c = o.getClass();
			Field[] fields = new Field[fieldNames.length];
			for (int i = 0; i < fields.length; ++i)
				fields[i] = c.getDeclaredField(fieldNames[i]);

			return fields;
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Object[] getValues(Object o, String[] fieldNames)
	{
		HashMap<String, Method> nameMethodMap = new HashMap<String, Method>();

		BeanInfo info = null;
		try
		{
			info = Introspector.getBeanInfo(o.getClass(), Object.class);
		}
		catch (IntrospectionException e)
		{
			e.printStackTrace();
		}
		PropertyDescriptor[] props = info.getPropertyDescriptors();
		for (PropertyDescriptor pd : props)
		{
			nameMethodMap.put(pd.getName(), pd.getReadMethod());
		}

		Object[] vals = new Object[fieldNames.length];
		for (int i = 0; i < fieldNames.length; ++i)
			vals[i] = invoke(nameMethodMap.get(fieldNames[i]), o);

		return vals;
	}

	public static Method getMethod(Object o, String name, Class<?>... args)
	{
		if (o instanceof Class)
		{
			try
			{
				return ((Class<?>) o).getMethod(name, args);
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}

		BeanInfo info = null;
		try
		{
			info = Introspector.getBeanInfo(o.getClass(), Object.class);
		}
		catch (IntrospectionException e)
		{
			e.printStackTrace();
		}
		MethodDescriptor[] methods = info.getMethodDescriptors();
		for (MethodDescriptor md : methods)
		{
			if (md.getMethod().getName().equals(name))
				return md.getMethod();
		}
		return null;
	}
	
	public static void loadProperties(Object o, ArrayList<Property> propList)
	{
		for(Field f : FieldUtils.getAllFields(o.getClass()))
		{
			String name = f.getName();
			Class<?> type = f.getType();
			ModelProperty mp = f.getAnnotation(ModelProperty.class);
			
			propList.add(new Property(name, type, mp));
		}
	}

	public static Object invoke(Method toInvoke, Object invokeOn,
			Object... args)
	{
		try
		{
			return toInvoke.invoke(invokeOn, args);
		}
		catch (IllegalArgumentException e)
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
		return null;
	}
}
