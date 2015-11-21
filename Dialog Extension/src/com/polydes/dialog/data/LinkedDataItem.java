package com.polydes.dialog.data;

import com.polydes.common.nodes.DefaultEditableLeaf;
import com.polydes.dialog.app.editors.DataItemEditor;

/*
 * DefaultLeaf that is linked to a data editor.
 * If the editor is "dirty", so is this item.
 * Can receive updated contents from a dirty editor. 
 */

public class LinkedDataItem extends DefaultEditableLeaf
{
	private DataItemEditor editor;
	
	public LinkedDataItem(String name)
	{
		super(name);
	}
	
	@Override
	public boolean isDirty()
	{
		return (super.isDirty() || (editor != null && editor.isDirty()));
	}
	
	@Override
	public void setName(String name)
	{
		super.setName(name);
		if(editor != null)
			editor.nameChanged(name);
	}
	
	public void updateContents()
	{
		if(editor != null && editor.isDirty())
		{
			this.setUserData(editor.getContents());
			editor.setClean();
		}
	}
	
	public void setEditor(DataItemEditor editor)
	{
		this.editor = editor;
	}
	
	public DataItemEditor getEditor()
	{
		return editor;
	}
}
