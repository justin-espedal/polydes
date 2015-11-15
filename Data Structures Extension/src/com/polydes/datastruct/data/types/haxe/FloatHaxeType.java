package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.FloatType;
import com.polydes.common.data.types.builtin.basic.FloatType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class FloatHaxeType extends HaxeDataType
{
	public FloatHaxeType()
	{
		super(Types._Float, "Float", "NUMBER");
	}

	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(FloatType.EDITOR, extras.get("editor", Editor.Plain));
		props.put(FloatType.MIN, extras.get("min", Types._Float, null));
		props.put(FloatType.MAX, extras.get("max", Types._Float, null));
		props.put(FloatType.DECIMAL_PLACES, extras.get("decimalPlaces", Types._Int, null));
		props.put(FloatType.STEP, extras.get("step", Types._Float, 0.01f));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(FloatType.EDITOR));
		if(props.containsKey(FloatType.MIN))
			emap.put("min", props.get(FloatType.MIN));
		if(props.containsKey(FloatType.MAX))
			emap.put("max", props.get(FloatType.MAX));
		if(props.containsKey(FloatType.DECIMAL_PLACES))
			emap.put("decimalPlaces", props.get(FloatType.DECIMAL_PLACES));
		emap.put("step", props.get(FloatType.STEP));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		final EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(FloatType.EDITOR)._enum(FloatType.Editor.class).add()
			
			.field(FloatType.MIN).optional()._float().add()
			
			.field(FloatType.MAX).optional()._float().add()
			
			.field(FloatType.DECIMAL_PLACES).optional()._int().add()
			
			.field(FloatType.STEP)._float().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(FloatType.EDITOR, event -> {
			panel.setRowVisibility(sheet, FloatType.STEP, props.get(FloatType.EDITOR) == Editor.Spinner);
			panel.setRowVisibility(sheet, FloatType.DECIMAL_PLACES, props.get(FloatType.EDITOR) == Editor.Slider);
		});
		
		panel.setRowVisibility(sheet, FloatType.STEP, props.get(FloatType.EDITOR) == Editor.Spinner);
		panel.setRowVisibility(sheet, FloatType.DECIMAL_PLACES, props.get(FloatType.EDITOR) == Editor.Slider);
	}
}
