package com.polydes.common.ui.darktree;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;

public interface DTreeSelectionListener<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	void setSelectionState(DTreeSelectionState<T,U> state);
	void selectionStateChanged();
}