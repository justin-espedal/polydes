package stencyl.ext.polydes.datastruct.data.structure;

import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureHeaderPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureHeader extends EditableObject
{
	private String label;
	
	public StructureHeader(String label)
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
	
	private StructureHeaderPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureHeaderPanel(this, PropertiesSheetStyle.LIGHT);
		
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
