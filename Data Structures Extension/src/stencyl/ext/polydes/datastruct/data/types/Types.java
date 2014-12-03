package stencyl.ext.polydes.datastruct.data.types;

import java.util.HashMap;
import java.util.LinkedHashMap;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.engine.sound.ISoundClip;
import stencyl.ext.polydes.datastruct.data.types.builtin.ArrayType;
import stencyl.ext.polydes.datastruct.data.types.builtin.BooleanType;
import stencyl.ext.polydes.datastruct.data.types.builtin.DimensionType;
import stencyl.ext.polydes.datastruct.data.types.builtin.DynamicType;
import stencyl.ext.polydes.datastruct.data.types.builtin.ExtrasImageType;
import stencyl.ext.polydes.datastruct.data.types.builtin.FloatType;
import stencyl.ext.polydes.datastruct.data.types.builtin.IControlType;
import stencyl.ext.polydes.datastruct.data.types.builtin.IntType;
import stencyl.ext.polydes.datastruct.data.types.builtin.PointType;
import stencyl.ext.polydes.datastruct.data.types.builtin.RectangleType;
import stencyl.ext.polydes.datastruct.data.types.builtin.SelectionType;
import stencyl.ext.polydes.datastruct.data.types.builtin.SetType;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType;
import stencyl.ext.polydes.datastruct.data.types.general.StencylResourceType;
import stencyl.ext.polydes.datastruct.data.types.hidden.DataTypeType;
import stencyl.sw.data.EditableBackground;
import stencyl.sw.data.EditableFont;
import stencyl.sw.data.EditableTileset;

public class Types
{
	public static HashMap<String, DataType<?>> typeFromXML = new LinkedHashMap<String, DataType<?>>();
	
	//===
	
	public static DataTypeType _DataType = new DataTypeType();
	
	public static DynamicType _Dynamic = new DynamicType();
	public static ArrayType _Array = new ArrayType();
	public static BooleanType _Boolean = new BooleanType();
	public static DimensionType _Dimension = new DimensionType();
	public static ExtrasImageType _ExtrasImage = new ExtrasImageType();
	public static FloatType _Float = new FloatType();
	public static IControlType _IControl = new IControlType();
	public static IntType _Integer = new IntType();
	public static PointType _Point = new PointType();
	public static RectangleType _Rectangle = new RectangleType();
	public static StringType _String = new StringType();
	public static SetType _Set = new SetType();
	public static SelectionType _Selection = new SelectionType();
	
	//===
	
	public static void addBasicTypes()
	{
		addType(_Boolean);
		//addType(new ColorType());
		addType(_Integer);
		addType(_Float);
		addType(_String);
		addType(_Point);
		addType(_Rectangle);
		addType(_Dimension);
		addType(_ExtrasImage);
		addType(_IControl);
		//ActorType, Background, Font, Scene, Sound, Tileset
		addType(new StencylResourceType<IActorType>(IActorType.class, "com.stencyl.models.actor.ActorType", "ACTORTYPE", "ActorType"));
		addType(new StencylResourceType<EditableBackground>(EditableBackground.class, "com.stencyl.models.Background", "OBJECT", "Background"));
		addType(new StencylResourceType<EditableFont>(EditableFont.class, "com.stencyl.models.Font", "FONT", "Font"));
		//types.put(SceneModel.class, new StencylResourceType<SceneModel>(SceneModel.class, "com.stencyl.models.Scene", "Scene"));
		addType(new StencylResourceType<ISoundClip>(ISoundClip.class, "com.stencyl.models.Sound", "SOUND", "Sound"));
		addType(new StencylResourceType<EditableTileset>(EditableTileset.class, "com.stencyl.models.scene.Tileset", "OBJECT", "Tileset"));
		addType(_Dynamic);
		addType(_Array);
		addType(_Selection);
		addType(_Set);
		
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
	
	public static void addType(DataType<?> type, boolean hidden)
	{
		String xml = type.xml;
		
		typeFromXML.put(xml, type);
	}
	
	public static void dispose()
	{
		typeFromXML.clear();
	}
}
