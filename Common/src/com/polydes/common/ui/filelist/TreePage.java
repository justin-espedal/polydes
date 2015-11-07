package com.polydes.common.ui.filelist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.polydes.common.comp.HorizontalDivider;
import com.polydes.common.comp.ScrollablePanel;
import com.polydes.common.comp.ScrollablePanel.ScrollableSizeHint;
import com.polydes.common.comp.StatusBar;
import com.polydes.common.comp.VerticalDivider;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.nodes.NodeSelectionListener;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.object.ViewableObject;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.UI;

public class TreePage<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel implements NodeSelectionListener<T,U>
{
	public static final Color PAGE_COLOR = new Color(43, 43, 43);
	
	private HierarchyModel<T,U> folderModel;
	private DarkTree<T,U> tree;
	
	private JScrollPane multiScroller;
	private ScrollablePanel multiPage;
	
	protected JComponent currView;
	
	private ArrayList<JPanel> currPages;
	
	public TreePage(HierarchyModel<T,U> folderModel)
	{
		super(new BorderLayout());
		
		this.folderModel = folderModel;
		tree = new DarkTree<T,U>(folderModel);
		
		multiPage = new ScrollablePanel();
		multiPage.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
		multiPage.setLayout(new BoxLayout(multiPage, BoxLayout.Y_AXIS));
		multiPage.setBackground(PropertiesSheetStyle.DARK.pageBg);
		multiScroller = UI.createScrollPane(multiPage);
		multiScroller.setBackground(PropertiesSheetStyle.DARK.pageBg);
		multiScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		multiScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		multiScroller.getViewport().setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		currPages = new ArrayList<JPanel>();
		
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		currView = multiScroller;
		add(currView, BorderLayout.CENTER);
		
		tree.forceRerender();
		folderModel.getSelection().addSelectionListener(this);
		refreshSelected();
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
		selectionChanged(null);
	}
	
	@Override
	public void selectionChanged(NodeSelectionEvent<T, U> e)
	{
		if(currView != null)
			remove(currView);
		
		multiPage.removeAll();
		currPages.clear();
		
		ArrayList<ViewableObject> toView = new ArrayList<ViewableObject>();
		
		for(T node : folderModel.getSelection())
		{
			if(!(node instanceof ViewableObject))
				continue;
			
			toView.add((ViewableObject) node);
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
				multiPage.add(current.getView());
			else
				thisRow.add(current.getView());
			
			lastObject = current;
		}
		if(!thisRow.isEmpty())
			multiPage.add(createRow(thisRow));
		thisRow.clear();
		multiPage.add(Box.createVerticalGlue());
		
		if(toView.isEmpty())
			currView = null;
		else
			currView = multiScroller;
		
		if(currView != null)
			add(currView, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	public JComponent createRow(ArrayList<JComponent> components)
	{
		ScrollablePanel row = new ScrollablePanel();
		row.setScrollableWidth(ScrollableSizeHint.STRETCH);
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setBackground(PropertiesSheetStyle.DARK.pageBg);
		for(int i = 0; i < components.size(); ++i)
		{
			JComponent c = components.get(i);
			c.setMaximumSize(c.getMinimumSize());
			c.setPreferredSize(c.getMinimumSize());
			c.setAlignmentY(Component.TOP_ALIGNMENT);
			row.add(c);
			if(i < components.size() - 1)
				row.add(new VerticalDivider(2));
		}
		row.add(Box.createHorizontalGlue());
		return createScrollPane(row);
	}
	
	private JScrollPane createScrollPane(JComponent comp)
	{
		JScrollPane pane = UI.createScrollPane(comp);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		return pane;
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
