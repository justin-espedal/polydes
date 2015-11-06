package com.polydes.common.ui.darktree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureRecognizer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;

public class DTreeTransferHandler<T extends Leaf<T,U>, U extends Branch<T,U>> extends TransferHandler
{
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
	HierarchyModel<T,U> folderModel;
	DarkTree<T,U> dtree;

	public DTreeTransferHandler(HierarchyModel<T,U> folderModel, DarkTree<T,U> dtree)
	{
		this.folderModel = folderModel;
		this.dtree = dtree;
		
		try
		{
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=\""
					+ Leaf.class.getName()
					+ "\"";
			nodesFlavor = new DataFlavor(mimeType, "DarkTree Node", Leaf.class.getClassLoader());
			flavors[0] = nodesFlavor;
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canImport(TransferSupport support)
	{
		if (!support.isDrop())
		{
			return false;
		}
		support.setShowDropLocation(true);
		if (!support.isDataFlavorSupported(nodesFlavor))
		{
			return false;
		}

		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		TreePath dest = dl.getPath();
		T target = (T) dest.getLastPathComponent();

		// don't allow dropping onto selection.
		JTree tree = (JTree) support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		if(selRows == null)
		{
			return false;
		}
		for (int i = 0; i < selRows.length; i++)
		{
			if (selRows[i] == dropRow)
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
		for (T item : dtree.getSelectionState().nodesForTransfer)
		{
			if (!folderModel.canMoveItem(item, f))
			{
				return false;
			}
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Transferable createTransferable(JComponent c)
	{
//		System.out.println("createTransferable()");
		
		// get the transfer nodes. removes any children of selected folders from
		// selection.
		dtree.getSelectionState().prepareNodesForTransfer();

		if (dtree.getSelectionState().nodesForTransfer.size() == 0)
			return null;

		// name uniqueness check among all selected items.
		HashSet<String> nodeNames = new HashSet<String>();
		
		for (T item : dtree.getSelectionState().nodesForTransfer)
		{
			if (nodeNames.contains(item.getName()))
				return null;

			nodeNames.add(item.getName());
		}
		
		ArrayList<T> transfer = dtree.getSelectionState().nodesForTransfer;
		T[] nodes = (T[]) Array.newInstance(dtree.getFolderModel().leafClass, transfer.size());
		transfer.toArray(nodes);
		
		return new NodesTransferable(nodes);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action)
	{
//		System.out.println("exportDone()");
	}

	@Override
	public int getSourceActions(JComponent c)
	{
//		System.out.println("getSourceActions()");
		return MOVE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support)
	{
//		System.out.println("importData()");
		if (!canImport(support))
			return false;
		
		// Extract transfer data.
		T[] nodes = null;
		try
		{
			Transferable t = support.getTransferable();
			nodes = (T[]) t.getTransferData(nodesFlavor);
		}
		catch (UnsupportedFlavorException ufe)
		{
			System.out.println("UnsupportedFlavor: " + ufe.getMessage());
		}
		catch (java.io.IOException ioe)
		{
			System.out.println("I/O error: " + ioe.getMessage());
		}
		
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
		
//		System.out.println(StringUtils.join(transferItems, ", "));
		
		folderModel.massMove(transferItems, parentFolder, index);
		
		return true;
	}

	@Override
	public String toString()
	{
//		System.out.println("toString()");
		return getClass().getName();
	}

	public class NodesTransferable implements Transferable
	{
		T[] nodes;

		public NodesTransferable(T[] nodes)
		{
//			System.out.println("new nodesTransferable()");
			this.nodes = nodes;
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException
		{
//			System.out.println("nodesTransferable.getTransferData()");
			
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
//			System.out.println("nodesTransferable.isDataFlavorSupported()");
			return nodesFlavor.equals(flavor);
		}
	}

	public void dispose()
	{
		try
		{
			DragGestureRecognizer recognizer =
				(DragGestureRecognizer) FieldUtils.readStaticField
				(
					TransferHandler.class,
					"recognizer",
					true
				);
			if(recognizer != null && recognizer.getComponent() == dtree.getTree())
			{
				recognizer.setComponent(null);
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		dtree = null;
		folderModel = null;
	}
}
