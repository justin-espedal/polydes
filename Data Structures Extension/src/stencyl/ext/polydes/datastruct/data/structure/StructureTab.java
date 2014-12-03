package stencyl.ext.polydes.datastruct.data.structure;

import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureTabPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureTab extends EditableObject
{
	private String label;
	
	public StructureTab(String label)
	{
		this.label = label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	private StructureTabPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureTabPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}
	
	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}
	
	@Override
	public void revertChanges()
	{
		editor.revert();
	}
}
