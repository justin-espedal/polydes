package com.polydes.dialog.data.def.elements;

import javax.swing.JComponent;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.comp.EnumEditor;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

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

	public static class StructureArgumentType extends DataType<StructureArgument>
	{
		public StructureArgumentType()
		{
			super(StructureArgument.class);
		}

		@Override
		public DataEditor<StructureArgument> createEditor(EditorProperties properties, PropertiesSheetStyle style)
		{
			return new StructureArgumentEditor(style);
		}

		@Override
		public DataEditorBuilder createEditorBuilder()
		{
			return new DataEditorBuilder(this, new EditorProperties());
		}

		@Override
		public StructureArgument decode(String s)
		{
			if(s == null || !s.contains(":"))
				return new StructureArgument("", Type.String);
			
			String name = StringUtils.substringBefore(s, ":");
			Type type = Type.valueOf(StringUtils.substringAfter(s, ":"));
			return new StructureArgument(name, type);
		}

		@Override
		public String encode(StructureArgument t)
		{
			return t.name + ":" + t.type;
		}

		@Override
		public StructureArgument copy(StructureArgument t)
		{
			return new StructureArgument(t.name, t.type);
		}
	}
	
	public static class StructureArgumentEditor extends DataEditor<StructureArgument>
	{
		StructureArgument arg;
		
		final DataEditor<String> nameEditor;
		final DataEditor<Type> typeEditor;
		
		final JComponent[] comps;
		
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
		}
	}
}
