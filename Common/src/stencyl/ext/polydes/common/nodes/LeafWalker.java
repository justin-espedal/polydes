package stencyl.ext.polydes.common.nodes;

public class LeafWalker
{
	public static final <T extends Leaf<T>> void installListeners(Leaf<T> item, LeafListener<T> l, BranchListener<T> fl)
	{
		if(l != null)
			item.addListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((Branch<T>) item).addFolderListener(fl);
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
			{
				installListeners(curItem, l, fl);
			}
		}
	}
	
	public static final <T extends Leaf<T>> void uninstallListeners(Leaf<T> item, LeafListener<T> l, BranchListener<T> fl)
	{
		if(l != null)
			item.removeListener(l);
		if(item instanceof Branch)
		{
			if(fl != null)
				((Branch<T>) item).removeFolderListener(fl);
			for(Leaf<T> curItem : ((Branch<T>) item).getItems())
			{
				uninstallListeners(curItem, l, fl);
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
	
	public static interface LeafRunnable<T extends Leaf<T>>
	{
		public void run(Leaf<T> item);
	}
}
