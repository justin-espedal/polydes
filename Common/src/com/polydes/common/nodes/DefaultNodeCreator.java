package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultNodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>> implements NodeCreator<T,U>
{
	@Override
	public T createNode(CreatableNodeInfo selected, String nodeName)
	{
		return null;
	}
	
	@Override
	public void editNode(T toEdit)
	{
	}
	
	@Override
	public ArrayList<CreatableNodeInfo> getCreatableNodeList(U creationBranch)
	{
		return null;
	}
	
	@Override
	public ArrayList<NodeAction<T>> getNodeActions(T[] targets)
	{
		return null;
	}
	
	@Override
	public boolean attemptRemove(List<T> toRemove)
	{
		return false;
	}
	
	@Override
	public void nodeRemoved(T toRemove)
	{
		
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