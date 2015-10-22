package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureTabPanel extends StructureObjectPanel
{
	StructureTab tab;
	
	String oldLabel;
	
	DataEditor<String> labelEditor;
	
	public StructureTabPanel(final StructureTab tab, PropertiesSheetStyle style)
	{
		super(style);
		
		this.tab = tab;
		
		oldLabel = tab.getLabel();
		
		//=== Label
		
		labelEditor = new StringType.SingleLineStringEditor(null, style);
		labelEditor.setValue(tab.getLabel());
		labelEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				tab.setLabel(labelEditor.getValue());
				previewKey.setName(labelEditor.getValue());
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		addGenericRow("Label", labelEditor);
	}
	
	public void revert()
	{
		tab.setLabel(oldLabel);
	}
	
	public void dispose()
	{
		clearExpansion(0);
		oldLabel = null;
		labelEditor = null;
	}
}
