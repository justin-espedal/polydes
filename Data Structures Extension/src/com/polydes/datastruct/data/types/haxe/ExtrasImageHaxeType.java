package com.polydes.datastruct.data.types.haxe;

import javax.swing.ImageIcon;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.sys.FileRenderer;
import com.polydes.datastruct.data.core.ExtrasResource;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.ExtrasResourceType;
import com.polydes.datastruct.data.types.HaxeDataType;

public class ExtrasImageHaxeType extends HaxeDataType
{
	public ExtrasImageHaxeType()
	{
		super(DSTypes._ExtrasResource, "com.polydes.datastruct.ExtrasImage", "IMAGE");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(ExtrasResourceType.RESOURCE_TYPE, ExtrasResourceType.ResourceType.IMAGE);
		return props;
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof ExtrasResource)
		{
			ExtrasResource r = (ExtrasResource) value;
			if(r.file != null && r.file.exists())
				return FileRenderer.generateThumb(r.file);
		}
		return null;
	}
}