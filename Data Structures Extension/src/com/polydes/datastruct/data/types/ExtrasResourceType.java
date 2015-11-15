package com.polydes.datastruct.data.types;

import java.io.File;

import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.FileType.FileEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.core.ExtrasResource;

import stencyl.core.lib.Game;
import stencyl.sw.util.Locations;

public class ExtrasResourceType extends DataType<ExtrasResource>
{
	public ExtrasResourceType()
	{
		super(ExtrasResource.class);
	}

	public static final String RESOURCE_TYPE = "resourceType";
	
	@Override
	public DataEditor<ExtrasResource> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new ExtrasResourceEditor(props, style);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new ExtrasResourceEditorBuilder();
	}

	@Override
	public ExtrasResource decode(String s)
	{
		ExtrasResource r = new ExtrasResource();
		r.file = new File(Locations.getGamePath(Game.getGame(), "extras"), s);
		return r;
	}

	@Override
	public String encode(ExtrasResource i)
	{
		if(i == null)
			return "";
		
		return StringUtils.difference(Locations.getGamePath(Game.getGame(), "extras"), i.file.getAbsolutePath());
	}
	
	@Override
	public ExtrasResource copy(ExtrasResource t)
	{
		return t;
	}
	
	public enum ResourceType
	{
		IMAGE("*.png");

		String filter;
		
		private ResourceType(String filter)
		{
			this.filter = filter;
		}
		
		public String getFilter()
		{
			return filter;
		}
	}
	
	public class ExtrasResourceEditorBuilder extends DataEditorBuilder
	{
		public ExtrasResourceEditorBuilder()
		{
			super(ExtrasResourceType.this, new EditorProperties());
		}
		
		public ExtrasResourceEditorBuilder type(ResourceType type)
		{
			props.put(RESOURCE_TYPE, type);
			return this;
		}
	}
	
	public static class ExtrasResourceEditor extends DataEditor<ExtrasResource>
	{
		FileEditor fileEditor;
		
		public ExtrasResourceEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			ResourceType type = props.get(RESOURCE_TYPE);
			
			fileEditor = (FileEditor) Types._File.new FileEditorBuilder()
					.rootDirectory(Locations.getGamePath(Game.getGame(), "extras")).filter(type.getFilter())
					.build(style);
			
			fileEditor.addListener(() -> updated());
		}
		
		@Override
		public ExtrasResource getValue()
		{
			File f = fileEditor.getValue();
			if(f == null)
				return null;
			if(!f.exists())
				return null;
			ExtrasResource r = new ExtrasResource();
			r.file = f;
			return r;
		}

		@Override
		public void set(ExtrasResource t)
		{
			if(t == null)
				fileEditor.setValue(null);
			else
				fileEditor.setValue(t.file);
		}

		@Override
		public JComponent[] getComponents()
		{
			return fileEditor.getComponents();
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			fileEditor.dispose();
			fileEditor = null;
		}
	}
}