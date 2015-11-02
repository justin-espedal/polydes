package com.polydes.common.nodes;

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
}
