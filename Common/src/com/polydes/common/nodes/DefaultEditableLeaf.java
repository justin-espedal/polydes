package com.polydes.common.nodes;

import com.polydes.common.ui.object.EditableObject;

public class DefaultEditableLeaf extends DefaultViewableLeaf
{
	public DefaultEditableLeaf(String name, EditableObject object)
	{
		super(name, object);
	}
	
	public DefaultEditableLeaf(String name)
	{
		this(name, null);
	}
	
	@Override
	public boolean isDirty()
	{
		return ((EditableObject) userData).isDirty();
	}
	
	@Override
	public void setDirty(boolean value)
	{
		if(isDirty() == value)
			return;
		
		((EditableObject) userData).setDirty(value);
		pcs.firePropertyChange(DIRTY, !value, value);
		
		if(value && parent != null && !parent.isDirty())
			parent.setDirty(true);
	}
}