package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.common.nodes.NodeUtils.LeafRunnable;

/**
 * Branch that can be represented as the root of a hierarchical tree system.
 * Changes to branches and leaves within this root's structure are reflected in
 * representative models.
 */
public class HierarchyModel<T extends Leaf<T,U>, U extends Branch<T,U>> implements LeafListener<T,U>, BranchListener<T,U>
{
	public final Class<T> leafClass;
	public final Class<U> branchClass;
	
	private U rootBranch;
	private HierarchyRepresentation<T,U>[] reps;
	private NodeSelection<T, U> selection;
	
	private HashSet<String> leafNames;
	private boolean simpleMove;
	private boolean uniqueLeafNames;
	
	@SuppressWarnings("unchecked")
	public HierarchyModel(U rootBranch, Class<T> leafClass, Class<U> branchClass)
	{
		this.rootBranch = rootBranch;
		this.leafClass = leafClass;
		this.branchClass = branchClass;
		NodeUtils.installListenersRecursive((T) rootBranch, this, this);
		
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
	
	@SuppressWarnings("unchecked")
	public void dispose()
	{
		NodeUtils.uninstallListenersRecursive((T) rootBranch, this, this);
		leafNames.clear();
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
	
	/*================================================*\
	 | Lead/Branch Listening
	\*================================================*/
	
	@Override
	public void branchLeafAdded(U folder, T item, int position)
	{
//		System.out.println("Folder Item Added: " + folder + ", " + item);
		if(!simpleMove)
			NodeUtils.installListenersRecursive(item, this, this);
		
		modelAddLeaf(folder, item, position);
	}

	@Override
	public void branchLeafRemoved(U folder, T item, int position)
	{
//		System.out.println("Folder Item Removed: " + folder + ", " + item);
		if(!simpleMove)
			NodeUtils.uninstallListenersRecursive(item, this, this);
		
		modelRemoveLeaf(folder, item, position);
	}
	
	@Override
	public void leafStateChanged(T source)
	{
//		System.out.println("Data Item State Changed: " + source);
		
		for(HierarchyRepresentation<T,U> rep : reps)
			rep.leafStateChanged(source);
	}

	@Override
	public void leafNameChanged(T source, String oldName)
	{
//		System.out.println("Data Item Name Changed: " + source + ", " + oldName);
		
		if(!(source instanceof Branch))
		{
			leafNames.remove(oldName);
			leafNames.add(source.getName());
		}
		
		for(HierarchyRepresentation<T,U> rep : reps)
			rep.leafNameChanged(source, oldName);
	}
	
	public NodeSelection<T, U> getSelection()
	{
		return selection;
	}
	
	/*================================================*\
	 | Hierarchy Modification API
	\*================================================*/
	
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
			item.getParent().removeItem(item);
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
