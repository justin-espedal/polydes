package com.polydes.common.ui.darktree;

import java.util.Collection;
import java.util.List;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.util.PopupUtil.PopupItem;

public interface DTreeNodeCreator<T extends Leaf<T>>
{
	void setSelectionState(DTreeSelectionState<T> selectionState);
	
	Collection<PopupItem> getCreatableNodeList();
	Leaf<T> createNode(PopupItem selected, String nodeName);
	void editNode(Leaf<T> dataItem);
	void nodeRemoved(Leaf<T> toRemove);
	boolean attemptRemove(List<Leaf<T>> toRemove);
}