package com.polydes.common.data.types.builtin;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class UnknownDataType extends DataType<String>
{
	public UnknownDataType(String id)
	{
		super(String.class, id);
	}
	
	@Override
	public DataEditor<String> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new UnkownEditor("", style);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
	}
	
	class UnkownEditor extends InvalidEditor<String>
	{
		public UnkownEditor(String msg, PropertiesSheetStyle style)
		{
			super(msg, style);
		}
		
		@Override
		public void set(String t)
		{
			this.msg = t;
		}
		
		@Override
		public String getValue()
		{
			return msg;
		}
	}

	@Override
	public String decode(String s)
	{
		return s;
	}

	@Override
	public String toDisplayString(String data)
	{
		return data;
	}

	@Override
	public String encode(String t)
	{
		return t;
	}

	@Override
	public String copy(String t)
	{
		return t;
	}
}
