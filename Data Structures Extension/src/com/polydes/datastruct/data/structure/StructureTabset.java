package com.polydes.datastruct.data.structure;

import javax.swing.JPanel;

import com.polydes.datastruct.data.folder.EditableObject;

public class StructureTabset extends EditableObject
{
	@Override
	public String toString()
	{
		return "Tabset";
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
