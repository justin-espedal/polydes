package stencyl.ext.polydes.paint.data;

import java.util.HashMap;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import stencyl.ext.polydes.paint.app.tree.DTree;
import stencyl.ext.polydes.paint.app.tree.DTreeNodeCreator;
import stencyl.ext.polydes.paint.app.tree.DTreeSelectionState;

/*
 * Folder that can be represented as the root of a hierarchical file system.
 * Changes to files and folders within this root's structure are reflected in
 * representative models.
 */

public class FolderHierarchyModel implements DataItemListener, FolderListener, DTreeNodeCreator
{
	private Folder rootFolder;
	private DTree tree;
	private DefaultTreeModel treeModel;
	private HashMap<DataItem, DefaultMutableTreeNode> nodeMap;
	private HashSet<String> itemNames;
	private boolean simpleMove;
	private boolean uniqueItemNames;
	
	private DefaultMutableTreeNode recentlyCreated = null;
	
	public FolderHierarchyModel(Folder rootFolder)
	{
		this.rootFolder = rootFolder;
		installListeners(rootFolder);
		
		nodeMap = new HashMap<DataItem, DefaultMutableTreeNode>();
		itemNames = new HashSet<String>();
		simpleMove = false;
		uniqueItemNames = false;
		
		tree = new DTree(this);
		tree.loadRoot(createNodeFromFolder(rootFolder));
		tree.setNodeCreator(this);
		treeModel = tree.getModel();
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
		//check for siblings with same name
		else
			return (target.getItemByName(item.getName()) == null);
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
			itemNode = nodeMap.get(item);
		else
		{
			itemNode = new DefaultMutableTreeNode(item);
			recentlyCreated = itemNode;
			nodeMap.put(item, itemNode);
		}
		treeModel.insertNodeInto(itemNode, nodeMap.get(folder), position);
	}
	
	private void modelRemoveItem(DataItem item)
	{
		treeModel.removeNodeFromParent(nodeMap.get(item));
		if(!simpleMove)
			nodeMap.remove(item);
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
		nodeMap.put(folder, newNode);
		
		for(DataItem item : folder.getItems())
		{
			if(item instanceof Folder)
				newNode.add(createNodeFromFolder((Folder) item));
			else
			{
				newSubNode = new DefaultMutableTreeNode(item);
				nodeMap.put(item, newSubNode);
				newNode.add(newSubNode);
				itemNames.add(item.getName());
			}
		}
		
		return newNode;
	}

	@Override
	public Object createNode(String nodeName)
	{
		return new TextSource(nodeName);
	}

	@Override
	public Folder createFolder(String newName)
	{
		return new Folder(newName);
	}

	@Override
	public Boolean isNodeCreationEnabled()
	{
		return true;
//				selectionState.nodes.size() == 1 &&
//				selectionState.nodes.get(0).getUserObject() instanceof Folder;
	}
	
	@Override
	public Boolean isRemovalEnabled()
	{
		//IF the root is selected, it should always be at index 0.
		return selectionState.nodes.get(0).getUserObject() != rootFolder;
	}

	@Override
	public Boolean isFolderCreationEnabled()
	{
		return true;
//				selectionState.nodes.size() == 1 &&
//				selectionState.nodes.get(0).getUserObject() instanceof Folder;
	}
	
	protected DTreeSelectionState selectionState;
	
	@Override
	public void setSelectionState(DTreeSelectionState selectionState)
	{
		this.selectionState = selectionState;
	}

	@Override
	public boolean canCreate(String name, Folder folder)
	{
		return
			(folder.getItemByName(name) == null) &&
			(uniqueItemNames ? !itemNames.contains(name) : true);
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
}
