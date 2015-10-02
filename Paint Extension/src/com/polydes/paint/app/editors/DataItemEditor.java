package com.polydes.paint.app.editors;

public interface DataItemEditor
{
	public boolean isDirty();
	public Object getContents();
	public void setClean();
	public void setDirty();
	public void nameChanged(String name);
}
