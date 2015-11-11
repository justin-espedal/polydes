package com.polydes.common.nodes;

import java.util.List;

public abstract class DefaultNodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>> implements NodeCreator<T,U>
{
	@Override
	public void editNode(T toEdit)
	{
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