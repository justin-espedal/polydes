package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.ArrayType.Editor;
import com.polydes.common.data.types.builtin.basic.ArrayType.Extras;
import com.polydes.common.data.types.builtin.basic.ArrayType.SimpleArrayEditor;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.utils.DLang;

public class ArrayHaxeType extends HaxeDataType
{
	public ArrayHaxeType()
	{
		super(Types._Array, "Array", "LIST");
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "Standard", "Simple"/*, "Grid", "Cycle"*/);
		final DataEditor<String> editorChooser = new SelectionType.DropdownSelectionEditor(editorChoices);
		editorChooser.setValue(e.editor.name());
		editorChooser.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.editor = Editor.valueOf(editorChooser.getValue());
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Type, Default
		
//		final DataEditor<DataType> typeField = new DataTypeEditor(DataTypeType.dynamicArraySubTypes);
		final SimpleArrayEditor defaultField = new SimpleArrayEditor(panel.style, e.genType);
		
//		typeField.setValue(e.genType);
//		typeField.addListener(new UpdateListener()
//		{
//			@Override
//			public void updated()
//			{
//				e.genType = typeField.getValue();
//				preview.refreshDataItem(previewKey);
//				defaultField.setType(typeField.getValue());
//			}
//		});
		
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addGenericRow(expansion, "Editor", editorChooser);
//		panel.addGenericRow(expansion, "Data Type", typeField);
		panel.addGenericRow(expansion, "Default", defaultField, StructureObjectPanel.RESIZE_FLAG);
	}
}
