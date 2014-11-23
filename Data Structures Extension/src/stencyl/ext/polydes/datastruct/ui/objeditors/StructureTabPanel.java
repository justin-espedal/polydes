package stencyl.ext.polydes.datastruct.ui.objeditors;

import javax.swing.JTextField;

import stencyl.ext.polydes.datastruct.data.structure.StructureTab;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;

public class StructureTabPanel extends StructureObjectPanel
{
	StructureTab tab;
	
	String oldLabel;
	
	JTextField labelField;
	
	public StructureTabPanel(final StructureTab tab, PropertiesSheetStyle style)
	{
		super(style);
		
		this.tab = tab;
		
		oldLabel = tab.getLabel();
		
		//=== Label

		labelField = style.createTextField();
		labelField.setText(tab.getLabel());
	
		labelField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				tab.setLabel(labelField.getText());
			}
		});
		
		addGenericRow("Label", labelField);
		finishBlock(5, style.rowgap, true);
	}
	
	public void revert()
	{
		tab.setLabel(oldLabel);
	}
	
	public void dispose()
	{
		removeAll();
		oldLabel = null;
		labelField = null;
	}
}
