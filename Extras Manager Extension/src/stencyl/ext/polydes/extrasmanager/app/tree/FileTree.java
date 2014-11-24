package stencyl.ext.polydes.extrasmanager.app.tree;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import stencyl.ext.polydes.extrasmanager.app.utils.ExtrasUtil;
import stencyl.ext.polydes.extrasmanager.data.ExtrasDirectory;

@SuppressWarnings("serial")
public class FileTree extends JTree
{
	public static final Color BG_COLOR = new Color(62, 62, 62);
	
	public FileTree()
	{
		DefaultMutableTreeNode root = readFile(new File(ExtrasDirectory.extrasFolder));
		TreeModel model = new DefaultTreeModel(root);
		setModel(model);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//setBackground(BG_COLOR);
		//setForeground(Color.WHITE);
	}
	
	public FNode readFile(File f)
	{
		if(f.isDirectory())
		{
			FNode parent = new FNode(f);
			HashSet<String> toExclude = f.equals(ExtrasDirectory.extrasFolderF) ? ExtrasDirectory.ownedFolderNames : null;
			for(File curFile : ExtrasUtil.orderFiles(f.listFiles(), toExclude))
				parent.add(readFile(curFile));
			return parent;
		}
		else
			return new FNode(f);
	}
	
	public void refreshFNodeFor(File f)
	{
		FNode n = getFNode(f);
		n.removeAllChildren();
		if(f.isDirectory())
		{
			HashSet<String> toExclude = f.equals(ExtrasDirectory.extrasFolderF) ? ExtrasDirectory.ownedFolderNames : null;
			for(File curFile : ExtrasUtil.orderFiles(f.listFiles(), toExclude))
				n.add(readFile(curFile));
		}
		((DefaultTreeModel) treeModel).nodeStructureChanged(n);
	}
	
	public FNode getFNode(File f)
	{
		File currFile = f;
		ArrayList<File> fpath = new ArrayList<File>();
		
		fpath.add(currFile);
		while(!currFile.equals(ExtrasDirectory.extrasFolderF))
			fpath.add(0, currFile = currFile.getParentFile());
		
		int i = 0;
		
		String[] spath = new String[fpath.size()]; 
		for(File file : fpath)
			spath[i++] = file.getName();
		
		FNode currNode = (FNode) treeModel.getRoot();
		i = 1;
		
		while(i < spath.length)
		{
			@SuppressWarnings("unchecked")
			Enumeration<FNode> e = currNode.children();
			
			while(e.hasMoreElements())
			{
				FNode n = e.nextElement(); 
				if(n.toString().equals(spath[i]))
				{
					currNode = n;
					break;
				}
			}
			
			++i;
		}
		
		return currNode;
	}
	
	public void setSelected(File f)
	{
		File currFile = f;
		ArrayList<File> fpath = new ArrayList<File>();
		
		fpath.add(currFile);
		while(!currFile.equals(ExtrasDirectory.extrasFolderF))
			fpath.add(0, currFile = currFile.getParentFile());
		
		int i = 0;
		
		String[] spath = new String[fpath.size()]; 
		for(File file : fpath)
			spath[i++] = file.getName();
		
		FNode[] nodes = new FNode[spath.length];
		FNode currNode = (FNode) treeModel.getRoot();
		nodes[0] = currNode;
		i = 1;
		
		while(i < spath.length)
		{
			@SuppressWarnings("unchecked")
			Enumeration<FNode> e = currNode.children();
			
			while(e.hasMoreElements())
			{
				FNode n = e.nextElement(); 
				if(n.toString().equals(spath[i]))
				{
					nodes[i] = n;
					currNode = n;
					break;
				}
			}
			
			++i;
		}
		
		setSelectionPath(new TreePath(nodes));
	}
}
