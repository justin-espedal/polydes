package com.polydes.common.ui.darktree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;

public class TNode<T extends Leaf<T,U>, U extends Branch<T,U>> extends DefaultMutableTreeNode
{
	public TNode(T item)
	{
		super(item);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getUserObject()
	{
		return (T) super.getUserObject();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TNode<T,U> getNextSibling()
	{
		return (TNode<T,U>) super.getNextSibling();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TNode<T,U> getPreviousSibling()
	{
		return (TNode<T,U>) super.getPreviousSibling();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TNode<T,U> getParent()
	{
		return (TNode<T,U>) super.getParent();
	}
}
