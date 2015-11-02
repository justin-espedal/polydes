package com.polydes.common.nodes;

public class NodeUtils
{
	public static final <T extends Leaf<T>> void installListenersRecursive(Leaf<T> item, LeafListener<T> l, BranchListener<T> fl)
	{
		if(l != null)
			item.addListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((Branch<T>) item).addFolderListener(fl);
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
			{
				installListenersRecursive(curItem, l, fl);
			}
		}
	}
	
	public static final <T extends Leaf<T>> void uninstallListenersRecursive(Leaf<T> item, LeafListener<T> l, BranchListener<T> fl)
	{
		if(l != null)
			item.removeListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((Branch<T>) item).removeFolderListener(fl);
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
			{
				uninstallListenersRecursive(curItem, l, fl);
			}
		}
	}
	
	public static final <T extends Leaf<T>> void recursiveRun(Leaf<T> item, LeafRunnable<T> runnable)
	{
		runnable.run(item);
		if(item instanceof Branch)
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
				recursiveRun(curItem, runnable);
	}
	
	public static final <T extends Leaf<T>> void recursiveRunPost(Leaf<T> item, LeafRunnable<T> runnable)
	{
		if(item instanceof Branch)
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
				recursiveRun(curItem, runnable);
		runnable.run(item);
	}
	
	public static interface LeafRunnable<T extends Leaf<T>>
	{
		public void run(Leaf<T> item);
	}
	
	public static final <T extends Leaf<T>> boolean isDescendantOf(Leaf<T> item, Branch<T> parent)
	{
		while((item = item.getParent()) != null)
			if(item == parent)
				return true;
		
		return false;
	}
}
