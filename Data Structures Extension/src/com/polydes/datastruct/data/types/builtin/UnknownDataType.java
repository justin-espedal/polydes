package com.polydes.datastruct.data.types.builtin;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class UnknownDataType extends DataType<String>
{
	public UnknownDataType(String xml)
	{
		super(String.class, xml, "");
	}

	class UnkownExtras extends ExtraProperties
	{
		ExtrasMap extras;
		
		public UnkownExtras(ExtrasMap extras)
		{
			this.extras = extras;
		}
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return ((UnkownExtras) extras).extras;
	}

	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return new UnkownExtras(extras);
	}

	@Override
	public DataEditor<String> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new UnkownEditor("", style);
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
