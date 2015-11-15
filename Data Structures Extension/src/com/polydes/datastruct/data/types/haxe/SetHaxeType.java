package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.DataSetSource;
import com.polydes.common.data.core.DataSetSource.CustomDataSetSource;
import com.polydes.common.data.core.DataSetSources;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.extra.SetType;
import com.polydes.common.data.types.builtin.extra.SetType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class SetHaxeType extends HaxeDataType
{
	public SetHaxeType()
	{
		super(Types._Set, "com.polydes.datastruct.Set", "OBJECT");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(SetType.EDITOR, extras.get("editor", Editor.Checklist));
		if(extras.containsKey("sourceType"))
			props.put(SetType.SOURCE, DataSetSources.get().getItem(extras.get("sourceType", Types._String)));
		else
			props.put(SetType.SOURCE, new CustomDataSetSource(extras.getTyped("source", Types._Array, null)));
		//TODO transform a string into an appropriate predicate
//		props.put(SetType.SOURCE_FILTER, extras.get("sourceFilter", Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(SetType.EDITOR));
		DataSetSource source = props.get(SetType.SOURCE);
		if(source.id.equals("custom"))
			emap.putTyped("source", Types._Array, source);
		else
			emap.put("sourceType", source.id);
		//TODO transform a predicate into a string
//		if(props.containsKey(SetType.SOURCE_FILTER))
//			emap.put("sourceFilter", props.get(SetType.SOURCE_FILTER));
		return emap;
	}

	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		EditorProperties props = panel.getExtras();
		boolean custom = props.<DataSetSource>get(SetType.SOURCE).id.equals("custom");
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
			
			.field(SetType.SOURCE)._collection(DataSetSources.get().values()).add()
			
			.field("sourceList")._array().simpleEditor().genType(Types._String).add()
			
			.field(SetType.SOURCE_FILTER).optional()._editor(null).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(SetType.SOURCE, event -> {
			boolean newCustom = props.<DataSetSource>get(SetType.SOURCE).id.equals("custom");
			panel.setRowVisibility(sheet, "sourceList", newCustom);
			panel.setRowVisibility(sheet, SetType.SOURCE_FILTER, !newCustom);
		});
		
		//TODO
//		customSourceField.addListener(new UpdateListener()
//		{
//			@Override
//			public void updated()
//			{
//				((CustomDataSetSource) e.source).list.clear();
//				((CustomDataSetSource) e.source).list.addAll(customSourceField.getValue());
//				preview.refreshDataItem(previewKey);
//			}
//		});
		
		//TODO ?
//		customSourceRow = panel.addGenericRow(expansion, "Source", customSourceField, StructureObjectPanel.RESIZE_FLAG);
		
		panel.setRowVisibility(sheet, "sourceList", custom);
		panel.setRowVisibility(sheet, SetType.SOURCE_FILTER, !custom);
	}
}
