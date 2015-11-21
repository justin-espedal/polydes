package com.polydes.datastruct.data.folder;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.common.ui.object.EditableObject;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.res.Resources;

public class Folder extends DefaultViewableBranch
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

		@Override
		public boolean fillsViewHorizontally()
		{
			return false;
		}
	}
	
	public Folder(String name, EditableObject object)
	{
		super(name, object);
		policy = null;
	}
	
	@Override
	public void addItem(DefaultLeaf item, int position)
	{
		super.addItem(item, position);
		if(item instanceof Folder && ((Folder) item).policy == null)
			((Folder) item).setPolicy(policy);
	}
	
	public void setPolicy(FolderPolicy policy)
	{
		this.policy = policy;
		for(DefaultLeaf item : items)
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
	public void removeItem(DefaultLeaf item)
	{
		if(Lang.or(policy, DEFAULT_POLICY).duplicateItemNamesAllowed || itemNames.contains(item.getName()))
			super.removeItem(item);
	}
	
	//This is currently never called.
//	public void moveItem(DefaultLeaf item, int position)
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
	
	/*================================================*\
	 | Folder Policies
	\*================================================*/
	
	@Override
	public final boolean canAcceptItem(DefaultLeaf item)
	{
		return Lang.or(policy, DEFAULT_POLICY).canAcceptItem(this, item);
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
	
	@Override
	public ImageIcon getIcon()
	{
		return folderIcon;
	}
}