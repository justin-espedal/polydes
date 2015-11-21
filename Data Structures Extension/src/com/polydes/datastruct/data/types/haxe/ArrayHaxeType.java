package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.util.Lang.or;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.ArrayType;
import com.polydes.common.data.types.builtin.basic.ArrayType.Editor;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.DataStructuresExtension;
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
		if(extras.containsKey("genTypeProps"))
			props.put(ArrayType.GEN_TYPE_PROPS, new UnknownProperties(extras.getMap("genTypeProps")));
		
		extras.requestDataType("genType", HaxeTypes._String, (type) -> {
			props.put(ArrayType.GEN_TYPE, type.dataType);
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
		
		String genType = props.<DataType<?>>get(ArrayType.GEN_TYPE).getId();
		emap.put("genType", DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(genType).getHaxeType());
		//TODO
//		emap.put("genTypeProps", props.<EditorProperties>get(ArrayType.GEN_TYPE_PROPS));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		final PropertiesSheet preview = panel.getPreview();
		final DefaultLeaf previewKey = panel.getPreviewKey();
		
		HaxeDataTypeType hdt = new HaxeDataTypeType();
		EditorProperties props = panel.getExtras();
		
		String genTypeProxy = "_" + ArrayType.GEN_TYPE;
		String typeID = or(props.<DataType<?>>get(ArrayType.GEN_TYPE), Types._String).getId();
		props.put(genTypeProxy, DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(typeID));
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(ArrayType.EDITOR)._enum(ArrayType.Editor.class).add()
			
			.field(genTypeProxy).label("Data Type")._editor(hdt).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(genTypeProxy, event -> {
			HaxeDataType newType = (HaxeDataType) event.getNewValue();
			props.put(ArrayType.GEN_TYPE, newType.dataType);
			preview.refreshDefaultLeaf(previewKey);
		});
	}
}
