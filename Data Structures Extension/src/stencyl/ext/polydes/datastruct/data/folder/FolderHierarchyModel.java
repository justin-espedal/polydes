package stencyl.ext.polydes.datastruct.data.folder;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;

import stencyl.ext.polydes.datastruct.data.folder.DataItemUtil.DataItemRunnable;

/**
 * Folder that can be represented as the root of a hierarchical file system.
 * Changes to files and folders within this root's structure are reflected in
 * representative models.
 */
public class FolderHierarchyModel implements DataItemListener, FolderListener
{
	private Folder rootFolder;
	private FolderHierarchyRepresentation[] reps;
	
	private HashSet<String> itemNames;
	private boolean simpleMove;
	private boolean uniqueItemNames;
	
	public FolderHierarchyModel(Folder rootFolder)
	{
		this.rootFolder = rootFolder;
		DataItemUtil.installListeners(rootFolder, this, this);
		
		itemNames = new HashSet<String>();
		simpleMove = false;
		uniqueItemNames = false;
		
		reps = new FolderHierarchyRepresentation[0];
		
		DataItemUtil.recursiveRun(rootFolder, new DataItemRunnable()
		{
			@Override
			public void run(DataItem item)
			{
				if(!(item instanceof Folder))
					itemNames.add(item.getName());
			}
		});
	}
	
	public void dispose()
	{
		DataItemUtil.uninstallListeners(rootFolder, this, this);
		itemNames.clear();
		rootFolder = null;
	}
	
	public ArrayList<DataItem> getPath(DataItem item)
	{
		ArrayList<DataItem> list = new ArrayList<DataItem>();
		while(item != null)
		{
			list.add(0, item);
			if(item == rootFolder)
				return list;
			
			item = item.getParent();
		}
		
		return null;
	}
	
	public boolean isUniqueItemNames()
	{
		return uniqueItemNames;
	}

	public void setUniqueItemNames(boolean uniqueItemNames)
	{
		this.uniqueItemNames = uniqueItemNames;
	}

	public Folder getRootFolder()
	{
		return rootFolder;
	}
	
	/*================================================*\
	 | Folder/DataItem Listening
	\*================================================*/
	
	@Override
	public void folderItemAdded(Folder folder, DataItem item)
	{
//		System.out.println("Folder Item Added: " + folder + ", " + item);
		if(!simpleMove)
			DataItemUtil.installListeners(item, this, this);
		
		modelAddItem(folder, item, folder.getItems().indexOf(item));
	}

	@Override
	public void folderItemRemoved(Folder folder, DataItem item)
	{
//		System.out.println("Folder Item Removed: " + folder + ", " + item);
		if(!simpleMove)
			DataItemUtil.uninstallListeners(item, this, this);
		
		modelRemoveItem(item);
	}
	
	@Override
	public void folderItemMoved(Folder folder, DataItem source, int oldPosition)
	{
//		System.out.println("Folder Item Moved: " + folder + ", " + source + ", " + oldPosition);
		
		modelRemoveItem(source);
		modelAddItem(folder, source, folder.getItems().indexOf(source));
	}

	@Override
	public void dataItemStateChanged(DataItem source)
	{
//		System.out.println("Data Item State Changed: " + source);
		
		for(FolderHierarchyRepresentation rep : reps)
			rep.dataItemStateChanged(source);
	}

	@Override
	public void dataItemNameChanged(DataItem source, String oldName)
	{
//		System.out.println("Data Item Name Changed: " + source + ", " + oldName);
		
		if(!(source instanceof Folder))
		{
			itemNames.remove(oldName);
			itemNames.add(source.getName());
		}
		
		for(FolderHierarchyRepresentation rep : reps)
			rep.dataItemNameChanged(source, oldName);
	}
	
	/*================================================*\
	 | Hierarchy Modification API
	\*================================================*/
	
	public boolean canMoveItem(DataItem item, Folder target)
	{
		//check against moving some folder into itself
		Folder parent = target;
		do
		{
			if(parent == item)
				return false;
		}
		while((parent = parent.getParent()) != null);
		
		//already in this folder, just moving it.
		if(target == item.getParent())
			return true;
		
		return target.canAcceptItem(item);
	}
	
	public void massMove(DataItem[] transferItems, Folder target, int position)
	{
		simpleMove = true;
		for(DataItem item : transferItems)
			item.getParent().removeItem(item);
		for(DataItem item : transferItems)
			target.addItem(item, position++);
		simpleMove = false;
	}
	
	//This is currently never called.
	public void moveItem(DataItem item, Folder target, int position)
	{
		simpleMove = true;
		if(item.getParent() == target)
			target.moveItem(item, position);
		else
		{
			item.getParent().removeItem(item);
			target.addItem(item, position);
		}
		simpleMove = false;
	}
	
	public void copyItem(DataItem item, Folder target, int position)
	{
		
	}
	
	public void addItem(DataItem item, Folder target, int position)
	{
		target.addItem(item, position);
		if(!(item instanceof Folder))
			itemNames.add(item.getName());
	}
	
	public void removeItem(DataItem item, Folder target)
	{
		target.removeItem(item);
		if(!(item instanceof Folder))
			itemNames.remove(item.getName());
	}
	
	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	public void addRepresentation(FolderHierarchyRepresentation rep)
	{
		reps = ArrayUtils.add(reps, rep);
	}
	
	public void removeRepresentation(FolderHierarchyRepresentation rep)
	{
		reps = ArrayUtils.removeElement(reps, rep);
	}
	
	public boolean isMovingItem()
	{
		return simpleMove;
	}
	
	private void modelAddItem(Folder folder, DataItem item, int position)
	{
		for(FolderHierarchyRepresentation rep : reps)
			rep.itemAdded(folder, item, position);
	}
	
	private void modelRemoveItem(DataItem item)
	{
		for(FolderHierarchyRepresentation rep : reps)
			rep.itemRemoved(item);
	}
}
