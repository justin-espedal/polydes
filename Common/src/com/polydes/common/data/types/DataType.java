package com.polydes.common.data.types;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.polydes.common.ext.RegistryObject;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public abstract class DataType<T> implements Comparable<DataType<?>>, RegistryObject
{
	public static String DEFAULT_VALUE = "default";
	public static String EDITOR = "editor";
	
	public final Class<T> javaType;
	private String id; //registry key
	
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
	
	/**
	 * Create editor, set value, add listener.<br />
	 * Dispose when you're done.
	 */
	public abstract DataEditor<T> createEditor(EditorProperties properties, PropertiesSheetStyle style);
	public abstract DataEditorBuilder createEditorBuilder();
	
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
		
		protected final JLabel label;
		
		public InvalidEditor(String msg, PropertiesSheetStyle style)
		{
			this.msg = msg;
			this.style = style;
			label = style.createSoftLabel(msg);
		}
		
		@Override public void set(T t) {}
		@Override public T getValue() { return null; }
		
		@Override
		public JComponent[] getComponents()
		{
			label.setText(msg);
			return new JComponent[] {label};
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
	
	public String getId()
	{
		return id;
	}
	
	public void changeId(String newID)
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