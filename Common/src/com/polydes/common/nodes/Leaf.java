package com.polydes.common.nodes;

import javax.swing.ImageIcon;

public interface Leaf<T extends Leaf<T>>
{
	public void setParent(Branch<T> branch, boolean addToParent);
	public Branch<T> getParent();
	public void addListener(LeafListener<T> l);
	public void removeListener(LeafListener<T> l);
	public void setName(String newName);
	public String getName();
	public boolean canEditName();
	public ImageIcon getIcon();
	public boolean isDirty();
}