package stencyl.ext.polydes.datastruct.ui.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;

public class DTreeTransferHandler extends TransferHandler
{
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
	DefaultMutableTreeNode[] nodesToRemove;
	FolderHierarchyModel folderModel;
	DTree dtree;

	public DTreeTransferHandler(FolderHierarchyModel folderModel, DTree dtree)
	{
//		System.out.println("new DTreeTransferHandler()");
		
		try
		{
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName()
					+ "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
			this.folderModel = folderModel;
			this.dtree = dtree;
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

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
		DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest.getLastPathComponent();

		// don't allow dropping onto selection.
		JTree tree = (JTree) support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		if(selRows == null)
			return false;
		for (int i = 0; i < selRows.length; i++)
		{
			if (selRows[i] == dropRow)
			{
				return false;
			}
		}

		// don't allow dragging of anything into non-folder node
		if (!(target.getUserObject() instanceof Folder))
			return false;

		Folder f = (Folder) target.getUserObject();
		
		// name uniqueness check within target folder
		ArrayList<DefaultMutableTreeNode> nodes = dtree.getSelectionState().nodesForTransfer;
		DataItem item;
		for (DefaultMutableTreeNode node : nodes)
		{
			item = (DataItem) node.getUserObject();
			if (!folderModel.canMoveItem(item, f))
				return false;
		}

		return true;
	}

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
		DataItem item;

		for (DefaultMutableTreeNode node : dtree.getSelectionState().nodesForTransfer)
		{
			item = (DataItem) node.getUserObject();
			if (nodeNames.contains(item.getName()))
				return null;

			nodeNames.add(item.getName());
		}

		DefaultMutableTreeNode[] nodes = dtree.getSelectionState().nodesForTransfer
				.toArray(new DefaultMutableTreeNode[0]);
		return new NodesTransferable(nodes);
	}

	protected void exportDone(JComponent source, Transferable data, int action)
	{
//		System.out.println("exportDone()");
	}

	public int getSourceActions(JComponent c)
	{
//		System.out.println("getSourceActions()");
		return MOVE;
	}

	public boolean importData(TransferSupport support)
	{
//		System.out.println("importData()");
		
		if (!canImport(support))
			return false;
		
		// Extract transfer data.
		DefaultMutableTreeNode[] nodes = null;
		try
		{
			Transferable t = support.getTransferable();
			nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
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
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();
		
		// Configure for drop mode.
		int visibleIndex = childIndex; // DropMode.INSERT
		if (childIndex == -1) // DropMode.ON
			visibleIndex = parent.getChildCount();
		
		// Build folder model representations.
		Folder parentFolder = (Folder) parent.getUserObject();
		DataItem[] transferItems = new DataItem[nodes.length];
		for(int i = 0; i < nodes.length; i++)
			transferItems[i] = (DataItem) nodes[i].getUserObject();
		
		int index = visibleIndex;
		
		//for all transferring nodes within target folder and pos < visibleChildIndex, decrement childIndex
		for(DataItem item : transferItems)
			if(item.getParent() == parentFolder && parentFolder.getItems().indexOf(item) < visibleIndex)
				--index;
		
//		System.out.println(StringUtils.join(transferItems, ", "));
		
		folderModel.massMove(transferItems, parentFolder, index);
		
		return true;
	}

	public String toString()
	{
//		System.out.println("toString()");
		return getClass().getName();
	}

	public class NodesTransferable implements Transferable
	{
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes)
		{
//			System.out.println("new nodesTransferable()");
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException
		{
//			System.out.println("nodesTransferable.getTransferData()");
			
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
//			System.out.println("nodesTransferable.isDataFlavorSupported()");
			return nodesFlavor.equals(flavor);
		}
	}
}
