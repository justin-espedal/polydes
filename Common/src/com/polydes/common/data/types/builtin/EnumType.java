package com.polydes.common.data.types.builtin;

import com.polydes.common.comp.EnumEditor;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

@SuppressWarnings("rawtypes")
public class EnumType extends DataType<Enum>
{
	public EnumType()
	{
		super(Enum.class);
	}
	
	public static final String ENUM_TYPE = "enumType";
	
	@Override
	public DataEditor<Enum> createEditor(EditorProperties properties, PropertiesSheetStyle style)
	{
		return new EnumEditor<>(properties.get(ENUM_TYPE));
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new EnumEditorBuilder();
	}

	@Override
	public Enum copy(Enum t)
	{
		return t;
	}
	
	@Override
	public Enum decode(String s)
	{
		return null;
	}
	
	@Override
	public String encode(Enum t)
	{
		return null;
	}
	
	public class EnumEditorBuilder extends DataEditorBuilder
	{
		public EnumEditorBuilder()
		{
			super(EnumType.this, new EditorProperties());
		}
		
		public EnumEditorBuilder type(Class<? extends Enum<?>> cls)
		{
			props.put(ENUM_TYPE, cls);
			return this;
		}
	}
}
