package com.polydes.common.nodes;

public interface NodeSelectionListener<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	void selectionChanged(NodeSelectionEvent<T,U> e);
}
