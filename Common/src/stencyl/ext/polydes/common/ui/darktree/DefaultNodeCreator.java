package stencyl.ext.polydes.common.ui.darktree;

import java.util.List;

import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.common.util.PopupUtil.PopupItem;

public abstract class DefaultNodeCreator<T extends Leaf<T>> implements DTreeNodeCreator<T>
{
	@Override
	public abstract Leaf<T> createNode(PopupItem selected, String nodeName);
	
	@Override
	public void editNode(Leaf<T> toEdit)
	{
	}
	
	@Override
	public abstract void nodeRemoved(Leaf<T> toRemove);
	
	protected DTreeSelectionState<T> selectionState;
	
	@Override
	public void setSelectionState(DTreeSelectionState<T> selectionState)
	{
		this.selectionState = selectionState;
	}
	
	@Override
	public boolean attemptRemove(List<Leaf<T>> toRemove)
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