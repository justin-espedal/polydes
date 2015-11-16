package com.polydes.common.data.types.builtin.extra;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.colors.ColorDisplay;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.ColorUtil;

public class ColorType extends DataType<Color>
{
	public ColorType()
	{
		super(Color.class);
	}

	@Override
	public DataEditor<Color> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new ColorEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
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
	
	public static class ColorEditor extends DataEditor<Color>
	{
		final ColorDisplay control;
		
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
			return new JComponent[] {control};
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
		}
	}
}
