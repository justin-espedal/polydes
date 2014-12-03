package stencyl.ext.polydes.datastruct.data.folder;

public class DataItemUtil
{
	public static final void installListeners(DataItem item, DataItemListener l, FolderListener fl)
	{
		if(l != null)
			item.addListener(l);
		if(item instanceof Folder)
		{
			if(fl != null)
				((Folder) item).addFolderListener(fl);
			for(DataItem curItem : ((Folder) item).getItems())
			{
				installListeners(curItem, l, fl);
			}
		}
	}
	
	public static final void uninstallListeners(DataItem item, DataItemListener l, FolderListener fl)
	{
		if(l != null)
			item.removeListener(l);
		if(item instanceof Folder)
		{
			if(fl != null)
				((Folder) item).removeFolderListener(fl);
			for(DataItem curItem : ((Folder) item).getItems())
			{
				uninstallListeners(curItem, l, fl);
			}
		}
	}
	
	public static final void recursiveRun(DataItem item, DataItemRunnable runnable)
	{
		runnable.run(item);
		if(item instanceof Folder)
			for(DataItem curItem : ((Folder) item).getItems())
				recursiveRun(curItem, runnable);
	}
	
	public static interface DataItemRunnable
	{
		public void run(DataItem item);
	}
}
