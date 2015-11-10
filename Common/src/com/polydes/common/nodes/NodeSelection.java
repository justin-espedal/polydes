package com.polydes.common.nodes;

import static com.polydes.common.util.Lang.array;
import static com.polydes.common.util.Lang.asArray;
import static com.polydes.common.util.Lang.newarray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.common.collections.SortedNodeCollection;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.common.util.Lang;

public class NodeSelection<T extends Leaf<T,U>, U extends Branch<T,U>> implements Iterable<T>
{
	@SuppressWarnings("rawtypes")
	private EventListenerSupport<NodeSelectionListener> selectionEvents;
	
	private Class<T> leafClass;
	
	private U root;
	private SelectionType type;
	private SortedNodeCollection<T,U> nodes;
	
	private T lead;

	private ArrayList<T> nodesForTransfer;
	
	public NodeSelection(HierarchyModel<T, U> model)
	{
		this.root = model.getRootBranch();
		nodes = new SortedNodeCollection<T, U>(root);
		type = SelectionType.FOLDERS;
		leafClass = model.leafClass;
		
		selectionEvents = new EventListenerSupport<>(NodeSelectionListener.class, NodeSelectionListener.class.getClassLoader());
	}
	
	public T firstNode()
	{
		if(nodes.isEmpty())
			return null;
		
		return nodes.get(0);
	}
	
	public T lastNode()
	{
		if(nodes.isEmpty())
			return null;
		
		return nodes.get(nodes.size() - 1);
	}
	
	public T lead()
	{
		return lead;
	}
	
	public TreePath treePath(T node)
	{
		if(node == null)
			return null;
		
		return new TreePath(NodeUtils.getPath(root, node));
	}

	public TreePath[] treePaths(T[] nodes)
	{
		return Lang.map(nodes, TreePath.class, node -> new TreePath(NodeUtils.getPath(root, node)));
	}
	
	public T get(int i)
	{
		return nodes.get(i);
	}
	
	public ArrayList<T> copyList()
	{
		return nodes.copyList();
	}
	
	public int size()
	{
		return nodes.size();
	}

	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}

	public boolean contains(T t)
	{
		return nodes.contains(t);
	}

	@Override
	public Iterator<T> iterator()
	{
		return nodes.iterator();
	}
	
	public boolean add(T t)
	{
		return change(array(t), null);
	}
	
	public boolean addAll(T[] newSelection)
	{
		return change(newSelection, null);
	}
	
	public boolean remove(T t)
	{
		return change(null, array(t));
	}
	
	public boolean removeAll(T[] oldSelection)
	{
		return change(null, oldSelection);
	}
	
	public boolean set(T newSelection)
	{
		if(newSelection == null)
			return clear();
		
		nodes.remove(newSelection);
		
		return change(array(newSelection), asArray(nodes, leafClass));
	}
	
	public boolean setAll(T[] newSelection)
	{
		for(T node : newSelection)
			nodes.remove(node);
		
		return change(newSelection, asArray(nodes, leafClass));
	}
	
	public boolean change(T[] toAdd, T[] toRemove)
	{
		List<T> additions = new ArrayList<>();
		List<T> removals = new ArrayList<>();
		
		if(toAdd != null)
			for(T node : toAdd)
				if(nodes.add(node))
					additions.add(node);
		if(toRemove != null)
			for(T node : toRemove)
				if(nodes.remove(node))
					removals.add(node);
		
		updateSelection(additions, removals);
		return _change(additions, removals);
	}
	
	public boolean clear()
	{
		if(nodes.isEmpty())
			return false;
		
		List<T> additions = new ArrayList<>();
		List<T> removals = copyList();
		nodes.clear();
		
		updateSelection(additions, removals);
		return _change(additions, removals);
	}
	
	private boolean _change(List<T> additions, List<T> removals)
	{
		int changeCount = additions.size() + removals.size();
		if(changeCount == 0)
			return false;
		
		T[] changedNodes = newarray(leafClass, changeCount);
		boolean[] areNew = new boolean[changeCount];
		int i = 0;
		for(T node : additions)
		{
			changedNodes[i] = node;
			areNew[i] = true;
			++i;
		}
		for(T node : removals)
		{
			changedNodes[i] = node;
			areNew[i] = false;
			++i;
		}
		
		T oldLead = lead;
		if(lead == null || !nodes.contains(lead))
			lead = lastNode();
		fireSelection(changedNodes, areNew, oldLead, lead);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void updateSelection(List<T> additions, List<T> removals)
	{
		if(nodes.isEmpty())
		{
			nodes.add((T) root);
			if(!additions.contains(root))
				additions.add((T) root);
			if(removals.contains(root))
				removals.remove(root);
		}
		
		int folderCounter = 0;
		int itemCounter = 0;
		
		for(T node : nodes)
		{
			if(node instanceof Branch)
				++folderCounter;
			else
				++itemCounter;
		}
		
		if(folderCounter > 0 && itemCounter > 0)
			type = SelectionType.MIX;
		else if(folderCounter > 0)
			type = SelectionType.FOLDERS;
		else
			type = SelectionType.ITEMS;
	}
	
	@SuppressWarnings("unchecked")
	private void fireSelection(T[] changes, boolean[] areNew, T oldLead, T newLead)
	{
		NodeSelectionEvent<T, U> e = changes.length > 1 ?
				new NodeSelectionEvent<>(this, changes, areNew, oldLead, newLead) :
				new NodeSelectionEvent<>(this, changes[0], areNew[0], oldLead, newLead);
		selectionEvents.fire().selectionChanged(e);
	}
	
	public SelectionType getType()
	{
		return type;
	}
	
	//called in transfer handler when needed
	public void prepareNodesForTransfer()
	{
		nodesForTransfer = new ArrayList<>();
		nodesForTransfer.addAll(nodes);
		NodeUtils.removeNodesWithContainedParents(nodesForTransfer);
	}

	public void addSelectionListener(NodeSelectionListener<T, U> l)
	{
		selectionEvents.addListener(l);
	}
	
	public void removeSelectionListener(NodeSelectionListener<T, U> l)
	{
		selectionEvents.removeListener(l);
	}

	public ArrayList<T> getNodesForTransfer()
	{
		return nodesForTransfer;
	}
}
