package com.polydes.datastruct.data.structure;

import javax.swing.JPanel;

import com.polydes.datastruct.data.folder.EditableObject;
import com.polydes.datastruct.ui.objeditors.StructureTextPanel;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureText extends EditableObject
{
	private String label;
	private String text;
	
	public StructureText(String label, String text)
	{
		this.label = label;
		this.text = text;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	private StructureTextPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureTextPanel(this, PropertiesSheetStyle.LIGHT);
		
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
