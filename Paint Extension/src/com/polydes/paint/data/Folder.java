package com.polydes.paint.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.BranchListener;
import com.polydes.common.nodes.Leaf;

public class Folder extends DataItem implements Branch<DataItem>
{
	protected ArrayList<BranchListener<DataItem>> fListeners;
	private ArrayList<Leaf<DataItem>> items;
	private HashSet<String> itemNames;
	
	public Folder(String name)
	{
		super(name);
		fListeners = new ArrayList<BranchListener<DataItem>>();
		contents = items = new ArrayList<Leaf<DataItem>>();
		itemNames = new HashSet<String>();
	}
	
	@Override
	public void addFolderListener(BranchListener<DataItem> l)
	{
		fListeners.add(l);
	}
	
	@Override
	public void removeFolderListener(BranchListener<DataItem> l)
	{
		fListeners.remove(l);
	}
	
	@Override
	public void addItem(Leaf<DataItem> item)
	{
		addItem(item, items.size());
	}
	
	@Override
	public void addItem(Leaf<DataItem> item, int position)
	{
		if(itemNames.contains(item.getName()) || position < 0 || position > items.size())
			return;
		
		items.add(position, (DataItem) item);
		itemNames.add(item.getName());
		if(item.getParent() != this)
			item.setParent(this, false);
		for(BranchListener<DataItem> l : fListeners) {l.branchLeafAdded(this, item, position);}
		
		setDirty();
	}
	
	
	@Override
	public List<Leaf<DataItem>> getItems()
	{
		return items;
	}
	
	@Override
	public Leaf<DataItem> getItemByName(String name)
	{
		for(Leaf<DataItem> item : items)
		{
			if(item.getName().equals(name))
				return item;
		}
		
		return null;
	}
	
	@Override
	public Leaf<DataItem> getItemAt(int position)
	{
		if(position < 0 || position >= items.size())
			return null;
		
		return items.get(position);
	}
	
	@Override
	public void removeItem(Leaf<DataItem> item)
	{
		if(itemNames.contains(item.getName()))
		{
			int pos = items.indexOf(item);
			items.remove(item);
			itemNames.remove(item.getName());
			for(BranchListener<DataItem> l : fListeners) {l.branchLeafRemoved(this, item, pos);}
			
			setDirty();
		}
	}
//	
//	public void moveItem(DataItem item, int position)
//	{
//		int curPos = items.indexOf(item);
//		if(curPos < position)
//			--position;
//		if(curPos == position)
//			return;
//		
//		items.remove(item);
//		items.add(position, item);
//		for(FolderListener l : fListeners) {l.folderItemMoved(this, item, curPos);}
//		
//		setDirty();
//	}
	
	public void unload()
	{
		for(Leaf<DataItem> item : items)
		{
			if(item instanceof Folder)
			{
				((Folder) item).unload();
			}
		}
		items = new ArrayList<Leaf<DataItem>>();
		itemNames = new HashSet<String>();
		super.setClean();
	}
	
	@Override
	public void setClean()
	{
		super.setClean();
		
		for(Leaf<DataItem> item : items)
		{
			if(item.isDirty())
				((DataItem) item).setClean();
		}
	}

	@Override
	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}

	@Override
	public boolean canAcceptItem(Leaf<DataItem> item)
	{
		return getItemByName(item.getName()) == null;
	}

	@Override
	public boolean canCreateItemWithName(String itemName)
	{
		return getItemByName(itemName) == null;
	}

	@Override
	public boolean hasItem(Leaf<DataItem> item)
	{
		return items.contains(item);
	}

	@Override
	public int indexOfItem(Leaf<DataItem> item)
	{
		return items.indexOf(item);
	}

	@Override
	public boolean isFolderCreationEnabled()
	{
		return true;
	}

	@Override
	public boolean isItemCreationEnabled()
	{
		return true;
	}

	@Override
	public boolean isItemEditingEnabled()
	{
		return true;
	}

	@Override
	public boolean isItemRemovalEnabled()
	{
		return true;
	}
}
