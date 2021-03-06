package com.polydes.common.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.nodes.NodeUtils.LeafRunnable;
import com.polydes.common.ui.darktree.SelectionType;

/**
 * Branch that can be represented as the root of a hierarchical tree system.
 * Changes to branches and leaves within this root's structure are reflected in
 * representative models.
 */
public class HierarchyModel<T extends Leaf<T,U>, U extends Branch<T,U>> implements PropertyChangeListener, BranchListener<T,U>
{
	/**
	 * Access the model for any root, with the limitation that a single
	 * root shouldn't be used as the basis for multiple models.
	 */
	public static HashMap<Branch<?,?>, HierarchyModel<?, ?>> rootModels = new HashMap<>();
	
	public final Class<T> leafClass;
	public final Class<U> branchClass;
	
	private U rootBranch;
	private HierarchyRepresentation<T,U>[] reps;
	private NodeSelection<T,U> selection;
	private NodeCreator<T,U> nodeCreator;
	
	private HashSet<String> leafNames;
	private boolean simpleMove;
	private boolean uniqueLeafNames;
	
	@SuppressWarnings("unchecked")
	public HierarchyModel(U rootBranch, Class<T> leafClass, Class<U> branchClass)
	{
		rootModels.put(rootBranch, this);
		
		this.rootBranch = rootBranch;
		this.leafClass = leafClass;
		this.branchClass = branchClass;
		
		installListeners((T) rootBranch);
		
		leafNames = new HashSet<String>();
		simpleMove = false;
		uniqueLeafNames = false;
		
		reps = new HierarchyRepresentation[0];
		selection = new NodeSelection<>(this);
		
		NodeUtils.recursiveRun((T) rootBranch, new LeafRunnable<T,U>()
		{
			@Override
			public void run(T item)
			{
				if(!(item instanceof Branch))
					leafNames.add(item.getName());
			}
		});
	}
	
	private void installListeners(T installRoot)
	{
		NodeUtils.recursiveRun(installRoot, (T leaf) -> {
			leaf.addListener(this);
			if(leaf instanceof Branch)
				((Branch<T,U>) leaf).addFolderListener(this);
		});
	}
	
