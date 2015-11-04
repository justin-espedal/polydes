package com.polydes.common.data.types.builtin;

import static com.polydes.common.util.Lang.array;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.core.lib.ResourceType;

public class StencylResourceType<T extends Resource> extends DataType<T>
{
	ResourceType stencylResourceType;
	
	@SuppressWarnings("unchecked")
	public StencylResourceType(ResourceType stencylResourceType)
	{
		super((Class<T>) stencylResourceType.getResourceClass());
		this.stencylResourceType = stencylResourceType;
	}
	
	@Override
	public DataEditor<T> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DropdownResourceEditor();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T decode(String s)
	{
		try
		{
			int id = Integer.parseInt(s);
			Resource r = Game.getGame().getResource(id);
			if(r != null && javaType.isAssignableFrom(r.getClass()))
				return (T) r;
			
			return null;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(T r)
	{
		if(r == null)
			return "";
		
		return "" + r.getID();
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public T copy(T t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras<T> e = new Extras<T>();
		e.defaultValue = extras.get(DEFAULT_VALUE, this, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		@SuppressWarnings("unchecked")
		Extras<T> e = (Extras<T>) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras<T extends Resource> extends ExtraProperties
	{
		public T defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public class DropdownResourceEditor extends DataEditor<T>
	{
		UpdatingCombo<T> editor;
		
		public DropdownResourceEditor()
		{
			editor = new UpdatingCombo<T>(Game.getGame().getResources().getResourcesByType(javaType), null);
			
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
		public void set(T t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public T getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(editor);
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