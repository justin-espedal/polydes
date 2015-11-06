package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.util.Lang;

public class NodeUtils
{
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void installListenersRecursive(T item, LeafListener<T,U> l, BranchListener<T,U> fl)
	{
		if(l != null)
			item.addListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((U) item).addFolderListener(fl);
			for(T curItem : ((U) item).getItems())
			{
				installListenersRecursive(curItem, l, fl);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void uninstallListenersRecursive(T item, LeafListener<T,U> l, BranchListener<T,U> fl)
	{
		if(l != null)
			item.removeListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((U) item).removeFolderListener(fl);
			for(T curItem : ((U) item).getItems())
			{
				uninstallListenersRecursive(curItem, l, fl);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void recursiveRun(T item, LeafRunnable<T,U> runnable)
	{
		runnable.run(item);
		if(item instanceof Branch)
			for(T curItem : ((U) item).getItems())
				recursiveRun(curItem, runnable);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void recursiveRunPost(T item, LeafRunnable<T,U> runnable)
	{
		if(item instanceof Branch)
			for(T curItem : ((U) item).getItems())
				recursiveRun(curItem, runnable);
		runnable.run(item);
	}
	
	public static interface LeafRunnable<T extends Leaf<T,U>, U extends Branch<T,U>>
	{
		public void run(T item);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> boolean isDescendantOf(T item, U parent)
	{
		while((item = (T) (item.getParent())) != null)
			if(item == parent)
				return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> int getDepth(T l)
	{
		int d = 0;
		while(l != null)
		{
			l = (T) l.getParent();
			++d;
		}
		return d;
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void removeNodesWithContainedParents(Collection<T> list)
	{
		HashSet<T> nodeSet = new HashSet<T>(list);
		HashSet<T> toRemove = new HashSet<T>();
		
		nodeIter:
		for(T node : list)
		{
			U parent = node.getParent();
			while(parent != null)
			{
				if(nodeSet.contains(parent))
				{
					toRemove.add(node);
					continue nodeIter;
				}
				parent = parent.getParent();
			}
		}
		
		for(T node : toRemove)
			list.remove(node);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void includeDescendants(Collection<T> list)
	{
		final HashSet<T> set = new HashSet<T>();
		for(T node : list)
			NodeUtils.recursiveRun(node, (T item) -> {
				if(!set.contains(item))
				{
					set.add(item);
					list.add(item);
				}
			});
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void depthSort(List<T> list)
	{
		Collections.sort(list, (a, b) -> Integer.compare(getDepth(b), getDepth(a)));
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> Object[] getPath(U parent, T child)
	{
		ArrayList<T> lookup = new ArrayList<>();
		lookup.add(child);
//		System.out.println(parent + ", " + child);
		System.out.println("Path lookup: " + parent.getName() + " --> " + child.getName());
		while(child != parent)
			lookup.add(child = (T) child.getParent());
		Collections.reverse(lookup);
		
		System.out.println("   Result: " + StringUtils.join(Lang.mapCA(lookup, String.class, i -> i.getName()), " -> "));
		
		return lookup.toArray();
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> int getIndex(T child)
	{
		return child.getParent().getItems().indexOf(child);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> T getNextSibling(T child)
	{
		U parent = child.getParent();
		int i = parent.getItems().indexOf(child);
		if(i == parent.getItems().size() - 1)
			return null;
		
		return parent.getItemAt(i + 1);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> T getPreviousSibling(T child)
	{
		U parent = child.getParent();
		int i = parent.getItems().indexOf(child);
		if(i == 0)
			return null;
		
		return parent.getItemAt(i - 1);
	}
}
