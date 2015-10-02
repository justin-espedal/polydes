package com.polydes.common.ui.darktree;

import java.util.ArrayList;
import java.util.HashSet;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;

public class DTreeSelectionState<T extends Leaf<T>>
{
	public SelectionType type;
	public ArrayList<TNode<T>> nodes;
	public ArrayList<TNode<T>> nodesForTransfer;
	
	public DTreeSelectionState()
	{
		type = SelectionType.FOLDERS;
	}
	
	//called in transfer handler when needed
	public void prepareNodesForTransfer()
	{
		nodesForTransfer = new ArrayList<TNode<T>>();
		HashSet<Branch<T>> folders = new HashSet<Branch<T>>();
		for(TNode<T> node : nodes)
		{
			if(node.getUserObject() instanceof Branch)
				folders.add((Branch<T>) node.getUserObject());
		}
		
		for(TNode<T> node : nodes)
		{
			boolean ignored = false;
			Leaf<T> item = node.getUserObject();
			while((item = item.getParent()) != null)
			{
				if(folders.contains((Branch<T>) item))
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
