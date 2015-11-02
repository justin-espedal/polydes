package com.polydes.paint.data;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;

public class DataItem implements Comparable<DataItem>, Leaf<DataItem,Folder>
{
	protected ArrayList<LeafListener<DataItem,Folder>> listeners;
	protected Folder parent;
	protected String name;
	protected Object contents;
	protected boolean modified;
	
	public DataItem(String name)
	{
		listeners = new ArrayList<LeafListener<DataItem,Folder>>();
		parent = null;
		this.name = name;
		contents = null;
		modified = false;
	}
	
	@Override
	public void addListener(LeafListener<DataItem,Folder> l)
	{
		listeners.add(l);
	}
	
	@Override
	public void removeListener(LeafListener<DataItem,Folder> l)
	{
		listeners.remove(l);
	}
	
	@Override
	public void setParent(Folder parent, boolean addToParent)
	{
		if(this.parent == parent)
			return;
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
		
		setDirty();
	}
	
	@Override
	public Folder getParent()
	{
		return parent;
	}
	
	@Override
	public void setName(String name)
	{
		if(this.name != name)
		{
			String oldName = this.name;
			this.name = name;
			for(LeafListener<DataItem,Folder> l : listeners) {l.leafNameChanged(this, oldName);}
			
			if(parent != null)
				parent.registerNameChange(oldName, name);
			
			setDirty();
		}
	}
	
	@Override
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
	
	@Override
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
	
	public void setDirty(boolean value)
	{
		modified = value;
		for(LeafListener<DataItem,Folder> l : listeners) {l.leafStateChanged(this);}
		
		if(modified && parent != null && !parent.isDirty())
			parent.setDirty();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean canEditName()
	{
		return true;
	}
	
	@Override
	public ImageIcon getIcon()
	{
		return null;
	}
	
	@Override
	public int compareTo(DataItem item)
	{
		if(item == null)
			return 0;
		
		return getName().compareTo(item.getName());
	}
}