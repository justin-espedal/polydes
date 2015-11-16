package com.polydes.datastruct.data.types;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.core.DataSet;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.ArrayType;
import com.polydes.datastruct.DataStructuresExtension;

/**
 * Kind of a hack class to make it so we can still have Haxe types
 * in our input/output instead of corresponding Java types.
 */
public class HaxeTypeConverter
{
	private static HaxeTypes getHT()
	{
		return DataStructuresExtension.get().getHaxeTypes();
	}
	
	@SuppressWarnings("rawtypes")
	private static HashMap<String, Coder> coders;
	static
	{
		coders = new HashMap<>();
		coders.put(Types._Array.getId(), new ArrayCoder());
		coders.put(Types._Set.getId(), new SetCoder());
	}
	
	public static Object decode(DataType<?> type, String s)
	{
		if(coders.containsKey(type.getId()))
			return coders.get(type.getId()).decode(s);
		else
			return type.decode(s);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static String encode(DataType type, Object o)
	{
		if(coders.containsKey(type.getId()))
			return coders.get(type.getId()).encode(o);
		else
			return type.encode(o);
	}
	
	static interface Coder<T>
	{
		T decode(String s);
		String encode(T t);
	}
	
	static class ArrayCoder implements Coder<DataList>
	{
		@Override
		public DataList decode(String s)
		{
			if(s.isEmpty())
				return null;
			
			int i = s.lastIndexOf(":");
			String typename = s.substring(i + 1);
			DataType<?> genType = getHT().getItem(typename).dataType;
			DataList list = new DataList(genType);
			
			for(String s2 : ArrayType.getEmbeddedArrayStrings(s))
				list.add(HaxeTypeConverter.decode(genType, s2));
			
			return list;
		}
		
		@Override
		public String encode(DataList array)
		{
			if(array == null)
				return "";
			
			String s = "[";
			
			for(int i = 0; i < array.size(); ++i)
				s += HaxeTypeConverter.encode(array.genType, array.get(i)) + (i < array.size() - 1 ? "," : "");
			s += "]:" + getHT().getHaxeFromDT(array.genType.getId()).getHaxeType();
			
			return s;
		}
	}
	
	static class SetCoder implements Coder<DataSet>
	{
		@Override
		public DataSet decode(String s)
		{
			int typeMark = s.lastIndexOf(":");
			if(typeMark == -1)
				return new DataSet(DSTypes._Dynamic);
			
			DataType<?> dtype = getHT().getItem(s.substring(typeMark + 1)).dataType;
			if(dtype == null)
				return new DataSet(DSTypes._Dynamic);
			
			DataSet toReturn = new DataSet(dtype);
			
			for(String s2 : StringUtils.split(s.substring(1, typeMark - 1), ","))
				toReturn.add(HaxeTypeConverter.decode(dtype, s2));
			
			return toReturn;
		}

		@Override
		public String encode(DataSet t)
		{
			Object[] a = t.toArray(new Object[0]);
			String s = "[";
			DataType<?> type = t.genType;
			
			for(int i = 0; i < a.length; ++i)
			{
				s += HaxeTypeConverter.encode(type, a[i]);
				
				if(i < a.length - 1)
					s += ",";
			}
			
			s += "]:" + getHT().getHaxeFromDT(type.getId()).getHaxeType();
			
			return s;
		}
	}
}
