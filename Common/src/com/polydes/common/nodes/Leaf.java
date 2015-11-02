package com.polydes.common.nodes;

import javax.swing.ImageIcon;

public interface Leaf<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public void setParent(U branch, boolean addToParent);
	public U getParent();
	public void addListener(LeafListener<T,U> l);
	public void removeListener(LeafListener<T,U> l);
	public void setName(String newName);
	public String getName();
	public boolean canEditName();
	public ImageIcon getIcon();
	public boolean isDirty();
}