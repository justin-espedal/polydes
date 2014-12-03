package stencyl.ext.polydes.datastruct.data.structure;

import javax.swing.JPanel;

public class StructureTable extends StructureTab
{
	StructureDefinition def;
	
	public StructureTable(StructureDefinition def)
	{
		super("");
		this.def = def;
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
	
	@Override
	public void setDirty(boolean value)
	{
		super.setDirty(value);
		def.dref.setDirty(value);
	}
}