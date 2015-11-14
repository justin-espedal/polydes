package com.polydes.common.ui.propsheet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;

import stencyl.sw.util.dg.DialogPanel;

public class PropertiesSheetSupport
{
	private final PropertiesSheetWrapper wrapper;
	private final HashMap<String, FieldInfo> fields;
	private final PropertyChangeSupport pcs;
	private final PropertiesSheetBuilder builder;
	
	private final Object model;
	
	public PropertiesSheetSupport(DialogPanel panel, Object model)
	{
		this(new DialogPanelWrapper(panel), PropertiesSheetStyle.DARK, model);
	}
	
	private PropertiesSheetSupport(PropertiesSheetWrapper wrapper, PropertiesSheetStyle style, Object model)
	{
		this.wrapper = wrapper;
		fields = new HashMap<>();
		pcs = new PropertyChangeSupport(this);
		builder = new PropertiesSheetBuilder(this, wrapper, style);
		
		this.model = model;
	}
	
	public void run()
	{
		build()
			.field("myInt")._int().min(0).max(10).spinnerEditor().add()
			.field("myBool")._boolean().add()
			.field("myString")._string().expandingEditor().regex("").add()
			.finish();
	}
	
	public PropertiesSheetBuilder build()
	{
		return builder;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void fieldAdded(final FieldInfo field, DataEditor editor)
	{
		fields.put(field.varname, field);
		field.oldValue = readField(model, field.varname);
		field.editor = editor;
		editor.addListener(() -> writeField(model, field.varname, editor.getValue()));
		editor.setValue(readField(model, field.varname));
	}
	
	public void revertChanges()
	{
		for(FieldInfo field : fields.values())
			writeField(model, field.varname, field.oldValue);
	}
	
	public void dispose()
	{
		for(FieldInfo field : fields.values())
			field.editor.dispose();
		fields.clear();
		wrapper.dispose();
	}
	
	public static class FieldInfo
	{
		private String varname;
		private DataType<?> type;
		private String label;
		private String hint;
		private boolean optional;
		
		private Object oldValue;
		private DataEditor<?> editor;
		
		public FieldInfo(String varname, DataType<?> type, String label, String hint, boolean optional)
		{
			this.varname = varname;
			this.type = type;
			this.label = label;
			this.hint = hint;
			this.optional = optional;
		}
		
		public DataType<?> getType()
		{
			return type;
		}
		
		public String getHint()
		{
			return hint;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public boolean isOptional()
		{
			return optional;
		}
	}
	
	/*-------------------------------------*\
	 * Property Change Support
	\*-------------------------------------*/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	
	/*-------------------------------------*\
	 * Helpers
	\*-------------------------------------*/
	
	public Object readField(Object target, String fieldName)
	{
		try
		{
			return FieldUtils.readDeclaredField(target, fieldName, true);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void writeField(Object target, String fieldName, Object value)
	{
		try
		{
			Object oldValue = readField(target, fieldName);
			FieldUtils.writeDeclaredField(target, fieldName, value, true);
			pcs.firePropertyChange(fieldName, oldValue, value);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
