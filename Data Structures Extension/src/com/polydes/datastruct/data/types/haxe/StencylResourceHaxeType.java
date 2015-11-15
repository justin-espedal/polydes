package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.builtin.StencylResourceType;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.lib.Resource;

public class StencylResourceHaxeType<T extends Resource> extends HaxeDataType
{
	public StencylResourceType<T> srt;
	
	public StencylResourceHaxeType(StencylResourceType<T> srt, String haxeType, String stencylType)
	{
		super(srt, haxeType, stencylType);
		this.srt = srt;
	}
}