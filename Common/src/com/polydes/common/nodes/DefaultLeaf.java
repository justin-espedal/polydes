package com.polydes.common.nodes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.ImageIcon;

public class DefaultLeaf implements Comparable<DefaultLeaf>, Leaf<DefaultLeaf,DefaultBranch>
{
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	protected DefaultBranch parent;
	
	protected String name;
	protected ImageIcon icon;
	protected boolean dirty;
	
	protected Object userData;
	
	public DefaultLeaf(String name, Object userData)
	{
		this.name = name;
		this.userData = userData;
	}
	
	public DefaultLeaf(String name)
	{
		this(name, null);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	@Override
	public void addListener(PropertyChangeListener l)
	{
		pcs.addPropertyChangeListener(l);
	}
	
	@Override
	public void addListener(String property, PropertyChangeListener l)
	{
		pcs.addPropertyChangeListener(property, l);
	}

	@Override
	public void removeListener(PropertyChangeListener l)
	{
		pcs.removePropertyChangeListener(l);
	}
	
	@Override
	public void removeListener(String property, PropertyChangeListener l)
	{
		pcs.removePropertyChangeListener(property, l);
	}
	
	@Override
	public DefaultBranch getParent()
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
			pcs.firePropertyChange(NAME, oldName, name);
			
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
		ImageIcon oldIcon = this.icon;
		this.icon = icon;
		pcs.firePropertyChange(ICON, oldIcon, icon);
	}
	
	@Override
	public ImageIcon getIcon()
	{
		return icon;
	}
	
	public void setUserData(Object userData)
	{
		this.userData = userData;
	}
	
	public Object getUserData()
	{
		return userData;
	}
	
	@Override
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void setDirty(boolean value)
	{
		if(dirty == value)
			return;
		
		dirty = value;
		pcs.firePropertyChange(DIRTY, !value, value);
		
		if(value && parent != null && !parent.isDirty())
			parent.setDirty(true);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public int compareTo(DefaultLeaf item)
	{
		if(item == null)
			return 0;
		
		return getName().compareTo(item.getName());
	}
}