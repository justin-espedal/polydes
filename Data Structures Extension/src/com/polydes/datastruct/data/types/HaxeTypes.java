package com.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;
import com.polydes.common.ext.ObjectRegistry;
import com.polydes.datastruct.Blocks;
import com.polydes.datastruct.data.types.haxe.ArrayHaxeType;
import com.polydes.datastruct.data.types.haxe.BoolHaxeType;
import com.polydes.datastruct.data.types.haxe.ColorHaxeType;
import com.polydes.datastruct.data.types.haxe.DynamicHaxeType;
import com.polydes.datastruct.data.types.haxe.FloatHaxeType;
import com.polydes.datastruct.data.types.haxe.IControlHaxeType;
import com.polydes.datastruct.data.types.haxe.IntHaxeType;
import com.polydes.datastruct.data.types.haxe.SelectionHaxeType;
import com.polydes.datastruct.data.types.haxe.SetHaxeType;
import com.polydes.datastruct.data.types.haxe.StencylResourceHaxeType;
import com.polydes.datastruct.data.types.haxe.StringHaxeType;

public class HaxeTypes extends ObjectRegistry<HaxeDataType>
{
	public static ArrayHaxeType _Array = new ArrayHaxeType();
	public static BoolHaxeType _Bool = new BoolHaxeType();
	public static DynamicHaxeType _Dynamic = new DynamicHaxeType();
	public static FloatHaxeType _Float = new FloatHaxeType();
	public static IntHaxeType _Int = new IntHaxeType();
	public static StringHaxeType _String = new StringHaxeType();
	
	public static ColorHaxeType _Color = new ColorHaxeType();
	public static IControlHaxeType _Control = new IControlHaxeType();
//	public static ExtrasImageHaxeType _ExtrasImage = new ExtrasImageHaxeType();
	public static SelectionHaxeType _Selection = new SelectionHaxeType();
	public static SetHaxeType _Set = new SetHaxeType();
	
	public HaxeTypes()
	{
		addBasicTypes();
	}
	
	public void addBasicTypes()
	{
		//Basic
		registerItem(_Array);
		registerItem(_Bool);
		registerItem(_Dynamic);
		registerItem(_Float);
		registerItem(_Int);
		registerItem(_String);
		
		//Extra
		registerItem(_Color);
//		registerItem(_ExtrasImage);
		registerItem(_Selection);
		registerItem(_Set);
		
		//Stencyl types
		registerItem(_Control);
		
		registerItem(new StencylResourceHaxeType<>(Types._Actor, "com.stencyl.models.actor.ActorType", "ACTORTYPE"));
		registerItem(new StencylResourceHaxeType<>(Types._Background, "com.stencyl.models.Background", "OBJECT"));
		registerItem(new StencylResourceHaxeType<>(Types._Font, "com.stencyl.models.Font", "FONT"));
//		registerItem(new StencylResourceHaxeType<>(Types._Scene, "com.stencyl.models.Scene", "SCENE"));
		registerItem(new StencylResourceHaxeType<>(Types._Sound, "com.stencyl.models.Sound", "SOUND"));
		registerItem(new StencylResourceHaxeType<>(Types._Tileset, "com.stencyl.models.scene.Tileset", "OBJECT"));
	}
	
	@Override
	public void registerItem(HaxeDataType type)
	{
		super.registerItem(type);
		Blocks.addDesignModeBlocks(type);
		Types.get().requestValue(type.dataType.getId(), (dt) -> bridgeTypes(type, dt));
	}
	
	@Override
	public void unregisterItem(HaxeDataType type)
	{
		super.unregisterItem(type);
		Blocks.removeDesignModeBlocks(type);
	}

	@Override
	public HaxeDataType generatePlaceholder(String key)
	{
		return null;
	}

	/*================================================*\
	 | DataType Glue
	\*================================================*/
	
	private HashMap<String, String> dtFromHaxe = new HashMap<>();
	private HashMap<String, String> haxeFromDt = new HashMap<>();
	private HashMap<String, ArrayList<Consumer<DataType<?>>>> bridgeRequests = new HashMap<>();
	
	private void bridgeTypes(HaxeDataType type, DataType<?> dtype)
	{
		dtFromHaxe.put(type.getKey(), dtype.getKey());
		haxeFromDt.put(dtype.getKey(), type.getKey());
		
		if(bridgeRequests.containsKey(type.getKey()))
			for(Consumer<DataType<?>> request : bridgeRequests.remove(type.getKey()))
				request.accept(dtype);
	}
	
	/**
	 * Waits until both the HaxeDataType and the DataType<?> associated with a
	 * HaxeDataType key are added to their respective registries, then runs
	 * the callback.
	 */
	public void requestDataTypeBridge(String haxeKey, Consumer<DataType<?>> callback)
	{
		if(dtFromHaxe.containsKey(haxeKey))
			callback.accept(getDtFromHaxe(haxeKey));
		else
		{
			if(!bridgeRequests.containsKey(haxeKey))
				bridgeRequests.put(haxeKey, new ArrayList<>());
			bridgeRequests.get(haxeKey).add(callback);
		}
	}

	public DataType<?> getDtFromHaxe(String haxeType)
	{
		return Types.get().getItem(dtFromHaxe.get(haxeType));
	}
	
	public HaxeDataType getHaxeFromDT(String dataType)
	{
		return getItem(haxeFromDt.get(dataType));
	}
}
