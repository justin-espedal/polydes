package com.polydes.common.nodes;

import java.util.List;

import com.polydes.common.util.PopupUtil.PopupItem;

public abstract class DefaultNodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>> implements NodeCreator<T,U>
{
	@Override
	public abstract T createNode(PopupItem selected, String nodeName);
	
	@Override
	public void editNode(T toEdit)
	{
	}
	
	@Override
	public abstract void nodeRemoved(T toRemove);
	
	protected NodeSelection<T,U> selection;
	
	@Override
	public void setSelection(NodeSelection<T,U> selection)
	{
		this.selection = selection;
	}
	
	@Override
	public boolean attemptRemove(List<T> toRemove)
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