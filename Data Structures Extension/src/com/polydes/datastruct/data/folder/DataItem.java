package com.polydes.datastruct.data.folder;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;
import com.polydes.common.ui.object.EditableObject;
import com.polydes.common.ui.object.ViewableObject;


public class DataItem implements Comparable<DataItem>, Leaf<DataItem,Folder>, ViewableObject
{
	protected ArrayList<LeafListener<DataItem,Folder>> listeners;
	protected Folder parent;
	
	protected String name;
	protected ImageIcon icon;
	
	protected EditableObject object;
	
	public DataItem(String name, EditableObject object)
	{
		listeners = new ArrayList<LeafListener<DataItem,Folder>>();
		parent = null;
		this.name = name;
		this.object = object;
	}
	
	public DataItem(String name)
	{
		this(name, null);
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
		{
			Folder oldParent = this.parent;
			this.parent = null;
			oldParent.removeItem(this);
		}
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
		
		setDirty(true);
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
			
			setDirty(true);
		}
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean canEditName()
	{
		return true;
	}
	
	public void setIcon(ImageIcon icon)
	{
		this.icon = icon;
	}
	
	@Override
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
	
	@Override
	public boolean isDirty()
	{
		return object.isDirty();
	}
	
	public void setDirty(boolean value)
	{
		if(object.isDirty() == value)
			return;
		
		object.setDirty(value);
		for(LeafListener<DataItem,Folder> l : listeners) {l.leafStateChanged(this);}
		
		if(value && parent != null && !parent.isDirty())
			parent.setDirty(true);
	}
	
	@Override
	public JPanel getView()
	{
		return object.getView();
	}
	
	@Override
	public void disposeView()
	{
		object.disposeView();
	}
	
	@Override
	public boolean fillsViewHorizontally()
	{
		return false;
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