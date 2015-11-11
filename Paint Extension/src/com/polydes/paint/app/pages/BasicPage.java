package com.polydes.paint.app.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.nodes.NodeSelectionListener;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.paint.data.DataItem;
import com.polydes.paint.data.Folder;

public class BasicPage extends JPanel implements NodeSelectionListener<DataItem,Folder>
{
	protected Boolean listEditEnabled;
	
	protected MiniSplitPane splitPane;
	protected HierarchyModel<DataItem,Folder> folderModel;
	protected DarkTree<DataItem,Folder> tree;
	
	protected NodeSelection<DataItem,Folder> selection;
	
	protected BasicPage()
	{
		super(new BorderLayout());
	}
	
	protected BasicPage(final Folder rootFolder)
	{
		super(new BorderLayout());
		
		folderModel = new HierarchyModel<DataItem,Folder>(rootFolder, DataItem.class, Folder.class);
		tree = new DarkTree<DataItem,Folder>(folderModel);
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(tree);
		
		add(splitPane);
		
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
		
		folderModel.getSelection().addSelectionListener(this);
		
		tree.forceRerender();
	}
	
	public void setListEditEnabled(boolean value)
	{
		if(listEditEnabled == null || listEditEnabled != value)
		{
			listEditEnabled = value;
			if(listEditEnabled)
			{
				tree.setListEditEnabled(true);
			}
			else
			{
				tree.setListEditEnabled(false);
			}
		}
	}
	
	@Override
	public void selectionChanged(NodeSelectionEvent<DataItem, Folder> e)
	{
		
	}
	
	class HorizontalDivider extends JComponent
	{
		public int height;
		public Color color;
		
		public HorizontalDivider(int height)
		{
			color = new Color(0x4F4F4F);
			this.height = height;
		}
		
		@Override
		public Dimension getMinimumSize()
		{
			return new Dimension(1, height);
		}
		
		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(1, height);
		}
		
		@Override
		public Dimension getMaximumSize()
		{
			return new Dimension(Short.MAX_VALUE, height);
		}
		
		@Override
		public void paint(Graphics g)
		{
			g.setColor(color);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}