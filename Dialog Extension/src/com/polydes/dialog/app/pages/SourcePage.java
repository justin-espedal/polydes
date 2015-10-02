package com.polydes.dialog.app.pages;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import stencyl.sw.util.UI;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.ui.darktree.DTreeNodeCreator;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.common.util.PopupUtil.PopupItem;
import com.polydes.dialog.app.StatusBar;
import com.polydes.dialog.app.editors.DataItemEditor;
import com.polydes.dialog.app.editors.EditorFactory;
import com.polydes.dialog.app.editors.text.BasicHighlighter;
import com.polydes.dialog.app.editors.text.Highlighter;
import com.polydes.dialog.app.editors.text.TextArea;
import com.polydes.dialog.data.DataItem;
import com.polydes.dialog.data.Folder;
import com.polydes.dialog.data.LinkedDataItem;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.res.Resources;

public class SourcePage<T extends LinkedDataItem> extends BasicPage implements DTreeNodeCreator<DataItem>
{
	private Class<T> cls;
	
	protected JScrollPane multiScroller;
	protected JPanel multiPage;
	protected JPanel editPane;
	protected JPanel folderPage;
	
	protected JLabel folderIcon;
	protected int folderWidth = Resources.loadIcon("page/folder-large.png").getIconWidth();
	
	protected JComponent currView;
	
	protected ArrayList<JPanel> currPages;
	
	protected Highlighter textAreaHighlighter;
	
	public SourcePage(Class<T> cls, Folder rootFolder)
	{
		super(rootFolder);

		this.cls = cls;
		
		multiPage = new JPanel();
		multiPage.setLayout(new BoxLayout(multiPage, BoxLayout.Y_AXIS));
		multiPage.setBackground(TextArea.TEXT_EDITOR_COLOR);
		multiScroller = UI.createScrollPane(multiPage);
		multiScroller.setBackground(null);
		multiScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		multiScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		currPages = new ArrayList<JPanel>();
		
		textAreaHighlighter = new BasicHighlighter();
		
		editPane = new JPanel(new BorderLayout());
		editPane.setBackground(TextArea.TEXT_EDITOR_COLOR);
		editPane.add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		splitPane.setRightComponent(editPane);
		
		folderPage = new JPanel(new BorderLayout());
		folderPage.setBackground(null);
		folderIcon = new JLabel(Resources.loadIcon("page/folder-large.png"));
		folderPage.add(folderIcon, BorderLayout.CENTER);
		
		editPane.addComponentListener(new ComponentListener()
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
			}
			
			@Override
			public void componentMoved(ComponentEvent e)
			{
			}
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				if(currView == folderPage)
				{
					if(folderPage.getWidth() < folderWidth + 5)
						folderIcon.setIcon(Resources.loadIcon("page/folder-large-hurt.png"));
					else
						folderIcon.setIcon(Resources.loadIcon("page/folder-large.png"));
				}
			}
			
			@Override
			public void componentShown(ComponentEvent e)
			{
			}
		});
		
		currView = folderPage;
		editPane.add(currView, BorderLayout.CENTER);
		
		tree.setNodeCreator(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void selectionStateChanged()
	{
		if(currView == folderPage && (selectionState.type == SelectionType.FOLDERS))
			return;
		
		editPane.remove(currView);
		
		multiPage.removeAll();
		currPages.clear();
		
		currView = null;
		
		if(selectionState.type == SelectionType.FOLDERS)
			currView = folderPage;
		else
		{
			ArrayList<T> toEdit = new ArrayList<T>();
			
			for(DefaultMutableTreeNode node : selectionState.nodes)
			{
				if(cls.isInstance(node.getUserObject()))
					toEdit.add((T) node.getUserObject());
			}
			
			T item;
			DataItemEditor editor;
			
			EditorFactory.textAreaHighlighter = textAreaHighlighter;
			
			for(int i = 0; i < toEdit.size(); ++i)
			{
				item = toEdit.get(i);
				editor = EditorFactory.getEditor(item);
				currPages.add(editor);
				multiPage.add(editor);
				editor.setAlignmentX(LEFT_ALIGNMENT);
				
				if(i + 1 < toEdit.size())
				{
					editor.allowExpandVertical(false);
					multiPage.add(new HorizontalDivider(2));
				}
				else
					editor.allowExpandVertical(true);
			}
			
			currView = multiScroller;
		}

		if (currView != null)
			editPane.add(currView, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	/*================================================*\
	 | Tree Node Creator
	\*================================================*/
	
	private List<PopupItem> creatableNodeList = Arrays.asList(new PopupItem("Dialog Chunk", null, null));
	
	@Override
	public Collection<PopupItem> getCreatableNodeList()
	{
		return creatableNodeList;
	}

	@Override
	public Leaf<DataItem> createNode(PopupItem selected, String nodeName)
	{
		if(selected.text.equals("Folder"))
			return new Folder(nodeName);
		
		return new TextSource(nodeName);
	}

	@Override
	public void editNode(Leaf<DataItem> dataItem)
	{
		
	}

	@Override
	public void nodeRemoved(Leaf<DataItem> toRemove)
	{
		
	}

	@Override
	public boolean attemptRemove(List<Leaf<DataItem>> toRemove)
	{
		return true;
	}
}
