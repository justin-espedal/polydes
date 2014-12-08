package stencyl.ext.polydes.extrasmanager.data.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import stencyl.ext.polydes.common.nodes.Branch;
import stencyl.ext.polydes.common.nodes.BranchListener;
import stencyl.ext.polydes.common.nodes.Leaf;

public class SysFolder extends SysFile implements Branch<SysFile>
{
	private ArrayList<BranchListener<SysFile>> flisteners;
	private ArrayList<Leaf<SysFile>> items;
	private HashSet<String> itemNames;
	
	public SysFolder(File file)
	{
		super(file);
		flisteners = new ArrayList<BranchListener<SysFile>>();
		items = new ArrayList<Leaf<SysFile>>();
		itemNames = new HashSet<String>();
	}

	@Override
	public List<Leaf<SysFile>> getItems()
	{
		return items;
	}

	@Override
	public void addFolderListener(BranchListener<SysFile> l)
	{
		flisteners.add(l);
	}

	@Override
	public void removeFolderListener(BranchListener<SysFile> l)
	{
		flisteners.remove(l);
	}

	@Override
	public void addItem(Leaf<SysFile> item)
	{
		addItem(item, items.size());
	}

	@Override
	public void addItem(Leaf<SysFile> item, int position)
	{
		items.add(position, item);
		itemNames.add(item.getName());
		if(item.getParent() != this)
			item.setParent(this, false);
		for(BranchListener<SysFile> l : flisteners) {l.branchLeafAdded(this, item, position);}
	}

	@Override
	public Leaf<SysFile> getItemByName(String name)
	{
		for(Leaf<SysFile> item : items)
		{
			if(item.getName().equals(name))
				return item;
		}
		
		return null;
	}

	@Override
	public Leaf<SysFile> getItemAt(int position)
	{
		if(position < 0 || position >= items.size())
			return null;
		
		return items.get(position);
	}

	@Override
	public int indexOfItem(Leaf<SysFile> item)
	{
		return items.indexOf(item);
	}
	
	public int findInsertionIndex(String name, boolean isFolder)
	{
		int i;
		
		for(i = 0; i < items.size(); ++i)
		{
			SysFile current = (SysFile) items.get(i);
			if(isFolder)
			{
				if(!(current instanceof SysFolder))
					break;
			}
			else
			{
				if(current instanceof SysFolder)
					continue;
			}
			
			if(current.getName().compareToIgnoreCase(name) > 0)
				break;
		}
		
		return i;
	}

	@Override
	public void removeItem(Leaf<SysFile> item)
	{
		int pos = items.indexOf(item);
		items.remove(item);
		item.setParent(null, false);
		itemNames.remove(item.getName());
		for(BranchListener<SysFile> l : flisteners) {l.branchLeafRemoved(this, item, pos);}
	}

	@Override
	public boolean hasItem(Leaf<SysFile> item)
	{
		return items.contains(item);
	}
	
	@Override
	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}

	@Override
	public boolean canAcceptItem(Leaf<SysFile> item)
	{
		return getItemByName(item.getName()) == null;
	}

	@Override
	public boolean canCreateItemWithName(String itemName)
	{
		return getItemByName(itemName) == null;
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
}
