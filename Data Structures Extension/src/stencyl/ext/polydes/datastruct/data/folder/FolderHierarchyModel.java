package stencyl.ext.polydes.datastruct.data.folder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeNodeCreator;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeSelectionState;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;

/*
 * Folder that can be represented as the root of a hierarchical file system.
 * Changes to files and folders within this root's structure are reflected in
 * representative models.
 */

public class FolderHierarchyModel implements DataItemListener, FolderListener
{
	private Folder rootFolder;
	private DTree tree;
	private DefaultTreeModel treeModel;
	
	private HashSet<String> itemNames;
	private boolean simpleMove;
	private boolean uniqueItemNames;
	
	private DefaultMutableTreeNode recentlyCreated = null;
	
	public FolderHierarchyModel(Folder rootFolder)
	{
		this.rootFolder = rootFolder;
		installListeners(rootFolder);
		
		itemNames = new HashSet<String>();
		simpleMove = false;
		uniqueItemNames = false;
		
		tree = new DTree(this);
		tree.loadRoot(createNodeFromFolder(rootFolder));
		tree.setNodeCreator(new DefaultNodeCreator()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				return null;
			}
			
			@Override
			public Object createNode(PopupItem selected, String nodeName)
			{
				return null;
			}
			
			@Override
			public void nodeRemoved(DataItem toRemove)
			{
			}
		});
		treeModel = tree.getModel();
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
	
	public DTree getTree()
	{
		return tree;
	}
	
	@Override
	public void folderItemAdded(Folder folder, DataItem item)
	{
//		System.out.println("Folder Item Added: " + folder + ", " + item);
		if(!simpleMove)
			installListeners(item);
		
		modelAddItem(folder, item, folder.getItems().indexOf(item));
	}

	@Override
	public void folderItemRemoved(Folder folder, DataItem item)
	{
//		System.out.println("Folder Item Removed: " + folder + ", " + item);
		if(!simpleMove)
			uninstallListeners(item);
		
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
		tree.getModel().nodeChanged(tree.getNode(source));
		tree.repaint();
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
		
		tree.getModel().nodeChanged(tree.getNode(source));
		tree.repaint();
	}
	
	//these functions called by tree and associated structures.
	
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
	
	private void modelAddItem(Folder folder, DataItem item, int position)
	{
		DefaultMutableTreeNode itemNode;
		if(simpleMove)
			itemNode = tree.getNode(item);
		else
		{
			itemNode = new DefaultMutableTreeNode(item);
			recentlyCreated = itemNode;
			tree.setNode(item, itemNode);
		}
		treeModel.insertNodeInto(itemNode, tree.getNode(folder), position);
	}
	
	private void modelRemoveItem(DataItem item)
	{
		treeModel.removeNodeFromParent(tree.getNode(item));
		if(!simpleMove)
			tree.removeNode(item);
	}
	
	private void installListeners(DataItem item)
	{
		item.addListener(this);
		if(item instanceof Folder)
		{
			((Folder) item).addFolderListener(this);
			for(DataItem curItem : ((Folder) item).getItems())
			{
				installListeners(curItem);
			}
		}
	}
	
	private void uninstallListeners(DataItem item)
	{
		item.removeListener(this);
		if(item instanceof Folder)
		{
			((Folder) item).removeFolderListener(this);
			for(DataItem curItem : ((Folder) item).getItems())
			{
				uninstallListeners(curItem);
			}
		}
	}
	
	private DefaultMutableTreeNode createNodeFromFolder(Folder folder)
	{
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(folder);
		DefaultMutableTreeNode newSubNode;
		tree.setNode(folder, newNode);
		
		for(DataItem item : folder.getItems())
		{
			if(item instanceof Folder)
				newNode.add(createNodeFromFolder((Folder) item));
			else
			{
				newSubNode = new DefaultMutableTreeNode(item);
				tree.setNode(item, newSubNode);
				newNode.add(newSubNode);
				itemNames.add(item.getName());
			}
		}
		
		return newNode;
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

	public DefaultMutableTreeNode getRecentlyCreated()
	{
		return recentlyCreated;
	}
	
	public abstract class DefaultNodeCreator implements DTreeNodeCreator
	{
		@Override
		public abstract Object createNode(PopupItem selected, String nodeName);
		
		@Override
		public void editNode(DataItem toEdit)
		{
		}
		
		@Override
		public abstract void nodeRemoved(DataItem toRemove);
		
		protected DTreeSelectionState selectionState;
		
		@Override
		public void setSelectionState(DTreeSelectionState selectionState)
		{
			this.selectionState = selectionState;
		}
		
		//TODO: Do we need complete uniqueness of item names?
		/*
		@Override
		public boolean canCreate(String name, Folder folder)
		{
			return
				(folder.getItemByName(name) == null) &&
				(uniqueItemNames ? !itemNames.contains(name) : true);
		}
		*/
	}
}
