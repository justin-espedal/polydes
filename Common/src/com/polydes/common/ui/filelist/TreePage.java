package com.polydes.common.ui.filelist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.polydes.common.comp.HorizontalDivider;
import com.polydes.common.comp.StatusBar;
import com.polydes.common.comp.VerticalDivider;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.ui.darktree.DTreeSelectionListener;
import com.polydes.common.ui.darktree.DTreeSelectionState;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.darktree.TNode;
import com.polydes.common.ui.object.ViewableObject;

import stencyl.sw.util.UI;

public class TreePage<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel implements DTreeSelectionListener<T,U>
{
	public static final Color PAGE_COLOR = new Color(43, 43, 43);
	
	private HierarchyModel<T,U> folderModel;
	private DarkTree<T,U> tree;
	
	private DTreeSelectionState<T,U> selectionState;
	
	private JScrollPane multiScroller;
	private JPanel multiPage;
	
	protected JComponent currView;
	
	private ArrayList<JPanel> currPages;
	
	public TreePage(HierarchyModel<T,U> folderModel)
	{
		super(new BorderLayout());
		
		this.folderModel = folderModel;
		tree = new DarkTree<T,U>(folderModel);
		tree.addTreeListener(this);
		tree.expandLevel(1);
		
		multiPage = new JPanel();
		multiPage.setLayout(new BoxLayout(multiPage, BoxLayout.Y_AXIS));
		multiPage.setBackground(PAGE_COLOR);
		multiScroller = UI.createScrollPane(multiPage);
		multiScroller.setBackground(null);
		multiScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		multiScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		currPages = new ArrayList<JPanel>();
		
		setBackground(PAGE_COLOR);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		currView = multiScroller;
		add(currView, BorderLayout.CENTER);
		
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
	
	public HierarchyModel<T, U> getFolderModel()
	{
		return folderModel;
	}
	
	public DarkTree<T, U> getTree()
	{
		return tree;
	}
	
	public void refreshSelected()
	{
		selectionStateChanged();
	}
	
	@Override
	public void selectionStateChanged()
	{
		if(currView != null)
			remove(currView);
		
		multiPage.removeAll();
		currPages.clear();
		
		ArrayList<ViewableObject> toView = new ArrayList<ViewableObject>();
		
		for(TNode<T,U> node : selectionState.nodes)
		{
			T uo = node.getUserObject();
			if(!(uo instanceof ViewableObject))
				continue;
			
			toView.add((ViewableObject) uo);
		}
		
		ViewableObject lastObject = null;
		ArrayList<JComponent> thisRow = new ArrayList<>();
		
		for(int i = 0; i < toView.size(); ++i)
		{
			ViewableObject current = toView.get(i);
			boolean thisFill = current.fillsViewHorizontally();
			
			if(lastObject != null)
			{
				boolean prevFill = lastObject.fillsViewHorizontally();
				
				if(!prevFill && thisFill)
				{
					multiPage.add(createRow(thisRow));
					multiPage.add(new HorizontalDivider(2));
					thisRow.clear();
				}
				if(prevFill)
					multiPage.add(new HorizontalDivider(2));
			}
			
			currPages.add(current.getView());
			if(thisFill)
			{
				multiPage.add(current.getView());
//				current.getView().setAlignmentX(LEFT_ALIGNMENT);
			}
			else
				thisRow.add(current.getView());
			
			lastObject = current;
		}
		if(!thisRow.isEmpty())
			multiPage.add(createRow(thisRow));
		thisRow.clear();
		
		if(toView.isEmpty())
			currView = null;
		else
			currView = multiScroller;
		
		if(currView != null)
			add(currView, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	public JPanel createRow(ArrayList<JComponent> components)
	{
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setBackground(null);
		for(int i = 0; i < components.size(); ++i)
		{
			JComponent c = components.get(i);
			c.setMaximumSize(c.getMinimumSize());
			c.setPreferredSize(c.getMinimumSize());
			row.add(c);
			if(i < components.size() - 1)
				row.add(new VerticalDivider(2));
		}
		return row;
	}
	
	@Override
	public void setSelectionState(DTreeSelectionState<T,U> state)
	{
		this.selectionState = state;
	}
	
	public void dispose()
	{
		removeAll();
		folderModel.dispose();
		tree.dispose();
		folderModel = null;
		tree = null;
	}
}
