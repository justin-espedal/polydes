package stencyl.ext.polydes.common.nodes;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;

import stencyl.ext.polydes.common.nodes.LeafWalker.LeafRunnable;

/**
 * Branch that can be represented as the root of a hierarchical tree system.
 * Changes to branches and leaves within this root's structure are reflected in
 * representative models.
 */
public class HierarchyModel<T extends Leaf<T>> implements LeafListener<T>, BranchListener<T>
{
	private Branch<T> rootBranch;
	private HierarchyRepresentation<T>[] reps;
	
	private HashSet<String> leafNames;
	private boolean simpleMove;
	private boolean uniqueLeafNames;
	
	@SuppressWarnings("unchecked")
	public HierarchyModel(Branch<T> rootBranch)
	{
		this.rootBranch = rootBranch;
		LeafWalker.installListeners(rootBranch, this, this);
		
		leafNames = new HashSet<String>();
		simpleMove = false;
		uniqueLeafNames = false;
		
		reps = new HierarchyRepresentation[0];
		
		LeafWalker.recursiveRun(rootBranch, new LeafRunnable<T>()
		{
			@Override
			public void run(Leaf<T> item)
			{
				if(!(item instanceof Branch))
					leafNames.add(item.getName());
			}
		});
	}
	
	public void dispose()
	{
		LeafWalker.uninstallListeners(rootBranch, this, this);
		leafNames.clear();
		rootBranch = null;
	}
	
	public ArrayList<Leaf<T>> getPath(Leaf<T> leaf)
	{
		ArrayList<Leaf<T>> list = new ArrayList<Leaf<T>>();
		while(leaf != null)
		{
			list.add(0, leaf);
			if(leaf == rootBranch)
				return list;
			
			leaf = leaf.getParent();
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

	public Branch<T> getRootBranch()
	{
		return rootBranch;
	}
	
	/*================================================*\
	 | Lead/Branch Listening
	\*================================================*/
	
	@Override
	public void branchLeafAdded(Branch<T> folder, Leaf<T> item, int position)
	{
//		System.out.println("Folder Item Added: " + folder + ", " + item);
		if(!simpleMove)
			LeafWalker.installListeners(item, this, this);
		
		modelAddLeaf(folder, item, position);
	}

	@Override
	public void branchLeafRemoved(Branch<T> folder, Leaf<T> item, int position)
	{
//		System.out.println("Folder Item Removed: " + folder + ", " + item);
		if(!simpleMove)
			LeafWalker.uninstallListeners(item, this, this);
		
		modelRemoveLeaf(folder, item, position);
	}
	
	@Override
	public void leafStateChanged(Leaf<T> source)
	{
//		System.out.println("Data Item State Changed: " + source);
		
		for(HierarchyRepresentation<T> rep : reps)
			rep.leafStateChanged(source);
	}

	@Override
	public void leafNameChanged(Leaf<T> source, String oldName)
	{
//		System.out.println("Data Item Name Changed: " + source + ", " + oldName);
		
		if(!(source instanceof Branch))
		{
			leafNames.remove(oldName);
			leafNames.add(source.getName());
		}
		
		for(HierarchyRepresentation<T> rep : reps)
			rep.leafNameChanged(source, oldName);
	}
	
	/*================================================*\
	 | Hierarchy Modification API
	\*================================================*/
	
	public boolean canMoveItem(Leaf<T> item, Branch<T> target)
	{
		//check against moving some folder into itself
		Branch<T> parent = target;
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
	
	public void massMove(Leaf<T>[] transferItems, Branch<T> target, int position)
	{
		simpleMove = true;
		for(Leaf<T> item : transferItems)
			item.getParent().removeItem(item);
		for(Leaf<T> item : transferItems)
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
	
	public void copyItem(Leaf<T> item, Branch<T> target, int position)
	{
		
	}
	
	public void addItem(Leaf<T> item, Branch<T> target, int position)
	{
		target.addItem(item, position);
		if(!(item instanceof Branch))
			leafNames.add(item.getName());
	}
	
	public void removeItem(Leaf<T> item, Branch<T> target)
	{
		target.removeItem(item);
		if(!(item instanceof Branch))
			leafNames.remove(item.getName());
	}
	
	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	public void addRepresentation(HierarchyRepresentation<T> rep)
	{
		reps = ArrayUtils.add(reps, rep);
	}
	
	public void removeRepresentation(HierarchyRepresentation<T> rep)
	{
		reps = ArrayUtils.removeElement(reps, rep);
	}
	
	public boolean isMovingItem()
	{
		return simpleMove;
	}
	
	private void modelAddLeaf(Branch<T> folder, Leaf<T> item, int position)
	{
		for(HierarchyRepresentation<T> rep : reps)
			rep.itemAdded(folder, item, position);
	}
	
	private void modelRemoveLeaf(Branch<T> folder, Leaf<T> item, int position)
	{
		for(HierarchyRepresentation<T> rep : reps)
			rep.itemRemoved(folder, item, position);
	}
}
