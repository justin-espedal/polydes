package com.polydes.common.nodes;


public interface BranchListener<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public void branchLeafAdded(U folder, T item, int position);
	public void branchLeafRemoved(U folder, T item, int position);
}
