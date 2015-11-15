package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.ArrayType;
import com.polydes.common.data.types.builtin.basic.ArrayType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeDataTypeType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.data.types.haxe.UnknownHaxeType.UnknownProperties;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class ArrayHaxeType extends HaxeDataType
{
	public ArrayHaxeType()
	{
		super(Types._Array, "Array", "LIST");
	}
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		final EditorProperties props = new EditorProperties();
		props.put(ArrayType.EDITOR, extras.get("editor", Editor.Standard));
		if(extras.containsKey("genTypeExtras"))
			props.put(ArrayType.GEN_TYPE_PROPS, new UnknownProperties(extras.getMap("genTypeExtras")));
		
		extras.requestDataType("genType", HaxeTypes._String, (type) -> {
			props.put(ArrayType.GEN_TYPE, type);
			Object genExtra = props.get(ArrayType.GEN_TYPE_PROPS);
			if(genExtra instanceof UnknownProperties)
				props.put(ArrayType.GEN_TYPE_PROPS, type.loadExtras(((UnknownProperties) genExtra).getMap()));
		});
		
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.put("editor", props.get(ArrayType.EDITOR));
		emap.put("genType", props.<DataType<?>>get(ArrayType.GEN_TYPE).getId());
		//TODO
//		emap.put("genTypeExtras", props.<EditorProperties>get(ArrayType.GEN_TYPE_PROPS));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		
		HaxeDataTypeType hdt = new HaxeDataTypeType();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(ArrayType.EDITOR)._enum(ArrayType.Editor.class).add()
			
			.field(ArrayType.GEN_TYPE).label("Data Type")._editor(hdt).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(ArrayType.GEN_TYPE, event -> {
			HaxeDataType newType = (HaxeDataType) event.getNewValue();
			panel.getExtras().put(ArrayType.GEN_TYPE, newType.dataType);
			preview.refreshDataItem(previewKey);
			
			//TODO: Make sure this works
			panel.getSheet().change().field(DataType.DEFAULT_VALUE)._array().genType(newType.dataType);
		});
		
		//TODO: Resize flag
//		panel.addGenericRow(expansion, "Default", defaultField, StructureObjectPanel.RESIZE_FLAG);
	}
}
