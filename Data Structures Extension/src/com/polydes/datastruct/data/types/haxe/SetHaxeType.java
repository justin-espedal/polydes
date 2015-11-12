package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.core.DataList;
import com.polydes.common.data.core.DataSetSource;
import com.polydes.common.data.core.DataSetSource.CustomDataSetSource;
import com.polydes.common.data.core.DataSetSources;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.ArrayType.SimpleArrayEditor;
import com.polydes.common.data.types.builtin.basic.StringType.SingleLineStringEditor;
import com.polydes.common.data.types.builtin.extra.SetType.Extras;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class SetHaxeType extends HaxeDataType
{
	public SetHaxeType()
	{
		super(Types._Set, "com.polydes.datastruct.Set", "OBJECT");
	}

	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		final int customSourceRow;
		final int dataFilterRow;
		
		//=== Editor
		
//		DataList editorChoices = Lang.datalist(Types._String, "Checklist"/*, "Grid"*/);
//		final DataEditor<String> editorChooser = new SelectionType.DropdownSelectionEditor(editorChoices);
//		editorChooser.setValue("Plain");
//		editorChooser.addListener(new UpdateListener()
//		{
//			@Override
//			public void updated()
//			{
//				e.editor = Editor.valueOf(editorChooser.getValue());
//				preview.refreshDataItem(previewKey);
//			}
//		});
		
		//=== Sources
		
		boolean custom = e.source.id.equals("custom");
		
		final UpdatingCombo<DataSetSource> sourceChooser = new UpdatingCombo<DataSetSource>(DataSetSources.get().values(), null);
		sourceChooser.setSelectedItem(e.source);
		sourceChooser.addActionListener((event) -> {
			e.source = sourceChooser.getSelected();
			preview.refreshDataItem(previewKey);
		});
		
		final DataEditor<DataList> customSourceField = new SimpleArrayEditor(style, Types._String);
		customSourceField.setValue(custom ? ((CustomDataSetSource) e.source).list : null);
		customSourceField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				((CustomDataSetSource) e.source).list.clear();
				((CustomDataSetSource) e.source).list.addAll(customSourceField.getValue());
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Source Filter
		
		final DataEditor<String> filterField = new SingleLineStringEditor(null, style);
		filterField.setValue(e.sourceFilter);
		filterField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.sourceFilter = filterField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//panel.addGenericRow(expansion, "Editor", editorChooser);
		
		panel.addGenericRow(expansion, "Source", sourceChooser);
		dataFilterRow = panel.addEnablerRow(expansion, "Filter", filterField, e.sourceFilter != null);
		customSourceRow = panel.addGenericRow(expansion, "Source", customSourceField, StructureObjectPanel.RESIZE_FLAG);
		
		sourceChooser.addActionListener(event -> {
			boolean newCustom = e.source.id.equals("custom");
			panel.setRowVisibility(dataFilterRow, !newCustom);
			panel.setRowVisibility(customSourceRow, newCustom);
			
			if(newCustom)
			{
				e.source = new CustomDataSetSource(new DataList(Types._String));
				e.sourceFilter = null;
			}
			
			preview.refreshDataItem(previewKey);
		});
		
		panel.setRowVisibility(dataFilterRow, !custom);
		panel.setRowVisibility(customSourceRow, custom);
	}
}
