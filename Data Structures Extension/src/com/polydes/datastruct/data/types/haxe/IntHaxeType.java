package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.IntType;
import com.polydes.common.data.types.builtin.basic.IntType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class IntHaxeType extends HaxeDataType
{
	public IntHaxeType()
	{
		super(Types._Int, "Int", "NUMBER");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(IntType.EDITOR, extras.get("editor", Editor.Plain));
		props.put(IntType.MIN, extras.get("min", Types._Int, null));
		props.put(IntType.MAX, extras.get("max", Types._Int, null));
		props.put(IntType.STEP, extras.get("step", Types._Int, 1));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(IntType.EDITOR));
		if(props.containsKey(IntType.MIN))
			emap.put("min", props.get(IntType.MIN));
		if(props.containsKey(IntType.MAX))
			emap.put("max", props.get(IntType.MAX));
		emap.put("step", props.get(IntType.STEP));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		final EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(IntType.EDITOR)._enum(IntType.Editor.class).add()
			
			.field(IntType.MIN).optional()._int().add()
			
			.field(IntType.MAX).optional()._int().add()
			
			.field(IntType.STEP)._int().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(IntType.EDITOR, event -> {
			panel.setRowVisibility(sheet, IntType.STEP, props.get(IntType.EDITOR) == Editor.Spinner);
		});
		
		panel.setRowVisibility(sheet, IntType.STEP, props.get(IntType.EDITOR) == Editor.Spinner);
	}
}
