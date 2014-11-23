package stencyl.ext.polydes.datastruct.ui.tree;

import java.util.Collection;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;

public interface DTreeNodeCreator
{
	void setSelectionState(DTreeSelectionState selectionState);
	
	Collection<PopupItem> getCreatableNodeList();
	Object createNode(PopupItem selected, String nodeName);
	void editNode(DataItem dataItem);
	void nodeRemoved(DataItem toRemove);
}