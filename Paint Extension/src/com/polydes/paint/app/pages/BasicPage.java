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
import com.polydes.common.ui.darktree.DTreeSelectionListener;
import com.polydes.common.ui.darktree.DTreeSelectionState;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.paint.data.DataItem;
import com.polydes.paint.data.Folder;

public class BasicPage extends JPanel implements DTreeSelectionListener<DataItem,Folder>
{
	protected Boolean listEditEnabled;
	
	protected MiniSplitPane splitPane;
	protected HierarchyModel<DataItem,Folder> folderModel;
	protected DarkTree<DataItem,Folder> tree;
	
	protected DTreeSelectionState<DataItem,Folder> selectionState;
	
	protected BasicPage()
	{
		super(new BorderLayout());
	}
	
	protected BasicPage(final Folder rootFolder)
	{
		super(new BorderLayout());
		
		folderModel = new HierarchyModel<DataItem,Folder>(rootFolder);
		tree = new DarkTree<DataItem,Folder>(folderModel);
		tree.addTreeListener(this);
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(tree);
		
		add(splitPane);
		
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
		
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				tree.refreshDisplay();
			}
		}, 10);
		
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				tree.refreshDisplay();
			}
		}, 100);
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
	public void setSelectionState(DTreeSelectionState<DataItem,Folder> state)
	{
		this.selectionState = state;
	}
	
	@Override
	public void selectionStateChanged()
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