package stencyl.ext.polydes.datastruct.ui.objeditors;

import stencyl.ext.polydes.datastruct.data.structure.StructureTab;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

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
		
		labelEditor = new StringType.SingleLineStringEditor(style);
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
