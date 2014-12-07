package stencyl.ext.polydes.paint.data;

import java.util.ArrayList;

public class DataItem
{
	protected ArrayList<DataItemListener> listeners;
	protected Folder parent;
	protected String name;
	protected Object contents;
	protected boolean modified;
	
	public DataItem(String name)
	{
		listeners = new ArrayList<DataItemListener>();
		parent = null;
		this.name = name;
		contents = null;
		modified = false;
	}
	
	public void addListener(DataItemListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(DataItemListener l)
	{
		listeners.remove(l);
	}
	
	public void setParent(Folder parent)
	{
		if(this.parent == parent)
			return;
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		parent.addItem(this);
		
		setDirty();
	}
	
	public Folder getParent()
	{
		return parent;
	}
	
	public void setName(String name)
	{
		if(this.name != name)
		{
			String oldName = this.name;
			this.name = name;
			for(DataItemListener l : listeners) {l.dataItemNameChanged(this, oldName);}
			
			if(parent != null)
				parent.registerNameChange(oldName, name);
			
			setDirty();
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setContents(Object newContents)
	{
		contents = newContents;
		
		setDirty();
	}
	
	public Object getContents()
	{
		return contents;
	}
	
	public boolean isDirty()
	{
		return modified;
	}
	
	public void setClean()
	{
		if(modified)
			setDirty(false);
	}
	
	public void setDirty()
	{
		if(!modified)
			setDirty(true);
	}
	
	private void setDirty(boolean value)
	{
		modified = value;
		for(DataItemListener l : listeners) {l.dataItemStateChanged(this);}
		
		if(modified && parent != null && !parent.isDirty())
			parent.setDirty();
	}
	
	public String toString()
	{
		return name + (isDirty() ? "*" : "" );
	}
}