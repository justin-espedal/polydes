package com.polydes.datastruct.data.types.builtin.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.BuiltinType;
import com.polydes.datastruct.ui.comp.UpdatingCombo;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

import stencyl.core.engine.input.IControl;
import stencyl.core.lib.Game;

public class IControlType extends BuiltinType<IControl>
{
	public IControlType()
	{
		super(IControl.class, "com.polydes.datastruct.Control", "CONTROL");
	}

	@Override
	public DataEditor<IControl> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new IControlEditor();
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
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<IControl> defaultField = new IControlEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addGenericRow(expansion, "Default", defaultField);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Control, null);
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
	
	class Extras extends ExtraProperties
	{
		public IControl defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
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
			return comps(editor);
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
