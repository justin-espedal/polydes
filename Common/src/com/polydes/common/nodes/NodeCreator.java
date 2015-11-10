package com.polydes.common.nodes;

import java.util.Collection;
import java.util.List;

import com.polydes.common.util.PopupUtil.PopupItem;

public interface NodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	Collection<PopupItem> getCreatableNodeList();
	T createNode(PopupItem selected, String nodeName);
	void editNode(T dataItem);
	void nodeRemoved(T toRemove);
	boolean attemptRemove(List<T> toRemove);
}
