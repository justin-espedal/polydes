package com.polydes.dialog.app.pages;

import static com.polydes.common.util.Lang.arraylist;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultEditableLeaf;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeCreator;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.filelist.TreePage;
import com.polydes.dialog.data.TextSource;

public class SourcePage<T extends DefaultEditableLeaf> extends JPanel implements NodeCreator<DefaultLeaf,DefaultBranch>
{
	TreePage<DefaultLeaf,DefaultBranch> treePage;
	MiniSplitPane splitPane;
	
	public SourcePage(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(new BorderLayout());
		treePage = new TreePage<>(model);
		model.setNodeCreator(this);
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treePage.getTree());
		splitPane.setRightComponent(treePage);
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
	}
	
	/*================================================*\
	 | Tree Node Creator
	\*================================================*/
	
	private ArrayList<CreatableNodeInfo> creatableNodeList = arraylist(new CreatableNodeInfo("Dialog Chunk", null, null));
	
	@Override
	public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch branchNode)
	{
		return creatableNodeList;
	}

	@Override
	public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName)
	{
		if(selected.name.equals("Folder"))
			return new DefaultViewableBranch(nodeName);
		
		return new TextSource(nodeName, new ArrayList<>());
	}
	
	@Override
	public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
	{
		return null;
	}

	@Override
	public void editNode(DefaultLeaf DefaultLeaf)
	{
		
	}

	@Override
	public void nodeRemoved(DefaultLeaf toRemove)
	{
		
	}

	@Override
	public boolean attemptRemove(List<DefaultLeaf> toRemove)
	{
		return true;
	}
}
