package com.polydes.common.data.types;

import static com.polydes.common.util.Lang.array;

import javax.swing.JComponent;

import com.polydes.common.ext.RegistryObject;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public abstract class DataType<T> implements Comparable<DataType<?>>, RegistryObject
{
	public static String DEFAULT_VALUE = "default";
	public static String EDITOR = "editor";
	
	public final Class<T> javaType;
	public String id;
	
	private final int hash;
	
	public DataType(Class<T> javaType)
	{
		this(javaType, javaType.getName());
	}
	
	public DataType(Class<T> javaType, String id)
	{
		this.javaType = javaType;
		this.id = id;
		hash = id.hashCode();
	}
	
	public abstract ExtrasMap saveExtras(ExtraProperties extras);
	public abstract ExtraProperties loadExtras(ExtrasMap extras);
	
	/**
	 * Create editor, set value, add listener.<br />
	 * Dispose when you're done.
	 */
	public abstract DataEditor<T> createEditor(ExtraProperties extras, PropertiesSheetStyle style);
	public DataEditor<T> createEditor(ExtrasMap extras, PropertiesSheetStyle style)
	{
		return createEditor(loadExtras(extras), style);
	}
	
	public abstract T decode(String s);
	
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
	
	@SuppressWarnings("unchecked")
	public String checkToDisplayString(Object o)
	{
		if(o == null)
			return "null";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return toDisplayString((T) o);
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public String checkEncode(Object o)
	{
		if(o == null)
			return "";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return encode((T) o);
		
		System.out.println("Failed to encode " + o);
		
		return "";
	}
	
	public abstract String encode(T t);

	@Override
	public int compareTo(DataType<?> dt)
	{
		return id.compareTo(dt.id);
	}
	
	@SuppressWarnings("unchecked")
	public T checkCopy(Object o)
	{
		if(o == null)
			return null;
		
		if(javaType.isAssignableFrom(o.getClass()))
			return copy((T) o);
		
		System.out.println("Failed to copy " + o);
		
		return null;
	}
	
	public abstract T copy(T t);
	
	@Override
	public String toString()
	{
		return id;
	}
	
	public static class InvalidEditor<T> extends DataEditor<T>
	{
		protected PropertiesSheetStyle style;
		protected String msg;
		
		public InvalidEditor(String msg, PropertiesSheetStyle style)
		{
			this.msg = msg;
			this.style = style;
		}
		
		@Override public void set(T t) {}
		@Override public T getValue() { return null; }
		
		@Override
		public JComponent[] getComponents()
		{
			return array(style.createSoftLabel(msg));
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DataType) && id.equals(((DataType<?>) obj).id);
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
	
	public void changeID(String newID)
	{
		Types.get().renameItem(this, newID);
	}
	
	@Override
	public String getKey()
	{
		return id;
	}
	
	@Override
	public void setKey(String newKey)
	{
		this.id = newKey;
	}
}