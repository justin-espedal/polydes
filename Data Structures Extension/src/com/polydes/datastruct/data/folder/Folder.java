package com.polydes.datastruct.data.folder;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.BranchListener;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.page.FolderPage;


public class Folder extends DataItem implements Branch<DataItem>, ViewableObject
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
	
	public static final ImageIcon folderIcon = Resources.loadIcon("page/folder-small.png");
	
	protected ArrayList<BranchListener<DataItem>> fListeners;
	private ArrayList<Leaf<DataItem>> items;
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
			return BLANK_EDITOR;
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
		fListeners = new ArrayList<BranchListener<DataItem>>();
		items = new ArrayList<Leaf<DataItem>>();
		itemNames = new HashSet<String>();
		policy = null;
		this.object = object;
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
		items.add(position, item);
		itemNames.add(item.getName());
		if(item.getParent() != this)
			item.setParent(this, false);
		for(BranchListener<DataItem> l : fListeners) {l.branchLeafAdded(this, item, position);}
		if(item instanceof Folder && ((Folder) item).policy == null)
			((Folder) item).setPolicy(policy);
		
		setDirty(true);
	}
	
	public void setPolicy(FolderPolicy policy)
	{
		this.policy = policy;
		for(Leaf<DataItem> item : items)
		{
			if(item instanceof Folder && ((Folder) item).policy == null)
				((Folder) item).setPolicy(policy);
		}
	}
	
	public FolderPolicy getPolicy()
	{
		return policy;
	}
	
	@Override
	public ArrayList<Leaf<DataItem>> getItems()
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
	public int indexOfItem(Leaf<DataItem> item)
	{
		return items.indexOf(item);
	}
	
	@Override
	public void removeItem(Leaf<DataItem> item)
	{
		if(Lang.or(policy, DEFAULT_POLICY).duplicateItemNamesAllowed || itemNames.contains(item.getName()))
		{
			int pos = indexOfItem(item);
			
			if(pos != -1)
			{
				items.remove(item);
				item.setParent(null, false);
				itemNames.remove(item.getName());
				for(BranchListener<DataItem> l : fListeners) {l.branchLeafRemoved(this, item, pos);}
			
				setDirty(true);
			}
		}
	}
	
	//This is currently never called.
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
//		setDirty(true);
//	}
	
	@Override
	public boolean hasItem(Leaf<DataItem> item)
	{
		return items.contains(item);
	}
	
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
		super.setDirty(false);
	}
	
	@Override
	public void setDirty(boolean value)
	{
		super.setDirty(value);
		
		if(!value)
			for(Leaf<DataItem> item : items)
				((DataItem) item).setDirty(false);
	}

	@Override
	public void registerNameChange(String oldName, String newName)
	{
		itemNames.remove(oldName);
		itemNames.add(newName);
	}
	
	/*================================================*\
	 | Folder Policies
	\*================================================*/
	
	@Override
	public final boolean canAcceptItem(Leaf<DataItem> item)
	{
		return Lang.or(policy, DEFAULT_POLICY).canAcceptItem(this, (DataItem) item);
	}
	
	@Override
	public final boolean canCreateItemWithName(String itemName)
	{
		return Lang.or(policy, DEFAULT_POLICY).canCreateItemWithName(this, itemName);
	}
	
	@Override
	public final boolean isItemCreationEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemCreationEnabled;
	}
	
	@Override
	public final boolean isFolderCreationEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).folderCreationEnabled;
	}
	
	@Override
	public final boolean isItemRemovalEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemRemovalEnabled;
	}

	@Override
	public final boolean isItemEditingEnabled()
	{
		return Lang.or(policy, DEFAULT_POLICY).itemEditingEnabled;
	}

	private FolderPage view;
	
	@Override
	public JPanel getView()
	{
		if(view == null)
			view = new FolderPage(this);
		return view;
	}

	@Override
	public void disposeView()
	{
		if(view != null)
			view.dispose();
		view = null;
	}
}