package com.polydes.dialog.data.def.elements;

import javax.swing.JComponent;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.ui.comp.EnumEditor;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureArgument
{
	public static enum Type
	{
		Int,
		Float,
		Bool,
		String,
		Color,
		Array,
		Dynamic
	}
	
	public String name;
	public Type type;
	
	public StructureArgument(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	public Type getType()
	{
		return type;
	}

	public static class StructureArgumentEditor extends DataEditor<StructureArgument>
	{
		StructureArgument arg;
		
		DataEditor<String> nameEditor;
		DataEditor<Type> typeEditor;
		
		JComponent[] comps;
		
		public StructureArgumentEditor(PropertiesSheetStyle style)
		{
			nameEditor = new StringType.SingleLineStringEditor(null, style);
			nameEditor.addListener(() -> updated());
			
			typeEditor = new EnumEditor<Type>(Type.class);
			typeEditor.addListener(() -> updated());
			
			comps = ArrayUtils.addAll(nameEditor.getComponents(), typeEditor.getComponents());
		}
		
		@Override
		public void set(StructureArgument t)
		{
			nameEditor.setValue(t.name);
			typeEditor.setValue(t.type);
		}
		
		@Override
		public StructureArgument getValue()
		{
			return new StructureArgument(nameEditor.getValue(), typeEditor.getValue());
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			nameEditor.dispose();
			typeEditor.dispose();
			nameEditor = null;
			typeEditor = null;
		}
	}
}
