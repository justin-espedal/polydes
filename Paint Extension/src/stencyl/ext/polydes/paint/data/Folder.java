package stencyl.ext.polydes.paint.data;

import java.util.ArrayList;
import java.util.HashSet;

public class Folder extends DataItem
{
	protected ArrayList<FolderListener> fListeners;
	private ArrayList<DataItem> items;
	private HashSet<String> itemNames;
	
	public Folder(String name)
	{
		super(name);
		fListeners = new ArrayList<FolderListener>();
		contents = items = new ArrayList<DataItem>();
		itemNames = new HashSet<String>();
	}
	
	public void addFolderListener(FolderListener l)
	{
		fListeners.add(l);
	}
	
	public void removeFolderListener(FolderListener l)
	{
		fListeners.remove(l);
	}
	
	public boolean addItem(DataItem item)
	{
		return addItem(item, items.size());
	}
	
	public boolean addItem(DataItem item, int position)
	{
		if(itemNames.contains(item.getName()) || position < 0 || position > items.size())
			return false;
		
		items.add(position, item);
		itemNames.add(item.getName());
		if(item.getParent() != this)
			item.setParent(this);
		for(FolderListener l : fListeners) {l.folderItemAdded(this, item);}
		
		setDirty();
		return true;
	}
	
	public ArrayList<DataItem> getItems()
	{
		return items;
	}
	
	public DataItem getItemByName(String name)
	{
		for(DataItem item : items)
		{
			if(item.getName().equals(name))
				return item;
		}
		
		return null;
	}
	
	public DataItem getItemAt(int position)
	{
		if(position < 0 || position >= items.size())
			return null;
		
		return items.get(position);
	}
	
	public void removeItem(DataItem item)
	{
		if(itemNames.contains(item.getName()))
		{
			items.remove(item);
			itemNames.remove(item.getName());
			for(FolderListener l : fListeners) {l.folderItemRemoved(this, item);}
			
			setDirty();
		}
	}
	
	public void moveItem(DataItem item, int position)
	{
		int curPos = items.indexOf(item);
		if(curPos < position)
			--position;
		if(curPos == position)
			return;
		
		items.remove(item);
		items.add(position, item);
		for(FolderListener l : fListeners) {l.folderItemMoved(this, item, curPos);}
		
		setDirty();
	}
	
	public void unload()
	{
		for(DataItem item : items)
		{
			if(item instanceof Folder)
			{
				((Folder) item).unload();
			}
		}
		items = new ArrayList<DataItem>();
		itemNames = new HashSet<String>();
		super.setClean();
	}
	
	public void setClean()
	{
		super.setClean();
		
		for(DataItem item : items)
		{
			if(item.isDirty())
				item.setClean();
		}
	}

	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}
}
