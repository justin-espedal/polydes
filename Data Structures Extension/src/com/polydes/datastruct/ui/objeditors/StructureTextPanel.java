package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureText;

public class StructureTextPanel extends StructureObjectPanel
{
	StructureText text;
	
	String oldLabel;
	String oldText;
	
	DataEditor<String> labelEditor;
	DataEditor<String> textEditor;
	
	public StructureTextPanel(final StructureText text, PropertiesSheetStyle style)
	{
		super(style);
		
		this.text = text;
		
		oldLabel = text.getLabel();
		oldText = text.getText();
		
		//=== Label

		labelEditor = new StringType.SingleLineStringEditor(null, style);
		labelEditor.setValue(text.getLabel());
	
		labelEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				text.setLabel(labelEditor.getValue());
				previewKey.setName(labelEditor.getValue());
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		addGenericRow("Label", labelEditor);
		
		//=== Text

		textEditor = new StringType.ExpandingStringEditor(null, style);
		textEditor.setValue(text.getText());
		
		textEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				text.setText(textEditor.getValue());
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		addGenericRow("Text", textEditor);
	}
	
	public void revert()
	{
		text.setLabel(oldLabel);
		text.setText(oldText);
	}
	
	public void dispose()
	{
		clearExpansion(0);
		oldLabel = null;
		oldText = null;
		labelEditor = null;
	}
}
