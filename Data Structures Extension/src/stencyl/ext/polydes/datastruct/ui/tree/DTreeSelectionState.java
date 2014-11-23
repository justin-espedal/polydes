package stencyl.ext.polydes.datastruct.ui.tree;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.Folder;

public class DTreeSelectionState
{
	public SelectionType type;
	public ArrayList<DefaultMutableTreeNode> nodes;
	public ArrayList<DefaultMutableTreeNode> nodesForTransfer;
	
	public DTreeSelectionState()
	{
		type = SelectionType.FOLDERS;
	}
	
	//called in transfer handler when needed
	public void prepareNodesForTransfer()
	{
		nodesForTransfer = new ArrayList<DefaultMutableTreeNode>();
		HashSet<Folder> folders = new HashSet<Folder>();
		for(DefaultMutableTreeNode node : nodes)
		{
			if(node.getUserObject() instanceof Folder)
				folders.add((Folder) node.getUserObject());
		}
		
		for(DefaultMutableTreeNode node : nodes)
		{
			boolean ignored = false;
			DataItem item = (DataItem) node.getUserObject();
			while((item = item.getParent()) != null)
			{
				if(folders.contains((Folder) item))
				{
					ignored = true;
					break;
				}
			}
			
			if(!ignored)
				nodesForTransfer.add(node);
		}
	}
}
