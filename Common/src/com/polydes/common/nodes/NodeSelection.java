package com.polydes.common.nodes;

import java.util.ArrayList;

import com.polydes.common.ui.darktree.SelectionType;

public class NodeSelection<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public SelectionType type;
	public ArrayList<T> nodes;
	public ArrayList<T> nodesForTransfer;
	
	public NodeSelection()
	{
		type = SelectionType.FOLDERS;
	}
	
	public T lastNode()
	{
		return nodes.get(nodes.size() - 1);
	}
	
	//called in transfer handler when needed
	public void prepareNodesForTransfer()
	{
		nodesForTransfer = new ArrayList<>();
		nodesForTransfer.addAll(nodes);
		NodeUtils.removeNodesWithContainedParents(nodesForTransfer);
	}
}
