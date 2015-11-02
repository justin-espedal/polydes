package com.polydes.common.ui.darktree;

import java.util.Collection;
import java.util.List;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.util.PopupUtil.PopupItem;

public interface DTreeNodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	void setSelectionState(DTreeSelectionState<T,U> selectionState);
	
	Collection<PopupItem> getCreatableNodeList();
	T createNode(PopupItem selected, String nodeName);
	void editNode(T dataItem);
	void nodeRemoved(T toRemove);
	boolean attemptRemove(List<T> toRemove);
}