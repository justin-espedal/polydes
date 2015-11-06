package com.polydes.common.ui.darktree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeUtils;

public class DTreeModel<T extends Leaf<T,U>, U extends Branch<T,U>> implements TreeModel, HierarchyRepresentation<T, U>
{
	final EventListenerSupport<TreeModelListener> treeListeners;
	final HierarchyModel<T, U> model;
	final U root;
	
	public DTreeModel(HierarchyModel<T, U> model)
	{
		this.model = model;
		this.root = model.getRootBranch();
		treeListeners = new EventListenerSupport<>(TreeModelListener.class);
		model.addRepresentation(this);
	}
	
	public void dispose()
	{
		model.removeRepresentation(this);
	}
	
	@Override
	public U getRoot()
	{
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getChild(Object parent, int index)
	{
		return ((U) parent).getItemAt(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildCount(Object parent)
	{
		return ((U) parent).getItems().size();
	}

	@Override
	public boolean isLeaf(Object node)
	{
		return !(node instanceof Branch);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		
	}
	
	@SuppressWarnings("unchecked")
	public TreePath getPath(U u)
	{
		if(u == root)
			return new TreePath(root);
		else
			return new TreePath(NodeUtils.getPath(root, (T) u));
	}
	
	public TreePath getPath(T t)
	{
		return new TreePath(NodeUtils.getPath(root, t));
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((U) parent).getItems().indexOf(child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		treeListeners.addListener(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		treeListeners.removeListener(l);
	}

	@Override
	public void leafStateChanged(T source)
	{
		treeListeners.fire().treeNodesChanged(generateLeafModifyEvent(source));
	}

	@Override
	public void leafNameChanged(T source, String oldName)
	{
		treeListeners.fire().treeNodesChanged(generateLeafModifyEvent(source));
	}

	@Override
	public void itemAdded(U folder, T item, int position)
	{
		treeListeners.fire().treeNodesInserted(generateLeafAddRemoveEvent(folder, item, position));
	}

	@Override
	public void itemRemoved(U folder, T item, int oldPosition)
	{
		treeListeners.fire().treeNodesRemoved(generateLeafAddRemoveEvent(folder, item, oldPosition));
	}
	
	@SuppressWarnings("unchecked")
	private TreeModelEvent generateLeafModifyEvent(T source)
	{
		if(source == root)
			return new TreeModelEvent(this, (Object[]) null, (int[]) null, new Object[] {root});
		
		int[] childIndices = new int[] {NodeUtils.getIndex(source)};
		Object[] children = new Object[] {source};
		Object[] path = NodeUtils.getPath(root, (T) source.getParent());
		
		return new TreeModelEvent(this, path, childIndices, children);
	}
	
	@SuppressWarnings("unchecked")
	private TreeModelEvent generateLeafAddRemoveEvent(U folder, T source, int pos)
	{
		int[] childIndices = new int[] {pos};
		Object[] children = new Object[] {source};
		Object[] path = NodeUtils.getPath(root, (T) folder);
		
		return new TreeModelEvent(this, path, childIndices, children);
	}
}
