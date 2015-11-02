package com.polydes.common.ui.darktree;

import java.util.ArrayList;
import java.util.HashSet;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;

public class DTreeSelectionState<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public SelectionType type;
	public ArrayList<TNode<T,U>> nodes;
	public ArrayList<TNode<T,U>> nodesForTransfer;
	
	public DTreeSelectionState()
	{
		type = SelectionType.FOLDERS;
	}
	
	//called in transfer handler when needed
	@SuppressWarnings("unchecked")
	public void prepareNodesForTransfer()
	{
		nodesForTransfer = new ArrayList<TNode<T,U>>();
		HashSet<U> folders = new HashSet<U>();
		for(TNode<T,U> node : nodes)
		{
			if(node.getUserObject() instanceof Branch)
				folders.add((U) node.getUserObject());
		}
		
		for(TNode<T,U> node : nodes)
		{
			boolean ignored = false;
			T item = node.getUserObject();
			while((item = (T) item.getParent()) != null)
			{
				if(folders.contains((U) item))
				{
					ignored = true;
					break;
				}
			}
			
			if(!ignored)
				nodesForTransfer.add(node);
		}
	}
}
