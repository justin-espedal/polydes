package com.polydes.common.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.core.lib.ResourceType;
import stencyl.core.lib.ResourceTypes;

public class StencylResourceType<T extends AbstractResource> extends DataType<T>
{
	ResourceType stencylResourceType;
	
	@SuppressWarnings("unchecked")
	public StencylResourceType(ResourceType stencylResourceType)
	{
		super((Class<T>) stencylResourceType.getResourceClass());
		this.stencylResourceType = stencylResourceType;
	}
	
	@Override
	public DataEditor<T> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new DropdownResourceEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
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
	
	@SuppressWarnings("unchecked")
	public Collection<T> getList()
	{
		Collection<?> list =
			stencylResourceType == ResourceTypes.scene ?
			Game.getGame().getScenes() :
				stencylResourceType == ResourceTypes.snippet ?
				Game.getGame().getSnippets() :
					Game.getGame().getResources().getResourcesByType((Class<Resource>) javaType);
		
		return (Collection<T>) list;
	}
	
	public class DropdownResourceEditor extends DataEditor<T>
	{
		UpdatingCombo<T> editor;
		
		public DropdownResourceEditor()
		{
			editor = new UpdatingCombo<T>(getList(), null);
			
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