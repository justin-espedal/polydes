package com.polydes.common.ui.filelist;

import static com.polydes.common.util.Lang.asArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
		NodeDragData data = getTransferData(support);
		
		// don't allow dropping onto selection.
		if(!dl.isInsert() && data.source == leafList &&
			leafList.isSelectedIndex(dl.getIndex()))
			return false;
		
		T target = dl.isInsert() ?
			(T) leafList.getFolder() :
			leafList.getFolder().getItemAt(dl.getIndex());
		
		// don't allow dragging of anything into non-folder node
		if (!(target instanceof Branch))
			return false;
		
		U f = (U) target;
		
		// name uniqueness check within target folder
		for (T item : data.nodes)
			if (!folderModel.canMoveItem(item, f))
				return false;
		
		return true;
	}
	
	@Override
	protected T[] getNodesToTransfer()
	{
		ArrayList<T> selected = folderModel.getSelection().copyList();
		
		HashSet<T> thisFoldersNodes = new HashSet<T>(leafList.folder.getItems());
		for(Iterator<T> nodeItr = selected.iterator(); nodeItr.hasNext();)
			if(!thisFoldersNodes.contains(nodeItr.next()))
				nodeItr.remove();
		
		return asArray(selected, folderModel.leafClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support)
	{
		NodeDragData data = getTransferData(support);
		
		// Get drop location info.
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		
		U parent = dl.isInsert() ?
				leafList.getFolder() :
				(U) leafList.getFolder().getItemAt(dl.getIndex());
		
		// Build folder model representations.
		List<T> transferItems = Arrays.asList(data.nodes);
		
		int index = dl.getIndex();
		
		if(dl.isInsert())
		{
			//for all transferring nodes within target folder and pos < visibleChildIndex, decrement childIndex
			for(T item : transferItems)
				if(item.getParent() == parent && parent.getItems().indexOf(item) < dl.getIndex())
					--index;
		}
		else
			index = parent.getItems().size();
		
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
