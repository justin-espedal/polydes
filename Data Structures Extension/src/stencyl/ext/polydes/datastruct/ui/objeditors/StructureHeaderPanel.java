package stencyl.ext.polydes.datastruct.ui.objeditors;

import javax.swing.JTextField;

import stencyl.ext.polydes.datastruct.data.structure.StructureHeader;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;

public class StructureHeaderPanel extends StructureObjectPanel
{
	StructureHeader header;
	
	String oldLabel;
	
	JTextField labelField;
	
	public StructureHeaderPanel(final StructureHeader header, PropertiesSheetStyle style)
	{
		super(style);
		
		this.header = header;
		
		oldLabel = header.getLabel();
		
		//=== Label

		labelField = style.createTextField();
		labelField.setText(header.getLabel());
	
		labelField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				header.setLabel(labelField.getText());
			}
		});
		
		addGenericRow("Label", labelField);
		finishBlock(5, style.rowgap, true);
	}
	
	public void revert()
	{
		header.setLabel(oldLabel);
	}
	
	public void dispose()
	{
		removeAll();
		oldLabel = null;
		labelField = null;
	}
}
