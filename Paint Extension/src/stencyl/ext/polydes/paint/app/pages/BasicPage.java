package stencyl.ext.polydes.paint.app.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import stencyl.ext.polydes.paint.app.MiniSplitPane;
import stencyl.ext.polydes.paint.app.tree.DTree;
import stencyl.ext.polydes.paint.app.tree.DTreeSelectionListener;
import stencyl.ext.polydes.paint.app.tree.DTreeSelectionState;
import stencyl.ext.polydes.paint.data.Folder;
import stencyl.ext.polydes.paint.data.FolderHierarchyModel;

@SuppressWarnings("serial")
public class BasicPage extends JPanel implements DTreeSelectionListener
{
	protected Boolean listEditEnabled;
	
	protected MiniSplitPane splitPane;
	protected FolderHierarchyModel folderModel;
	protected DTree tree;
	
	protected DTreeSelectionState selectionState;
	
	protected BasicPage()
	{
		super(new BorderLayout());
	}
	
	protected BasicPage(final Folder rootFolder)
	{
		super(new BorderLayout());
		
		folderModel = new FolderHierarchyModel(rootFolder);
		tree = folderModel.getTree();
		tree.addTreeListener(this);
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(tree);
		
		add(splitPane);
		
		splitPane.setDividerLocation(DTree.DEF_WIDTH);
		
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
	public void setSelectionState(DTreeSelectionState state)
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