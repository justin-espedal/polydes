package com.polydes.common.data.types.builtin.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.core.engine.input.IControl;
import stencyl.core.lib.Game;

public class IControlType extends DataType<IControl>
{
	public IControlType()
	{
		super(IControl.class);
	}

	@Override
	public DataEditor<IControl> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new IControlEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
	}

	@Override
	public IControl decode(String s)
	{
		for(IControl c : Game.getGame().getNewController().values())
			if(c.getName().equals(s))
				return c;
		
		return null;
	}

	@Override
	public String encode(IControl c)
	{
		if(c == null)
			return "";
		
		return c.getName();
	}
	
	@Override
	public IControl copy(IControl t)
	{
		return t;
	}
	
	public static class IControlEditor extends DataEditor<IControl>
	{
		UpdatingCombo<IControl> editor;
		
		public IControlEditor()
		{
			editor = new UpdatingCombo<IControl>(Game.getGame().getNewController().values(), null);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(IControl t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public IControl getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
			editor = null;
		}
	}
}