	private void uninstallListeners(T uninstallRoot)
	{
		NodeUtils.recursiveRun(uninstallRoot, (T leaf) -> {
			leaf.removeListener(this);
			if(leaf instanceof Branch)
				((Branch<T,U>) leaf).removeFolderListener(this);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void dispose()
	{
		uninstallListeners((T) rootBranch);
		leafNames.clear();
		rootModels.remove(rootBranch);
		rootBranch = null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> getPath(T leaf)
	{
		ArrayList<T> list = new ArrayList<T>();
		while(leaf != null)
		{
			list.add(0, leaf);
			if(leaf == rootBranch)
				return list;
			
			leaf = (T) leaf.getParent();
		}
		
		return null;
	}
	
	public ArrayList<CreatableNodeInfo> getCreatableNodes(U creationFolder)
	{
		ArrayList<CreatableNodeInfo> items = new ArrayList<CreatableNodeInfo>();
		if(creationFolder.isFolderCreationEnabled())
			items.add(NodeCreator.folderInfo);
		items.addAll(nodeCreator.getCreatableNodeList(creationFolder));
		return items;
	}

	public ArrayList<NodeAction<T>> getNodeActions(T[] targets)
	{
		return nodeCreator.getNodeActions(targets);
	}
	
	public boolean isUniqueLeafNames()
	{
		return uniqueLeafNames;
	}

	public void setUniqueLeafNames(boolean uniqueLeafNames)
	{
		this.uniqueLeafNames = uniqueLeafNames;
	}

	public U getRootBranch()
	{
		return rootBranch;
	}
	
	public NodeSelection<T, U> getSelection()
	{
		return selection;
	}
	
	public void setNodeCreator(NodeCreator<T, U> nodeCreator)
	{
		this.nodeCreator = nodeCreator;
	}
	
	public NodeCreator<T, U> getNodeCreator()
	{
		return nodeCreator;
	}
	
	/*================================================*\
	 | Interface Actions - Hook up to buttons and keys
	\*================================================*/
	
	public void createNewItem(CreatableNodeInfo item)
	{
		U newNodeFolder = getCreationParentFolder(selection);
		
		int insertPosition;
		
		if(selection.getType() == SelectionType.FOLDERS)
			insertPosition = newNodeFolder.getItems().size();
		else
			insertPosition = NodeUtils.getIndex(selection.lastNode()) + 1;
		
		createNewItemFromFolder(item, newNodeFolder, insertPosition);
	}
	
	public void createNewItemFromFolder(CreatableNodeInfo item, U newNodeFolder, int insertPosition)
	{
		T newNodeObject;
		
		if (nodeCreator == null)
			return;
		
		String newName = "New " + item.name + " "; 
		int i = 1;
		
		while(!newNodeFolder.canCreateItemWithName(newName + i))
			++i;
		newName = newName + i;
		
		newNodeObject = nodeCreator.createNode(item, newName);
		if(newNodeObject == null)
			return;
		
		addItem(newNodeObject, newNodeFolder, insertPosition);
		
//		TreePath path = treeModel.getPath(newNodeObject);
//		tree.setSelectionPath(path);
//		
//		if(nameEditingAllowed && newNodeObject.canEditName())
//		{
//			editor.allowEdit();
//			tree.startEditingAtPath(path);
//			editor.clearText();
//		}
	}
	
	public void editItem(T item)
	{
		nodeCreator.editNode(item);
	}
	
	@SuppressWarnings("unchecked")
	public U getCreationParentFolder(NodeSelection<T,U> state)
	{
		if(selection.getType() == SelectionType.FOLDERS)
			return (U) selection.lastNode();
		else if(!selection.isEmpty())
			return (U) selection.lastNode().getParent();
		else
			return null;
	}
	
	/*================================================*\
	 | Internal - Leaf/Branch Listening
	\*================================================*/
	
	@Override
	public void branchLeafAdded(U folder, T item, int position)
	{
//		System.out.println("Folder Item Added: " + folder + ", " + item);
		if(!simpleMove)
			installListeners(item);
		
		modelAddLeaf(folder, item, position);
	}

	@Override
	public void branchLeafRemoved(U folder, T item, int position)
	{
//		System.out.println("Folder Item Removed: " + folder + ", " + item);
		if(!simpleMove)
			uninstallListeners(item);
		
		modelRemoveLeaf(folder, item, position);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getPropertyName() == Leaf.NAME)
		{
			if(!(evt.getSource() instanceof Branch))
			{
				leafNames.remove((String) evt.getOldValue());
				leafNames.add((String) evt.getNewValue());
			}
		}
		
		for(HierarchyRepresentation<T,U> rep : reps)
			rep.propertyChange(evt);
	}
	
	/*================================================*\
	 | Hierarchy Modification API
	\*================================================*/
	
	/*
	 * There are the safest methods to call in order to modify the tree.
	 * Usually called by direct user action.
	 */
	
	public boolean canMoveItem(T item, U target)
	{
		//check against moving some folder into itself
		U parent = target;
		do
		{
			if(parent == item)
				return false;
		}
		while((parent = (U) parent.getParent()) != null);
		
		//already in this folder, just moving it.
		if(target == item.getParent())
			return true;
		
		return target.canAcceptItem(item);
	}
	
	public void massMove(List<T> transferItems, U target, int position)
	{
		simpleMove = true;
		for(T item : transferItems)
		{
			selection.remove(item);
			item.getParent().removeItem(item);
		}
		for(T item : transferItems)
			target.addItem(item, position++);
		simpleMove = false;
	}
	
	//This is currently never called.
//	public void moveItem(Leaf<T> item, Branch<T> target, int position)
//	{
//		simpleMove = true;
//		if(item.getParent() == target)
//			target.moveItem(item, position);
//		else
//		{
//			item.getParent().removeItem(item);
//			target.addItem(item, position);
//		}
//		simpleMove = false;
//	}
	
	public void copyItem(T item, U target, int position)
	{
		
	}
	
	public void addItem(T item, U target, int position)
	{
		target.addItem(item, position);
		if(!(item instanceof Branch))
			leafNames.add(item.getName());
	}
	
	public void removeItem(T item, U target)
	{
		target.removeItem(item);
		if(!(item instanceof Branch))
			leafNames.remove(item.getName());
	}
	
	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	public void addRepresentation(HierarchyRepresentation<T,U> rep)
	{
		reps = ArrayUtils.add(reps, rep);
	}
	
	public void removeRepresentation(HierarchyRepresentation<T,U> rep)
	{
		reps = ArrayUtils.removeElement(reps, rep);
	}
	
	public boolean isMovingItem()
	{
		return simpleMove;
	}
	
	private void modelAddLeaf(U folder, T item, int position)
	{
		for(HierarchyRepresentation<T,U> rep : reps)
			rep.itemAdded(folder, item, position);
	}
	
	private void modelRemoveLeaf(U folder, T item, int position)
	{
		for(HierarchyRepresentation<T,U> rep : reps)
			rep.itemRemoved(folder, item, position);
	}
}
