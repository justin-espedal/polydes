package com.polydes.common.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.core.lib.Folder;
import stencyl.core.lib.Game;
import stencyl.core.lib.ResourceType;
import stencyl.core.lib.ResourceTypes;
import stencyl.sw.util.Util;

public class ResourceFolderType extends DataType<Folder>
{
	public ResourceFolderType()
	{
		super(Folder.class);
	}
	
	public static final String RESOURCE_TYPE = "resourceType";
	
	@Override
	public DataEditor<Folder> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new FolderChooser(props.get(RESOURCE_TYPE));
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new ResourceFolderEditorBuilder();
	}
	
	@Override
	public Folder decode(String s)
	{
		int id = Util.parseInt(s, -1);
		if(id == -1)
			return null;
		
		for(Folder f : Game.getGame().getFolders().getAllFolders())
			if(f.getID() == id)
				return f;
		
		return null;
	}

	@Override
	public String encode(Folder t)
	{
		return "" + t.getID();
	}

	@Override
	public Folder copy(Folder t)
	{
		return t;
	}
	
	public class ResourceFolderEditorBuilder extends DataEditorBuilder
	{
		public ResourceFolderEditorBuilder()
		{
			super(ResourceFolderType.this, new EditorProperties());
		}

		public ResourceFolderEditorBuilder type(ResourceType type)
		{
			props.put(RESOURCE_TYPE, type);
			return this;
		}
	}
	
	public class FolderChooser extends DataEditor<Folder>
	{
		final UpdatingCombo<Folder> editor;
		final JComponent[] comps;
		
		ResourceType type;
		
		public FolderChooser(ResourceType type)
		{
			editor = new UpdatingCombo<Folder>(Game.getGame().getFolders().getAllFolders(), null);
			setType(type);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
			
			comps = new JComponent[] {editor};
		}
		
		public void setType(ResourceType type)
		{
			this.type = type;
			editor.setFilter(type == null ? null : (f) -> this.type == ResourceTypes.getTypeByName(f.getType()));
		}
		
		@Override
		public void set(Folder f)
		{
			editor.setSelectedItem(f);
		}
		
		@Override
		public Folder getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
		}
	}
}
