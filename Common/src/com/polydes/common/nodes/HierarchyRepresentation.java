package com.polydes.common.nodes;


public interface HierarchyRepresentation<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public void leafStateChanged(T source);
	public void leafNameChanged(T source, String oldName);
	public void itemAdded(U folder, T item, int position);
	public void itemRemoved(U folder, T item, int oldPosition);
}
