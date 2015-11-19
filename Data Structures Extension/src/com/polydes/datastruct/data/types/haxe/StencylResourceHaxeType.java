package com.polydes.datastruct.data.types.haxe;

import javax.swing.ImageIcon;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.builtin.StencylResourceType;
import com.polydes.common.sw.Resources;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Resource;

public class StencylResourceHaxeType<T extends Resource> extends HaxeDataType
{
	public StencylResourceType<T> srt;
	
	public StencylResourceHaxeType(StencylResourceType<T> srt, String haxeType, String stencylType)
	{
		super(srt, haxeType, stencylType);
		this.srt = srt;
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		if(extras.containsKey(StencylResourceType.RENDER_PREVIEW))
			props.put(StencylResourceType.RENDER_PREVIEW, Boolean.TRUE);
		return props;
	}
	
	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap extras = new ExtrasMap();
		if(props.get(StencylResourceType.RENDER_PREVIEW) == Boolean.TRUE)
			extras.put(StencylResourceType.RENDER_PREVIEW, "true");
		return extras;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		panel.getEditorSheet().build()
			.field(StencylResourceType.RENDER_PREVIEW)._boolean().add()
			.finish();
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof AbstractResource)
			return new ImageIcon(Resources.getImage((AbstractResource) value));
		return null;
	}
}