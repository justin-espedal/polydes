package com.polydes.common.nodes;


public interface BranchListener<T extends Leaf<T>>
{
	public void branchLeafAdded(Branch<T> folder, Leaf<T> item, int position);
	public void branchLeafRemoved(Branch<T> folder, Leaf<T> item, int position);
}
