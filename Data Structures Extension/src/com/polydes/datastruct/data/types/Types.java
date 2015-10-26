package com.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.polydes.datastruct.Blocks;
import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.builtin.UnknownDataType;
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
import com.polydes.datastruct.data.types.general.HaxeObjectType;
import com.polydes.datastruct.data.types.general.StencylResourceType;
import com.polydes.datastruct.data.types.hidden.DataTypeType;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.engine.sound.ISoundClip;
import stencyl.sw.data.EditableBackground;
import stencyl.sw.data.EditableFont;
import stencyl.sw.data.EditableTileset;

public class Types
{
	public static HashMap<String, DataType<?>> typeFromXML = new LinkedHashMap<>();
	public static HashSet<DataType<?>> addedTypes = new HashSet<>();
	public static HashSet<DataType<?>> removedTypes = new HashSet<>();
	
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
	
	private static boolean initialized;
	
	public static boolean isInitialized()
	{
		return initialized;
	}
	
	public static void initialize()
	{
		addBasicTypes();
	}
	
	public static void addBasicTypes()
	{
		if(initialized)
			return;
		initialized = true;
		
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
		if(isUnknown(type.haxeType))
			realizeUnknown(type.haxeType, type);
		else
			addType(type, false);
	}
	
	public static void removeType(DataType<?> type)
	{
		typeFromXML.remove(type.haxeType);
		
		removedTypes.add(type);
	}
	
	public static void addType(DataType<?> type, boolean hidden)
	{
		//Catch anything holding unknown types that have already been realized.
		if(type instanceof HaxeObjectType)
			for(HaxeField f : ((HaxeObjectType) type).getDef().fields)
				if(f.type instanceof UnknownDataType && !isUnknown(f.type.haxeType))
					f.type = typeFromXML.get(f.type.haxeType);
		
		typeFromXML.put(type.haxeType, type);
		
		addedTypes.add(type);
	}
	
	public static void dispose()
	{
		typeFromXML.clear();
		addedTypes.clear();
		removedTypes.clear();
		unknownTypes.clear();
		initialized = false;
	}

	/*-------------------------------------*\
	 * Unknown Data Types
	\*-------------------------------------*/ 
	
	private static HashMap<String, UnknownDataType> unknownTypes = new HashMap<>();
	
	public static void addUnknown(String s)
	{
		UnknownDataType newType = new UnknownDataType(s);
		unknownTypes.put(s, newType);
		addType(newType, false);
	}
	
	public static boolean isUnknown(String s)
	{
		return unknownTypes.containsKey(s);
	}
	
	public static void realizeUnknown(String s, DataType<?> type)
	{
		UnknownDataType unknown = unknownTypes.remove(s);
		
		if(unknown == null)
			throw new IllegalArgumentException("There is no unknown type \"" + s + "\"");
		
		for(StructureDefinition def : StructureDefinitions.defMap.values())
		{
			ArrayList<StructureField> realizedFields = new ArrayList<>();
			for(StructureField field : def.getFields())
			{
				if(field.getType() == unknown)
				{
					field.realizeType(type);
					realizedFields.add(field);
				}
			}
			for(Structure struct : Structures.structures.get(def))
				for(StructureField field : realizedFields)
					struct.setPropertyFromString(field, (String) struct.getProperty(field));
		}
		
		for(DataType<?> t : typeFromXML.values())
			if(t instanceof HaxeObjectType)
				for(HaxeField f : ((HaxeObjectType) t).getDef().fields)
					if(f.type == unknown)
						f.type = type;
		
		addType(type, false);
	}
	
	/*-------------------------------------*\
	 * Delayed Initialization
	\*-------------------------------------*/ 
	
	public static void resolveChanges()
	{
		for(DataType<?> type : addedTypes)
			if(!removedTypes.contains(type))
				Blocks.addDesignModeBlocks(type);
		for(DataType<?> type : removedTypes)
			if(!addedTypes.contains(type))
				Blocks.removeDesignModeBlocks(type);
	}
}
