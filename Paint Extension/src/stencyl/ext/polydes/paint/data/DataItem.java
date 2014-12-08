package stencyl.ext.polydes.paint.data;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import stencyl.ext.polydes.common.nodes.Branch;
import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.common.nodes.LeafListener;

public class DataItem implements Comparable<DataItem>, Leaf<DataItem>
{
	protected ArrayList<LeafListener<DataItem>> listeners;
	protected Branch<DataItem> parent;
	protected String name;
	protected Object contents;
	protected boolean modified;
	
	public DataItem(String name)
	{
		listeners = new ArrayList<LeafListener<DataItem>>();
		parent = null;
		this.name = name;
		contents = null;
		modified = false;
	}
	
	@Override
	public void addListener(LeafListener<DataItem> l)
	{
		listeners.add(l);
	}
	
	@Override
	public void removeListener(LeafListener<DataItem> l)
	{
		listeners.remove(l);
	}
	
	@Override
	public void setParent(Branch<DataItem> parent, boolean addToParent)
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
	public Branch<DataItem> getParent()
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
			for(LeafListener<DataItem> l : listeners) {l.leafNameChanged(this, oldName);}
			
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
		for(LeafListener<DataItem> l : listeners) {l.leafStateChanged(this);}
		
		if(modified && parent != null && !parent.isDirty())
			((Folder) parent).setDirty();
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