package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.common.data.types.builtin.extra.SelectionType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class SelectionHaxeType extends HaxeDataType
{
	public SelectionHaxeType()
	{
		super(Types._String, "com.polydes.datastruct.Selection", "TEXT");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(SelectionType.EDITOR, extras.get("editor", Editor.Dropdown));
		props.put(SelectionType.OPTIONS, extras.get("options", Types._Array, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(SelectionType.EDITOR));
		if(props.containsKey(SelectionType.OPTIONS))
			emap.put("options", Types._Array.encode(props.get(SelectionType.OPTIONS)));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(SelectionType.EDITOR)._enum(SelectionType.Editor.class).add()
			
			.field(SelectionType.OPTIONS)._array().simpleEditor().add()
			
			.finish();
		
		//TODO ?
//		panel.addGenericRow(expansion, "Options", optionsField, StructureObjectPanel.RESIZE_FLAG);
	}
}
