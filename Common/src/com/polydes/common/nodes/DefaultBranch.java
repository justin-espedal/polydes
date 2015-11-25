package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;

import com.polydes.common.res.ResourceLoader;

public class DefaultBranch extends DefaultLeaf implements Branch<DefaultLeaf,DefaultBranch>
{
	public static final ImageIcon folderIcon = ResourceLoader.loadIcon("filelist/folder.png");
	
	protected ArrayList<BranchListener<DefaultLeaf,DefaultBranch>> fListeners;
	protected ArrayList<DefaultLeaf> items;
	protected HashSet<String> itemNames;
	
	public DefaultBranch(String name)
	{
		this(name, null);
	}
	
	public DefaultBranch(String name, Object userData)
	{
		super(name);
		fListeners = new ArrayList<BranchListener<DefaultLeaf,DefaultBranch>>();
		items = new ArrayList<DefaultLeaf>();
		itemNames = new HashSet<String>();
		this.userData = userData;
	}
	
	@Override
	public void addFolderListener(BranchListener<DefaultLeaf,DefaultBranch> l)
	{
		fListeners.add(l);
	}
	
	@Override
	public void removeFolderListener(BranchListener<DefaultLeaf,DefaultBranch> l)
	{
		fListeners.remove(l);
	}
	
	@Override
	public void addItem(DefaultLeaf item)
	{
		addItem(item, items.size());
	}
	
	@Override
	public void addItem(DefaultLeaf item, int position)
	{
		items.add(position, item);
		itemNames.add(item.getName());
		item.parent = this;
		for(BranchListener<DefaultLeaf,DefaultBranch> l : fListeners) {l.branchLeafAdded(this, item, position);}
		
		setDirty(true);
	}
	
	@Override
	public ArrayList<DefaultLeaf> getItems()
	{
		return items;
	}
	
	@Override
	public DefaultLeaf getItemByName(String name)
	{
		for(DefaultLeaf item : items)
		{
			if(item.getName().equals(name))
				return item;
		}
		
		return null;
	}
	
	@Override
	public DefaultLeaf getItemAt(int position)
	{
		if(position < 0 || position >= items.size())
			return null;
		
		return items.get(position);
	}
	
	@Override
	public int indexOfItem(DefaultLeaf item)
	{
		return items.indexOf(item);
	}
	
	@Override
	public void removeItem(DefaultLeaf item)
	{
		int pos = indexOfItem(item);
		
		if(pos != -1)
		{
			items.remove(item);
			itemNames.remove(item.getName());
			item.parent = null;
			
			for(BranchListener<DefaultLeaf,DefaultBranch> l : fListeners) {l.branchLeafRemoved(this, item, pos);}
		
			setDirty(true);
		}
	}
	
	//This is currently never called.
//		public void moveItem(DefaultLeaf item, int position)
//		{
//			int curPos = items.indexOf(item);
//			if(curPos < position)
//				--position;
//			if(curPos == position)
//				return;
//			
//			items.remove(item);
//			items.add(position, item);
//			for(DefaultBranchListener l : fListeners) {l.DefaultBranchItemMoved(this, item, curPos);}
//			
//			setDirty(true);
//		}
	
	@Override
	public boolean hasItem(DefaultLeaf item)
	{
		return items.contains(item);
	}
	
	public void unload()
	{
		for(DefaultLeaf item : items)
			if(item instanceof DefaultBranch)
				((DefaultBranch) item).unload();
		items = new ArrayList<DefaultLeaf>();
		itemNames = new HashSet<String>();
		super.setDirty(false);
	}
	
	@Override
	public void setDirty(boolean value)
	{
		super.setDirty(value);
		
		if(!value)
			for(DefaultLeaf item : items)
				item.setDirty(false);
	}

	@Override
	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}
	
	/*================================================*\
	 | DefaultBranch Policies
	\*================================================*/
	
	@Override
	public boolean canAcceptItem(DefaultLeaf item)
	{
		return true;
	}
	
	@Override
	public boolean canCreateItemWithName(String itemName)
	{
		return !itemNames.contains(itemName);
	}
	
	@Override
	public boolean isItemCreationEnabled()
	{
		return true;
	}
	
	@Override
	public boolean isFolderCreationEnabled()
	{
		return true;
	}
	
	@Override
	public boolean isItemRemovalEnabled()
	{
		return true;
	}

	@Override
	public boolean isItemEditingEnabled()
	{
		return true;
	}
	
	@Override
	public ImageIcon getIcon()
	{
		return folderIcon;
	}
}