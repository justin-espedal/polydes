package stencyl.ext.polydes.common.ui.darktree;

import stencyl.ext.polydes.common.nodes.Leaf;

public interface DTreeSelectionListener<T extends Leaf<T>>
{
	void setSelectionState(DTreeSelectionState<T> state);
	void selectionStateChanged();
}