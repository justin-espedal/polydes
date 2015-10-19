package com.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.polydes.datastruct.Blocks;
import com.polydes.datastruct.data.types.builtin.basic.ArrayType;
import com.polydes.datastruct.data.types.builtin.basic.BoolType;
import com.polydes.datastruct.data.types.builtin.basic.DynamicType;
import com.polydes.datastruct.data.types.builtin.basic.FloatType;
import com.polydes.datastruct.data.types.builtin.basic.IntType;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.data.types.builtin.extra.ColorType;
import com.polydes.datastruct.data.types.builtin.extra.ExtrasImageType;
import com.polydes.datastruct.data.types.builtin.extra.IControlType;
import com.polydes.datastruct.data.types.builtin.extra.SelectionType;
import com.polydes.datastruct.data.types.builtin.extra.SetType;
import com.polydes.datastruct.data.types.general.StencylResourceType;
import com.polydes.datastruct.data.types.hidden.DataTypeType;
import com.polydes.datastruct.utils.DelayedInitialize;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.engine.sound.ISoundClip;
import stencyl.sw.data.EditableBackground;
import stencyl.sw.data.EditableFont;
import stencyl.sw.data.EditableTileset;

public class Types
{
	public static HashMap<String, DataType<?>> typeFromXML = new LinkedHashMap<String, DataType<?>>();
	public static ArrayList<DataType<?>> changedTypes = new ArrayList<DataType<?>>();
	
	//===
	
	public static DataTypeType _DataType = new DataTypeType();
	
	public static ArrayType _Array = new ArrayType();
	public static BoolType _Bool = new BoolType();
	public static DynamicType _Dynamic = new DynamicType();
	public static FloatType _Float = new FloatType();
	public static IntType _Int = new IntType();
	public static StringType _String = new StringType();
	
	public static ColorType _Color = new ColorType();
	public static IControlType _Control = new IControlType();
	public static ExtrasImageType _ExtrasImage = new ExtrasImageType();
	public static SelectionType _Selection = new SelectionType();
	public static SetType _Set = new SetType();
	
	//===
	
	public static void addBasicTypes()
	{
		//Basic
		addType(_Array);
		addType(_Bool);
		addType(_Dynamic);
		addType(_Float);
		addType(_Int);
		addType(_String);
		
		//Extra
		addType(_Color);
		addType(_ExtrasImage);
		addType(_Selection);
		addType(_Set);
		
		//Stencyl types
		addType(_Control);
		addType(new StencylResourceType<IActorType>(IActorType.class, "com.stencyl.models.actor.ActorType", "ACTORTYPE"));
		addType(new StencylResourceType<EditableBackground>(EditableBackground.class, "com.stencyl.models.Background", "OBJECT"));
		addType(new StencylResourceType<EditableFont>(EditableFont.class, "com.stencyl.models.Font", "FONT"));
		//types.put(SceneModel.class, new StencylResourceType<SceneModel>(SceneModel.class, "com.stencyl.models.Scene", "Scene"));
		addType(new StencylResourceType<ISoundClip>(ISoundClip.class, "com.stencyl.models.Sound", "SOUND"));
		addType(new StencylResourceType<EditableTileset>(EditableTileset.class, "com.stencyl.models.scene.Tileset", "OBJECT"));
		
		//These types are hidden. Not available from normal type menu.
		addType(_DataType, true);
	}
	
	public static DataType<?> fromXML(String s)
	{
		return typeFromXML.get(s);
	}
	
	public static void addType(DataType<?> type)
	{
		addType(type, false);
	}
	
	public static void removeType(DataType<?> type)
	{
		typeFromXML.remove(type.haxeType);
		
		changedTypes.add(type);
	}
	
	public static void addType(DataType<?> type, boolean hidden)
	{
		typeFromXML.put(type.haxeType, type);
		
		changedTypes.add(type);
	}
	
	public static void dispose()
	{
		typeFromXML.clear();
	}

	public static void initNewTypeFields()
	{
		for(DataType<?> type : typeFromXML.values())
			DelayedInitialize.initPropPartial(type.haxeType, type, DelayedInitialize.CALL_FIELDS);
	}

	public static void initNewTypeMethods()
	{
		for(DataType<?> type : typeFromXML.values())
			DelayedInitialize.initPropPartial(type.haxeType, type, DelayedInitialize.CALL_METHODS);
	}

	public static void finishInit()
	{
		DelayedInitialize.clearProps();
		
		for(DataType<?> type : changedTypes)
			Blocks.addDesignModeBlocks(type);
		
		changedTypes.clear();
	}
	
	public static void finishRemove()
	{
		for(DataType<?> type : changedTypes)
			Blocks.removeDesignModeBlocks(type);
		
		changedTypes.clear();
	}
}
