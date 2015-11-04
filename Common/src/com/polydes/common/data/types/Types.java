package com.polydes.common.data.types;

import com.polydes.common.data.core.DataSetSource;
import com.polydes.common.data.core.DataSetSources;
import com.polydes.common.data.types.builtin.ResourceFolderType;
import com.polydes.common.data.types.builtin.StencylResourceType;
import com.polydes.common.data.types.builtin.UnknownDataType;
import com.polydes.common.data.types.builtin.basic.ArrayType;
import com.polydes.common.data.types.builtin.basic.BoolType;
import com.polydes.common.data.types.builtin.basic.DynamicType;
import com.polydes.common.data.types.builtin.basic.FloatType;
import com.polydes.common.data.types.builtin.basic.IntType;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.data.types.builtin.extra.ColorType;
import com.polydes.common.data.types.builtin.extra.IControlType;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.common.data.types.builtin.extra.SetType;
import com.polydes.common.data.types.hidden.DataTypeType;
import com.polydes.common.ext.ObjectRegistry;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.sound.ISoundClip;
import stencyl.core.lib.ResourceTypes;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.data.EditableBackground;
import stencyl.sw.data.EditableFont;
import stencyl.sw.data.EditableTileset;

public class Types extends ObjectRegistry<DataType<?>>
{
	public static DataTypeType _DataType = new DataTypeType();
	
	public static ArrayType _Array = new ArrayType();
	public static BoolType _Bool = new BoolType();
	public static DynamicType _Dynamic = new DynamicType();
	public static FloatType _Float = new FloatType();
	public static IntType _Int = new IntType();
	public static StringType _String = new StringType();
	
	public static ColorType _Color = new ColorType();
	public static IControlType _Control = new IControlType();
//	public static ExtrasImageType _ExtrasImage = new ExtrasImageType();
	public static SelectionType _Selection = new SelectionType();
	public static SetType _Set = new SetType();
	
	public static StencylResourceType<IActorType> _Actor = new StencylResourceType<>(ResourceTypes.actor);
	public static StencylResourceType<EditableBackground> _Background = new StencylResourceType<>(ResourceTypes.background);
	public static StencylResourceType<EditableFont> _Font = new StencylResourceType<>(ResourceTypes.font);
	public static StencylResourceType<SceneModel> _Scene = new StencylResourceType<>(ResourceTypes.scene);
	public static StencylResourceType<ISnippet> _Snippet = new StencylResourceType<>(ResourceTypes.snippet);
	public static StencylResourceType<ISoundClip> _Sound = new StencylResourceType<>(ResourceTypes.sound);
	public static StencylResourceType<EditableTileset> _Tileset = new StencylResourceType<>(ResourceTypes.tileset);
	public static ResourceFolderType _ResourceFolder = new ResourceFolderType();
	
	public static StencylResourceType<?>[] srts;
	static
	{
		srts = new StencylResourceType[]
		{
			_Actor,
			_Background,
			_Font,
			_Scene,
			_Snippet,
			_Sound,
			_Tileset
		};
	}
	
	//===
	
	private static Types instance;
	
	public static Types get()
	{
		if(instance == null)
			instance = new Types();
		
		return instance;
	}
	
	private Types()
	{
		addBasicTypes();
	}
	
	public void addBasicTypes()
	{
		registerItem(_DataType);
		
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
		registerItem(_ResourceFolder);
		
		for(StencylResourceType<?> srt : srts)
		{
			//TODO: Add structure types from DSExtension
			registerItem(srt);
			DataSetSources.get().registerItem(new DataSetSource(srt.id, srt, () -> srt.getList()));
		}
	}
	
	@Override
	public DataType<?> generatePlaceholder(String key)
	{
		return new UnknownDataType(key);
	}
}
