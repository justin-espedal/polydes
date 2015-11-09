package com.polydes.common.nodes;

import static com.polydes.common.util.Lang.asArray;
import static com.polydes.common.util.Lang.hashset;
import static com.polydes.common.util.Lang.newarray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
		if(!nodes.add(t))
			return false;
		
		T oldLead = lead;
		lead = t;
		fireSelection(t, true, oldLead, lead);
		return true;
	}
	
	public boolean addAll(T[] newSelection)
	{
		List<T> added = new ArrayList<>();
		for(T t : newSelection)
			if(nodes.add(t))
				added.add(t);
		
		boolean changed = !added.isEmpty();
		if(!changed)
			return false;
		
		T oldLead = lead;
		lead = added.get(added.size() - 1);
		
		newSelection = asArray(added, leafClass);
		boolean[] areNew = new boolean[added.size()];
		Arrays.fill(areNew, true);
		
		fireSelection(newSelection, areNew, oldLead, lead);
		return true;
	}
	
	public boolean remove(T t)
	{
		if(!nodes.remove(t))
			return false;
		
		T oldLead = lead;
		if(!nodes.contains(lead))
			lead = lastNode();
		fireSelection(t, false, oldLead, lead);
		return true;
	}
	
	public boolean removeAll(T[] oldSelection)
	{
		List<T> removed = new ArrayList<>();
		for(T t : oldSelection)
			if(nodes.remove(t))
				removed.add(t);
		
		boolean changed = !removed.isEmpty();
		if(!changed)
			return false;
		
		T oldLead = lead;
		if(!nodes.contains(lead))
			lead = lastNode();
		
		oldSelection = asArray(removed, leafClass);
		boolean[] areNew = new boolean[removed.size()];
		Arrays.fill(areNew, false);
		
		fireSelection(oldSelection, areNew, oldLead, lead);
		return true;
	}
	
	public boolean set(T newSelection)
	{
		if(newSelection == null)
			return clear();
		
		if(nodes.isEmpty())
			return add(newSelection);
		
		boolean alreadySelected = nodes.contains(newSelection);
		int changeCount = nodes.size() + (alreadySelected ? -1 : 1);
		if(changeCount == 0)
			return false;
		
		T[] changedNodes = newarray(leafClass, changeCount);
		boolean[] areNew = new boolean[changeCount];
		int i = 0;
		for(T node : nodes)
		{
			if(node == newSelection)
				continue;
			changedNodes[i] = nodes.get(i);
			areNew[i] = false;
			++i;
		}
		if(!alreadySelected)
		{
			changedNodes[i] = newSelection;
			areNew[i] = true;
		}
		
		nodes.clear();
		nodes.add(newSelection);
		
		T oldLead = lead;
		if(!nodes.contains(lead))
			lead = lastNode();
		fireSelection(changedNodes, areNew, oldLead, lead);
		return true;
	}
	
	public boolean setAll(T[] newSelection)
	{
		if(nodes.isEmpty())
			return addAll(newSelection);
		
		HashSet<T> newSelSet = hashset(newSelection);
		List<T> additions = new ArrayList<>();
		List<T> removals = new ArrayList<>();
		
		for(T node : nodes)
			if(!newSelSet.contains(node))
				removals.add(node);
		for(T node : newSelection)
			if(!nodes.contains(node))
				additions.add(node);
		
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
		
		nodes.clear();
		nodes.addAll(newSelSet);
		
		T oldLead = lead;
		if(!nodes.contains(lead))
			lead = lastNode();
		fireSelection(changedNodes, areNew, oldLead, lead);
		return true;
	}
	
	public boolean change(T[] toAdd, T[] toRemove)
	{
		List<T> additions = new ArrayList<>();
		List<T> removals = new ArrayList<>();
		
		for(T node : toAdd)
			if(nodes.add(node))
				additions.add(node);
		for(T node : toRemove)
			if(nodes.remove(node))
				removals.add(node);
		
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
		if(!nodes.contains(lead))
			lead = lastNode();
		fireSelection(changedNodes, areNew, oldLead, lead);
		return true;
	}
	
	public boolean clear()
	{
		if(nodes.isEmpty())
			return false;
		
		T[] changedNodes = asArray(nodes, leafClass);
		boolean[] areNew = new boolean[changedNodes.length];
		Arrays.fill(areNew, false);
		nodes.clear();
		T oldLead = lead;
		lead = null;
		fireSelection(changedNodes, areNew, oldLead, lead);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void fireSelection(T change, boolean isNew, T oldLead, T newLead)
	{
		NodeSelectionEvent<T, U> e = new NodeSelectionEvent<>(this, change, isNew, oldLead, newLead);
		selectionEvents.fire().selectionChanged(e);
	}
	
	@SuppressWarnings("unchecked")
	private void fireSelection(T[] changes, boolean[] areNew, T oldLead, T newLead)
	{
		NodeSelectionEvent<T, U> e = new NodeSelectionEvent<>(this, changes, areNew, oldLead, newLead);
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
