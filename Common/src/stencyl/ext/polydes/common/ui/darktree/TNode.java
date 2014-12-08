package stencyl.ext.polydes.common.ui.darktree;

import javax.swing.tree.DefaultMutableTreeNode;

import stencyl.ext.polydes.common.nodes.Leaf;

public class TNode<T extends Leaf<T>> extends DefaultMutableTreeNode
{
	public TNode(Leaf<T> item)
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
	public TNode<T> getNextSibling()
	{
		return (TNode<T>) super.getNextSibling();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TNode<T> getPreviousSibling()
	{
		return (TNode<T>) super.getPreviousSibling();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TNode<T> getParent()
	{
		return (TNode<T>) super.getParent();
	}
}
