package com.polydes.common.data.types.builtin.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class BoolType extends DataType<Boolean>
{
	public BoolType()
	{
		super(Boolean.class);
	}
	
	@Override
	public DataEditor<Boolean> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new BooleanEditor();
	}

	@Override
	public Boolean decode(String s)
	{
		return s.equals("true");
	}

	@Override
	public String encode(Boolean b)
	{
		return b ? "true" : "false";
	}

	@Override
	public Boolean copy(Boolean t)
	{
		return new Boolean(t);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Bool, false);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Boolean defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static class BooleanEditor extends DataEditor<Boolean>
	{
		JCheckBox control;
		
		public BooleanEditor()
		{
			control = new JCheckBox();
			control.setBackground(null);
			
			control.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {control};
		}
		
		@Override
		public Boolean getValue()
		{
			return control.isSelected();
		}
		
		@Override
		public void set(Boolean b)
		{
			if(b == null)
				b = false;
			control.setSelected(b);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			control = null;
		}
	}
}
