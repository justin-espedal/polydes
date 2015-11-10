package com.polydes.common.ui.filelist;

import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafTransferHandler;

public class LeafListTransferHandler<T extends Leaf<T,U>, U extends Branch<T,U>> extends LeafTransferHandler<T,U>
{
	LeafList<T,U> leafList;
	
	public LeafListTransferHandler(HierarchyModel<T,U> folderModel, LeafList<T,U> leafList)
	{
		super(folderModel, leafList);
		this.leafList = leafList;
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
		
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		int dropIndex = dl.getIndex();
		T target = leafList.getFolder().getItemAt(dropIndex);
		
		// don't allow dropping onto selection.
		if(leafList.isSelectedIndex(dropIndex))
			return false;
		
		// don't allow dragging of anything into non-folder node
		if (!(target instanceof Branch))
			return false;
		
		U f = (U) target;
		
		// name uniqueness check within target folder
		for (T item : folderModel.getSelection().getNodesForTransfer())
		{
			if (!folderModel.canMoveItem(item, f))
			{
				return false;
			}
		}
		
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support)
	{
		T[] nodes = getTransferData(support);
		if(nodes == null)
			return false;
		
		// Get drop location info.
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		int dropIndex = dl.getIndex();
		U parent = (U) leafList.getFolder().getItemAt(dropIndex);
		
		// Configure for drop mode.
		int visibleIndex = dropIndex; // DropMode.INSERT
		if (dropIndex == -1) // DropMode.ON
			visibleIndex = ((U) parent).getItems().size();
		
		// Build folder model representations.
		List<T> transferItems = Arrays.asList(nodes);
		
		int index = visibleIndex;
		
		//for all transferring nodes within target folder and pos < visibleChildIndex, decrement childIndex
		for(T item : transferItems)
			if(item.getParent() == parent && parent.getItems().indexOf(item) < visibleIndex)
				--index;
		
		folderModel.massMove(transferItems, parent, index);
		
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
		leafList = null;
	}
}
