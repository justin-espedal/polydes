package com.polydes.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeUtils;

public class SortedNodeCollection<T extends Leaf<T,U>, U extends Branch<T,U>> implements Collection<T>
{
	private List<T> list;
	private U root;
	
	private NodeOrderComparator comparator;
	
	public SortedNodeCollection(U root)
	{
		this.root = root;
		
		list = new ArrayList<>();
		comparator = new NodeOrderComparator();
	}
	
	public T get(int i)
	{
		return list.get(i);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> copyList()
	{
		return (ArrayList<T>) ((ArrayList<T>) list).clone();
	}
	
	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o)
	{
		return binarySearch((T) o) >= 0;
	}

	@Override
	public Iterator<T> iterator()
	{
		return list.iterator();
	}
	
	@Override
	public Object[] toArray()
	{
		return list.toArray();
	}

	@Override
	public <S> S[] toArray(S[] a)
	{
		return list.toArray(a);
	}
	
	@Override
	public boolean add(T e)
	{
		int i = binarySearch(e);
		if(i >= 0)
			return false;
		
		list.add(-(i + 1), e);
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o)
	{
		int i = binarySearch((T) o);
		if(i < 0)
			return false;
		
		list.remove(i);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsAll(Collection<?> c)
	{
		for(Object o : c)
			if(binarySearch((T) o) < 0)
				return false;
		
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean changed = false;
		
		for(T t : c)
			changed = add(t) || changed;
		
		return changed;
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean changed = false;
		for(Object o : c)
			changed = remove(o) || changed;
		
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		list.clear();
	}
	
	private int binarySearch(T node)
	{
		return Collections.binarySearch(list, node, comparator);
	}
	
	public class NodeOrderComparator implements Comparator<T>
	{
		@Override
		public int compare(T o1, T o2)
		{
			int[] path1 = NodeUtils.getIndexPath(root, o1);
			int[] path2 = NodeUtils.getIndexPath(root, o2);
			
			for(int i = 0, n = Math.min(path1.length, path2.length); i < n; ++i)
				if(path1[i] != path2[i])
					return Integer.compare(path1[i], path2[i]);
			
			return Integer.compare(path1.length, path2.length);
		}
	}
}