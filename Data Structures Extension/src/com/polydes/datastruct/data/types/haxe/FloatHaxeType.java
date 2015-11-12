package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.FloatType.Editor;
import com.polydes.common.data.types.builtin.basic.FloatType.Extras;
import com.polydes.common.data.types.builtin.basic.FloatType.PlainFloatEditor;
import com.polydes.common.data.types.builtin.basic.IntType.PlainIntegerEditor;
import com.polydes.common.data.types.builtin.extra.SelectionType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.utils.DLang;

public class FloatHaxeType extends HaxeDataType
{
	public FloatHaxeType()
	{
		super(Types._Float, "Float", "NUMBER");
	}

	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		final int decimalPlacesRow;
		final int stepRow;
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "Plain", "Spinner", "Slider");
		final DataEditor<String> editorChooser = new SelectionType.DropdownSelectionEditor(editorChoices);
		editorChooser.setValue(e.editor.name());
		//editorChooser listener later, after stepRow is added.
		
		//=== Min, Max, Decimal Places, Step, Default Value
		
		final DataEditor<Float> minField = new PlainFloatEditor(style);
		minField.setValue(e.min);
		minField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.min = minField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		final DataEditor<Float> maxField = new PlainFloatEditor(style);
		maxField.setValue(e.max);
		maxField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.max = maxField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		final DataEditor<Integer> decimalPlacesField = new PlainIntegerEditor(style);
		decimalPlacesField.setValue(e.decimalPlaces);
		decimalPlacesField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.decimalPlaces = decimalPlacesField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		final DataEditor<Float> stepField = new PlainFloatEditor(style);
		stepField.setValue(e.step);
		stepField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.step = stepField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		final DataEditor<Float> defaultField = new PlainFloatEditor(style);
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		panel.addGenericRow(expansion, "Editor", editorChooser);
		panel.addEnablerRow(expansion, "Minimum", minField, e.min != null);
		panel.addEnablerRow(expansion, "Maximum", maxField, e.max != null);
		decimalPlacesRow = panel.addEnablerRow(expansion, "Decimal Places", decimalPlacesField, e.decimalPlaces != null);
		stepRow = panel.addGenericRow(expansion, "Step", stepField);
		panel.addGenericRow(expansion, "Default", defaultField);
		
		editorChooser.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.editor = Editor.valueOf(editorChooser.getValue());
				preview.refreshDataItem(previewKey);
				panel.setRowVisibility(stepRow, e.editor == Editor.Spinner);
				panel.setRowVisibility(decimalPlacesRow, e.editor == Editor.Slider);
				
			}
		});
		panel.setRowVisibility(stepRow, e.editor == Editor.Spinner);
		panel.setRowVisibility(decimalPlacesRow, e.editor == Editor.Slider);
	}
}
