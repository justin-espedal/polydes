package stencyl.ext.polydes.extrasmanager.app.darktree;

import java.util.Collection;
import java.util.List;

import stencyl.ext.polydes.extrasmanager.app.utils.PopupUtil.PopupItem;
import stencyl.ext.polydes.extrasmanager.data.folder.Leaf;

public interface DTreeNodeCreator<T extends Leaf<T>>
{
	void setSelectionState(DTreeSelectionState<T> selectionState);
	
	Collection<PopupItem> getCreatableNodeList();
	Leaf<T> createNode(PopupItem selected, String nodeName);
	void editNode(Leaf<T> dataItem);
	void nodeRemoved(Leaf<T> toRemove);
	boolean attemptRemove(List<Leaf<T>> toRemove);
}