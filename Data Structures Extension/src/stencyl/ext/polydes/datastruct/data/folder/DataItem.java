package stencyl.ext.polydes.datastruct.data.folder;

import java.util.ArrayList;

import javax.swing.ImageIcon;


public class DataItem implements Comparable<DataItem>
{
	protected ArrayList<DataItemListener> listeners;
	protected Folder parent;
	
	protected String name;
	protected ImageIcon icon;
	
	protected EditableObject object;
	
	public DataItem(String name, EditableObject object)
	{
		listeners = new ArrayList<DataItemListener>();
		parent = null;
		this.name = name;
		this.object = object;
	}
	
	public DataItem(String name)
	{
		this(name, null);
	}
	
	public void addListener(DataItemListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(DataItemListener l)
	{
		listeners.remove(l);
	}
	
	public void setParent(Folder parent, boolean addToParent)
	{
		if(this.parent == parent)
			return;
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
		
		setDirty(true);
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
			
			setDirty(true);
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean canEditName()
	{
		return true;
	}
	
	public void setIcon(ImageIcon icon)
	{
		this.icon = icon;
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}
	
	public void setObject(EditableObject object)
	{
		this.object = object;
	}
	
	public EditableObject getObject()
	{
		return object;
	}
	
	public boolean isDirty()
	{
		return object.isDirty();
	}
	
	public void setDirty(boolean value)
	{
		if(object.isDirty() == value)
			return;
		
		object.setDirty(value);
		for(DataItemListener l : listeners) {l.dataItemStateChanged(this);}
		
		if(value && parent != null && !parent.isDirty())
			parent.setDirty(true);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public int compareTo(DataItem item)
	{
		if(item == null)
			return 0;
		
		return getName().compareTo(item.getName());
	}
}