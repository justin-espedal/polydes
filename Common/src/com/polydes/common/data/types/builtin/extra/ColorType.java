package com.polydes.common.data.types.builtin.extra;

import static com.polydes.common.util.Lang.array;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.colors.ColorDisplay;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.ColorUtil;

public class ColorType extends DataType<Color>
{
	public ColorType()
	{
		super(Color.class);
	}

	@Override
	public DataEditor<Color> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new ColorEditor();
	}

	@Override
	public Color decode(String s)
	{
		return ColorUtil.decode(s);
	}

	@Override
	public String encode(Color c)
	{
		return ColorUtil.encode(c);
	}

	@Override
	public Color copy(Color t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Color, null);
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
		public Color defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static class ColorEditor extends DataEditor<Color>
	{
		ColorDisplay control;
		
		public ColorEditor()
		{
			control = new ColorDisplay(20, 20, null, null);
			
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
			return array(control);
		}
		
		@Override
		public Color getValue()
		{
			return control.getColor();
		}
		
		@Override
		public void set(Color c)
		{
			if(c == null)
				c = Color.BLACK;
			control.setColor(c);
		}
		
		public void setOwner(Window owner)
		{
			control.setOwner(owner);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			control.dispose();
			control = null;
		}
	}
}
