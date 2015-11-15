package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.EditorProperties;
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
}