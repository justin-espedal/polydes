package com.polydes.common.nodes;


public interface HierarchyRepresentation<T extends Leaf<T>>
{
	public void leafStateChanged(Leaf<T> source);
	public void leafNameChanged(Leaf<T> source, String oldName);
	public void itemAdded(Branch<T> folder, Leaf<T> item, int position);
	public void itemRemoved(Branch<T> folder, Leaf<T> item, int oldPosition);
}
