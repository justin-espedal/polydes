package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureHeader;

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
