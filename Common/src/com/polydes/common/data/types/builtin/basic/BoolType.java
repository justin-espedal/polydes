package com.polydes.common.data.types.builtin.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class BoolType extends DataType<Boolean>
{
	public BoolType()
	{
		super(Boolean.class);
	}
	
	@Override
	public DataEditor<Boolean> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new BooleanEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new BoolEditorBuilder();
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
	
	public class BoolEditorBuilder extends DataEditorBuilder
	{
		public BoolEditorBuilder()
		{
			super(BoolType.this, new EditorProperties());
		}
	}
	
	public static class BooleanEditor extends DataEditor<Boolean>
	{
		final JCheckBox control;
		
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
		}
	}
}
