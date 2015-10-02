package com.polydes.common.ui.darktree;

import com.polydes.common.nodes.Leaf;

public interface DTreeSelectionListener<T extends Leaf<T>>
{
	void setSelectionState(DTreeSelectionState<T> state);
	void selectionStateChanged();
}