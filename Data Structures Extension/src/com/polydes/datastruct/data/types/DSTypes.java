package com.polydes.datastruct.data.types;

import com.polydes.common.data.types.Types;

public class DSTypes
{
	public static DynamicType _Dynamic = new DynamicType();
	public static ExtrasResourceType _ExtrasResource = new ExtrasResourceType();
	
	public static void register()
	{
		Types.get().registerItem(_Dynamic);
		Types.get().registerItem(_ExtrasResource);
	}
	
	public static void unregister()
	{
		Types.get().unregisterItem(_Dynamic);
		Types.get().unregisterItem(_ExtrasResource);
	}
}
