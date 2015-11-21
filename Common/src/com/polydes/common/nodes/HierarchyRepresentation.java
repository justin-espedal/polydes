package com.polydes.common.nodes;

import java.beans.PropertyChangeListener;

public interface HierarchyRepresentation<T extends Leaf<T,U>, U extends Branch<T,U>> extends PropertyChangeListener
{
	public void itemAdded(U folder, T item, int position);
	public void itemRemoved(U folder, T item, int oldPosition);
}