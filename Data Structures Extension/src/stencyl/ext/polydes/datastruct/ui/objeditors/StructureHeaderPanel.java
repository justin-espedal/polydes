package stencyl.ext.polydes.datastruct.ui.objeditors;

import stencyl.ext.polydes.datastruct.data.structure.StructureHeader;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

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

		labelEditor = new StringType.SingleLineStringEditor(style);
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
