package com.polydes.common.nodes;

import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;

public interface Leaf<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public static final String NAME = "name";
	public static final String ICON = "icon";
	public static final String DIRTY = "dirty";
	public static final String STATE = "state";
	
	public U getParent();
	
	public void addListener(PropertyChangeListener l);
	public void removeListener(PropertyChangeListener l);
	public void addListener(String property, PropertyChangeListener l);
	public void removeListener(String property, PropertyChangeListener l);
	
	public void setName(String name);
	public String getName();
	public boolean canEditName();
	
	public ImageIcon getIcon();
	public boolean isDirty();
}