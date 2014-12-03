package stencyl.ext.polydes.datastruct.ui.tree;

import java.util.List;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;

public abstract class DefaultNodeCreator implements DTreeNodeCreator
{
	@Override
	public abstract Object createNode(PopupItem selected, String nodeName);
	
	@Override
	public void editNode(DataItem toEdit)
	{
	}
	
	@Override
	public abstract void nodeRemoved(DataItem toRemove);
	
	protected DTreeSelectionState selectionState;
	
	@Override
	public void setSelectionState(DTreeSelectionState selectionState)
	{
		this.selectionState = selectionState;
	}
	
	@Override
	public boolean attemptRemove(List<DataItem> toRemove)
	{
		return true;
	}
	
	//TODO: Do we need complete uniqueness of item names?
	/*
	@Override
	public boolean canCreate(String name, Folder folder)
	{
		return
			(folder.getItemByName(name) == null) &&
			(uniqueItemNames ? !itemNames.contains(name) : true);
	}
	*/
}