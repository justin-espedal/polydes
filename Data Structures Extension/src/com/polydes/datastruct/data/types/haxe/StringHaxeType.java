package com.polydes.datastruct.data.types.haxe;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.data.types.builtin.basic.StringType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class StringHaxeType extends HaxeDataType
{
	public StringHaxeType()
	{
		super(Types._String, "String", "TEXT");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(StringType.EDITOR, extras.get("editor", Editor.SingleLine));
		props.put(StringType.REGEX, extras.get("regex", Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(StringType.EDITOR));
		if(props.containsKey(StringType.REGEX))
			emap.put("regex", props.get(StringType.REGEX));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		EditorProperties props = panel.getExtras();
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(StringType.EDITOR)._enum(StringType.Editor.class).add()
			
			.field(StringType.REGEX)._string().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(StringType.REGEX, event -> {
			if(StringUtils.isEmpty(props.<String>get(StringType.REGEX)))
				props.put(StringType.REGEX, null);
		});
	}
}
