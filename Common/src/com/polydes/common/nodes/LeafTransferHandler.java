package com.polydes.common.nodes;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureRecognizer;
import java.io.IOException;
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
import org.apache.log4j.Logger;

public class LeafTransferHandler<T extends Leaf<T, U>, U extends Branch<T, U>> extends TransferHandler
{
	private static final Logger log = Logger.getLogger(LeafTransferHandler.class);

	private JComponent installedOn;

	protected DataFlavor nodesFlavor;
	protected DataFlavor[] flavors = new DataFlavor[1];
	protected HierarchyModel<T, U> folderModel;

	public LeafTransferHandler(HierarchyModel<T, U> folderModel, JComponent c)
	{
		this.installedOn = c;
		this.folderModel = folderModel;

		try
		{
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";" +
					"class=\"" + folderModel.leafClass.getName() + "\"";
			nodesFlavor = new DataFlavor(mimeType, "Leaf Node", folderModel.leafClass.getClassLoader());
			flavors[0] = nodesFlavor;
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		if(!support.isDataFlavorSupported(nodesFlavor))
			return false;
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Transferable createTransferable(JComponent c)
	{
		folderModel.getSelection().prepareNodesForTransfer();

		if(folderModel.getSelection().getNodesForTransfer().isEmpty())
			return null;

		// name uniqueness check among all selected items.
		HashSet<String> nodeNames = new HashSet<String>();

		for(T item : folderModel.getSelection().getNodesForTransfer())
		{
			if(nodeNames.contains(item.getName()))
				return null;

			nodeNames.add(item.getName());
		}

		ArrayList<T> transfer = folderModel.getSelection().getNodesForTransfer();
		T[] nodes = (T[]) Array.newInstance(folderModel.leafClass, transfer.size());
		transfer.toArray(nodes);

		return new NodesTransferable(nodes);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action)
	{

	}

	@Override
	public int getSourceActions(JComponent c)
	{
		return MOVE;
	}

	@SuppressWarnings("unchecked")
	protected T[] getTransferData(TransferSupport support)
	{
		if(!canImport(support))
			return null;

		try
		{
			Transferable t = support.getTransferable();
			return (T[]) t.getTransferData(nodesFlavor);
		}
		catch(UnsupportedFlavorException | IOException e)
		{
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support)
	{
		if(!canImport(support))
			return false;

		// Extract transfer data.
		T[] nodes = null;
		try
		{
			Transferable t = support.getTransferable();
			nodes = (T[]) t.getTransferData(nodesFlavor);
		}
		catch(UnsupportedFlavorException | IOException e)
		{
			System.out.println("UnsupportedFlavor: " + e.getMessage());
		}

		// Get drop location info.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();
		T parent = (T) dest.getLastPathComponent();

		// Configure for drop mode.
		int visibleIndex = childIndex; // DropMode.INSERT
		if(childIndex == -1) // DropMode.ON
			visibleIndex = ((U) parent).getItems().size();

		// Build folder model representations.
		U parentFolder = (U) parent;
		List<T> transferItems = Arrays.asList(nodes);

		int index = visibleIndex;

		// for all transferring nodes within target folder and pos <
		// visibleChildIndex, decrement childIndex
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

	public class NodesTransferable implements Transferable
	{
		T[] nodes;

		public NodesTransferable(T[] nodes)
		{
			this.nodes = nodes;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
		{
			if(!isDataFlavorSupported(flavor))
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
			if(recognizer != null && recognizer.getComponent() == installedOn)
			{
				recognizer.setComponent(null);
			}
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}

		installedOn = null;
		folderModel = null;
	}
}
