package com.polydes.datastruct.data.types.haxe;

import java.util.Collection;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.extra.SetType;
import com.polydes.common.data.types.builtin.extra.SetType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class SetHaxeType extends HaxeDataType
{
	public SetHaxeType()
	{
		super(Types._Set, "com.polydes.datastruct.Set", "OBJECT");
	}
	
	public static final String SOURCE_TYPE = "sourceType";
	public static final String SOURCE_ID = "sourceId";
	
	public enum SourceType
	{
		Resource,
		Structure,
		Custom
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(SetType.EDITOR, extras.get("editor", Editor.Checklist));
		props.put(SOURCE_TYPE, extras.get(SOURCE_TYPE, SourceType.Custom));
		String sourceId = extras.get(SOURCE_ID, Types._String, null);
		switch(props.<SourceType>get(SOURCE_TYPE))
		{
			case Custom:
				props.put(SetType.SOURCE, extras.getTyped("source", Types._Array, null));
				props.put(SetType.GEN_TYPE, Types._String);
				break;
			case Resource:
				DataStructuresExtension.get().getHaxeTypes().requestValue(sourceId, htype -> {
					props.put(SetType.SOURCE, ((StencylResourceHaxeType<?>) htype).srt.getList());
					props.put(SetType.GEN_TYPE, htype.dataType);
				});
				break;
			case Structure:
				DataStructuresExtension.get().getHaxeTypes().requestValue(sourceId, htype -> {
					props.put(SetType.SOURCE, Structures.getList(((StructureHaxeType) htype).type.def));
					props.put(SetType.GEN_TYPE, htype.dataType);
				});
				break;
		}
		//TODO transform a string into an appropriate predicate
//		props.put(SetType.SOURCE_FILTER, extras.get("sourceFilter", Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(SetType.EDITOR));
		
		SourceType sourceType = props.get(SOURCE_TYPE);
		emap.put(SOURCE_TYPE, sourceType.name());
		if(sourceType == SourceType.Custom)
			emap.putTyped("source", Types._Array, props.<Collection<?>>get("source"));
		else
			emap.put(SOURCE_ID, props.<String>get(SOURCE_ID)); 
		//TODO transform a predicate into a string
//		if(props.containsKey(SetType.SOURCE_FILTER))
//			emap.put("sourceFilter", props.get(SetType.SOURCE_FILTER));
		return emap;
	}
	
	private static final String SOURCE_PROXY = "_" + SetType.SOURCE;
	private static final String FILTER_PROXY = "_" + SetType.SOURCE_FILTER;
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		props.remove(SOURCE_PROXY);
		props.remove(FILTER_PROXY);
		
		sheet.build()
			.field(SOURCE_TYPE)._enum(SourceType.class).add()
			.field(SOURCE_PROXY)._array().simpleEditor().genType(Types._String).add()
			.field(FILTER_PROXY).optional()._string().add()
			.finish();
		
		sheet.addPropertyChangeListener(SOURCE_TYPE, event -> {
			updateSourceType(panel, sheet, props);
		});
		
		sheet.addPropertyChangeListener(SOURCE_PROXY, event -> {
			updateSource(panel, sheet, props);
		});
		
		updateSourceType(panel, sheet, props);
	}

	private void updateSourceType(StructureFieldPanel panel, PropertiesSheetSupport sheet, EditorProperties props)
	{
		SourceType type = props.get(SOURCE_TYPE);
		
		HaxeTypes types = DataStructuresExtension.get().getHaxeTypes();
		
		props.remove(SOURCE_PROXY);
		
		switch(type)
		{
			case Custom:
				sheet.change().field(SOURCE_PROXY)
					._array().simpleEditor().genType(Types._String).change().finish();
				break;
			case Resource:
				sheet.change().field(SOURCE_PROXY)
					._collection(types.values()).filter(htype -> (htype instanceof StencylResourceHaxeType)).change().finish();
				break;
			case Structure:
				sheet.change().field(SOURCE_PROXY)
					._collection(types.values()).filter(htype -> (htype instanceof StructureHaxeType)).change().finish();
				break;
		}
		
		panel.setRowVisibility(sheet, FILTER_PROXY, type != SourceType.Custom);
	}
	
	private void updateSource(StructureFieldPanel panel, PropertiesSheetSupport sheet, EditorProperties props)
	{
		SourceType type = props.get(SOURCE_TYPE);
		
		if(props.get(SOURCE_PROXY) == null)
			return;
		
		switch(type)
		{
			case Custom:
				props.put(SetType.SOURCE, props.get(SOURCE_PROXY));
				props.put(SetType.GEN_TYPE, Types._String);
				break;
			case Resource:
				StencylResourceHaxeType<?> srht = props.get(SOURCE_PROXY);
				props.put(SetType.SOURCE, srht.srt.getList());
				props.put(SetType.GEN_TYPE, srht.dataType);
				break;
			case Structure:
				StructureHaxeType sht = props.get(SOURCE_PROXY);
				props.put(SetType.SOURCE, Structures.getList(sht.type.def));
				props.put(SetType.GEN_TYPE, sht.dataType);
				break;
		}
		
		panel.getPreview().refreshDataItem(panel.getPreviewKey());
	}
}
