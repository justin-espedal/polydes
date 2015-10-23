package com.polydes.datastruct.data.folder;

import javax.swing.JPanel;

public abstract class EditableObject
{
	public static JPanel BLANK_EDITOR = new JPanel();
	
	private boolean dirty;
	
	public abstract JPanel getEditor();
	public abstract void disposeEditor();
	public abstract void revertChanges();
	
	public void setDirty(boolean value)
	{
		dirty = value;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
}
