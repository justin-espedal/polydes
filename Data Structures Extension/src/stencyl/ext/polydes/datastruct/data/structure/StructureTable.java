package stencyl.ext.polydes.datastruct.data.structure;

import javax.swing.JPanel;

public class StructureTable extends StructureTab
{
	public StructureTable()
	{
		super("");
	}
	
	@Override
	public JPanel getEditor()
	{
		return new JPanel();
	}
	
	@Override
	public void disposeEditor()
	{
	}
	
	@Override
	public void revertChanges()
	{
	}
}