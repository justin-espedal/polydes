package com.polydes.common.nodes;

public interface NodeSelectionListener<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	void setSelectionState(NodeSelection<T,U> state);
	void selectionStateChanged();
}
