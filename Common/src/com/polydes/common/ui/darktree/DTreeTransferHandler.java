package com.polydes.common.ui.darktree;

import static com.polydes.common.util.Lang.asArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafTransferHandler;
import com.polydes.common.nodes.NodeUtils;

public class DTreeTransferHandler<T extends Leaf<T,U>, U extends Branch<T,U>> extends LeafTransferHandler<T,U>
{
	DarkTree<T,U> dtree;

	public DTreeTransferHandler(HierarchyModel<T,U> folderModel, DarkTree<T,U> dtree)
	{
		super(folderModel, dtree.getTree());
		this.dtree = dtree;
	}
	
	@Override
	protected T[] getNodesToTransfer()
	{
		ArrayList<T> selected = dtree.getSelectionState().copyList();
		NodeUtils.removeNodesWithContainedParents(selected);
		return asArray(selected, folderModel.leafClass);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean canImport(TransferSupport support)
	{
		if(!super.canImport(support))
			return false;
		if(!support.isDrop())
			return false;
		support.setShowDropLocation(true);
		
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		TreePath dest = dl.getPath();
		T target = (T) dest.getLastPathComponent();

		// don't allow dropping onto selection.
		JTree tree = (JTree) support.getComponent();
		TreePath[] selPaths = tree.getSelectionPaths();
		if(selPaths == null)
		{
			return false;
		}
		for(int i = 0; i < selPaths.length; i++)
		{
			if(selPaths[i].getLastPathComponent() == dl.getPath().getLastPathComponent())
			{
				return false;
			}
		}
		
		// don't allow dragging of anything into non-folder node
		if (!(target instanceof Branch))
		{
			return false;
		}

		U f = (U) target;
		
		// name uniqueness check within target folder
		for (T item : getTransferData(support).nodes)
			if (!folderModel.canMoveItem(item, f))
				return false;
		
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support)
	{
		T[] nodes = getTransferData(support).nodes;
		
		// Get drop location info.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();
		T parent = (T) dest.getLastPathComponent();
		
		// Configure for drop mode.
		int visibleIndex = childIndex; // DropMode.INSERT
		if (childIndex == -1) // DropMode.ON
			visibleIndex = ((U) parent).getItems().size();
		
		// Build folder model representations.
		U parentFolder = (U) parent;
		List<T> transferItems = Arrays.asList(nodes);
		
		int index = visibleIndex;
		
		//for all transferring nodes within target folder and pos < visibleChildIndex, decrement childIndex
		for(T item : transferItems)
			if(item.getParent() == parentFolder && parentFolder.getItems().indexOf(item) < visibleIndex)
				--index;
		
		folderModel.massMove(transferItems, parentFolder, index);
		
		return true;
	}

	@Override
	public String toString()
	{
		return getClass().getName();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		dtree = null;
	}
}
