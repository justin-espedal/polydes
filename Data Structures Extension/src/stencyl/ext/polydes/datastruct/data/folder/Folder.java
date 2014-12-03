package stencyl.ext.polydes.datastruct.data.folder;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.utils.Lang;


public class Folder extends DataItem
{
	public static FolderPolicy DEFAULT_POLICY;
	static
	{
		DEFAULT_POLICY = new FolderPolicy();
		DEFAULT_POLICY.duplicateItemNamesAllowed = false;
		DEFAULT_POLICY.folderCreationEnabled = true;
		DEFAULT_POLICY.itemCreationEnabled = true;
		DEFAULT_POLICY.itemEditingEnabled = true;
		DEFAULT_POLICY.itemRemovalEnabled = true;
	}
	protected FolderPolicy policy;
	
	protected ArrayList<FolderListener> fListeners;
	private ArrayList<DataItem> items;
	private HashSet<String> itemNames;
	
	public Folder(String name)
	{
		this(name, new FolderState());
	}
	
	private static class FolderState extends EditableObject
	{
		@Override
		public JPanel getEditor()
		{
			return null;
		}

		@Override
		public void disposeEditor()
		{
		}

		@Override
		public void revertChanges()
		{
		}
	}
	
	public Folder(String name, EditableObject object)
	{
		super(name);
		fListeners = new ArrayList<FolderListener>();
		items = new ArrayList<DataItem>();
		itemNames = new HashSet<String>();
		policy = null;
		this.object = object;
	}
	
	public void addFolderListener(FolderListener l)
	{
		fListeners.add(l);
	}
	
	public void removeFolderListener(FolderListener l)
	{
		fListeners.remove(l);
	}
	
	public void addItem(DataItem item)
	{
		addItem(item, items.size());
	}
	
	public void addItem(DataItem item, int position)
	{
		items.add(position, item);
		itemNames.add(item.getName());
		if(item.getParent() != this)
			item.setParent(this, false);
		for(FolderListener l : fListeners) {l.folderItemAdded(this, item);}
		if(item instanceof Folder && ((Folder) item).policy == null)
			((Folder) item).setPolicy(policy);
		
		setDirty(true);
	}
	
	public void setPolicy(FolderPolicy policy)
	{
		this.policy = policy;
		for(DataItem item : items)
		{
			if(item instanceof Folder && ((Folder) item).policy == null)
				((Folder) item).setPolicy(policy);
		}
	}
	
	public FolderPolicy getPolicy()
	{
		return policy;
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
	
	public int indexOfItem(DataItem item)
	{
		return items.indexOf(item);
	}
	
	public void removeItem(DataItem item)
	{
		if(Lang.or(policy, DEFAULT_POLICY).duplicateItemNamesAllowed || itemNames.contains(item.getName()))
		{
			items.remove(item);
			item.parent = null;
			itemNames.remove(item.getName());
			for(FolderListener l : fListeners) {l.folderItemRemoved(this, item);}
			
			setDirty(true);
		}
	}
	
	//This is currently never called.
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
		
		setDirty(true);
	}
	
	public boolean hasItem(DataItem item)
	{
		return items.contains(item);
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
		super.setDirty(false);
	}
	
	@Override
	public void setDirty(boolean value)
	{
		super.setDirty(value);
		
		if(!value)
			for(DataItem item : items)
				item.setDirty(false);
	}

	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}
	
	/*================================================*\
	 | Folder Policies
	\*================================================*/
	
	public final boolean canAcceptItem(DataItem item)
	{
		return Lang.or(policy, DEFAULT_POLICY).canAcceptItem(this, item);
	}
	
	public final boolean canCreateItemWithName(String itemName)
	{
		return Lang.or(policy, DEFAULT_POLICY).canCreateItemWithName(this, itemName);
	}
	
	public final boolean isItemCreationEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemCreationEnabled;
	}
	
	public final boolean isFolderCreationEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).folderCreationEnabled;
	}
	
	public final boolean isItemRemovalEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemRemovalEnabled;
	}

	public final boolean isItemEditingEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemEditingEnabled;
	}
}