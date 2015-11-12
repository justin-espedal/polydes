package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.ArrayType.SimpleArrayEditor;
import com.polydes.common.data.types.builtin.basic.StringType.SingleLineStringEditor;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.common.data.types.builtin.extra.SelectionType.Editor;
import com.polydes.common.data.types.builtin.extra.SelectionType.Extras;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.utils.DLang;

public class SelectionHaxeType extends HaxeDataType
{
	public SelectionHaxeType()
	{
		super(Types._String, "com.polydes.datastruct.Selection", "TEXT");
	}

	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "Dropdown", "RadioButtons"/*, "Grid", "Cycle"*/);
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
		
		//=== Options
		
		final DataEditor<DataList> optionsField = new SimpleArrayEditor(style, Types._String);
		optionsField.setValue(e.options);
		optionsField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.options = optionsField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Default Value
		
		final DataEditor<String> defaultField = new SingleLineStringEditor(null, style);
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
		panel.addGenericRow(expansion, "Options", optionsField, StructureObjectPanel.RESIZE_FLAG);
		panel.addGenericRow(expansion, "Default", defaultField);
	}
}
