package com.polydes.common.data.types.builtin;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class UnknownDataType extends DataType<String>
{
	public UnknownDataType(String id)
	{
		super(String.class, id);
	}

	public static class UnknownExtras extends ExtraProperties
	{
		ExtrasMap extras;
		
		public UnknownExtras(ExtrasMap extras)
		{
			this.extras = extras;
		}
		
		public ExtrasMap getMap()
		{
			return extras;
		}
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return ((UnknownExtras) extras).extras;
	}

	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return new UnknownExtras(extras);
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
