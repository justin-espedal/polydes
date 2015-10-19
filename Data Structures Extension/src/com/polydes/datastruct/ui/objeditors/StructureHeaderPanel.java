package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.StructureHeader;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureHeaderPanel extends StructureObjectPanel
{
	StructureHeader header;
	
	String oldLabel;
	
	DataEditor<String> labelEditor;
	
	public StructureHeaderPanel(final StructureHeader header, PropertiesSheetStyle style)
	{
		super(style);
		
		this.header = header;
		
		oldLabel = header.getLabel();
		
		//=== Label

		labelEditor = new StringType.SingleLineStringEditor(null, style);
		labelEditor.setValue(header.getLabel());
	
		labelEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				header.setLabel(labelEditor.getValue());
				previewKey.setName(labelEditor.getValue());
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		addGenericRow("Label", labelEditor);
	}
	
	public void revert()
	{
		header.setLabel(oldLabel);
	}
	
	public void dispose()
	{
		clearExpansion(0);
		oldLabel = null;
		labelEditor = null;
	}
}
