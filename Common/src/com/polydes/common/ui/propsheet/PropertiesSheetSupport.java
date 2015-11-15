package com.polydes.common.ui.propsheet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

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
	
	private boolean usesMap;
	private final Object model;
	
	public PropertiesSheetSupport(DialogPanel panel, Object model)
	{
		this(new DialogPanelWrapper(panel), PropertiesSheetStyle.DARK, model);
	}
	
	public PropertiesSheetSupport(PropertiesSheetWrapper wrapper, PropertiesSheetStyle style, Object model)
	{
		this.wrapper = wrapper;
		fields = new HashMap<>();
		pcs = new PropertyChangeSupport(this);
		builder = new PropertiesSheetBuilder(this, wrapper, style);
		
		this.model = model;
		usesMap = model instanceof Map;
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
		return builder.startBuilding();
	}

	public PropertiesSheetBuilder change()
	{
		return builder.startChanging();
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void fieldAdded(final FieldInfo field, DataEditor editor)
	{
		fields.put(field.varname, field);
		field.oldValue = readField(model, field.varname);
		field.editor = editor;
		editor.addListener(() -> writeField(model, field.getVarname(), editor.getValue()));
		editor.setValue(readField(model, field.varname));
	}
	
	public FieldInfo getField(String varname)
	{
		return fields.get(varname);
	}
	
	public PropertiesSheetWrapper getWrapper()
	{
		return wrapper;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void updateField(String varname, Object value)
	{
		((DataEditor) fields.get(varname).editor).setValue(value);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void changeField(String varname, FieldInfo field, DataEditor editor)
	{
		fields.remove(varname).editor.dispose();
		fields.put(varname, field);
		field.editor = editor;
		editor.addListener(() -> writeField(model, field.getVarname(), editor.getValue()));
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
		
		public String getVarname()
		{
			return varname;
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
		
		public DataEditor<?> getEditor()
		{
			return editor;
		}
	}
	
	/*-------------------------------------*\
	 * Property Change Support
	\*-------------------------------------*/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String property, PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	
	/*-------------------------------------*\
	 * Helpers
	\*-------------------------------------*/
	
	@SuppressWarnings("rawtypes")
	public Object readField(Object target, String fieldName)
	{
		if(usesMap)
			return ((Map) target).get(fieldName);
		try
		{
			return FieldUtils.readDeclaredField(target, fieldName, true);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void writeField(Object target, String fieldName, Object value)
	{
		try
		{
			Object oldValue = readField(target, fieldName);
			if(usesMap)
				((Map) target).put(fieldName, value);
			else
				FieldUtils.writeDeclaredField(target, fieldName, value, true);
			pcs.firePropertyChange(fieldName, oldValue, value);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
