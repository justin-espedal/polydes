package stencyl.ext.polydes.extrasmanager.app.darktree;

import stencyl.ext.polydes.extrasmanager.data.folder.Leaf;

public interface DTreeSelectionListener<T extends Leaf<T>>
{
	void setSelectionState(DTreeSelectionState<T> state);
	void selectionStateChanged();
}