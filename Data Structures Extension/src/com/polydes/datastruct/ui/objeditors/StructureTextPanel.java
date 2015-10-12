package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.StructureText;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.StringType;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

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

		labelEditor = new StringType.SingleLineStringEditor(style);
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

		textEditor = new StringType.ExpandingStringEditor(style);
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
