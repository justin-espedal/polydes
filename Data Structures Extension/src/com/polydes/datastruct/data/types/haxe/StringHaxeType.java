package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.StringType.Editor;
import com.polydes.common.data.types.builtin.basic.StringType.Extras;
import com.polydes.common.data.types.builtin.basic.StringType.SingleLineStringEditor;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.utils.DLang;

public class StringHaxeType extends HaxeDataType
{
	public StringHaxeType()
	{
		super(Types._String, "String", "TEXT");
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
		
		DataList editorChoices = DLang.datalist(Types._String, "SingleLine", "Expanding"/*, "Grid", "Cycle"*/);
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
		
		//=== Regex
		
		final DataEditor<String> regexField = new SingleLineStringEditor(null, style);
		regexField.setValue(Lang.or(e.regex, ""));
		regexField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				String val = regexField.getValue();
				if(val.isEmpty())
					e.regex = null;
				else
					e.regex = val;
			}
		});

		panel.addGenericRow(expansion, "Editor", editorChooser);
		panel.addGenericRow(expansion, "Default", defaultField);
		panel.addGenericRow(expansion, "Regex", regexField);
	}
}
