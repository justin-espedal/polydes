package com.polydes.dialog.app.pages;

import static com.polydes.common.util.Lang.arraylist;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.common.nodes.NodeCreator;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.dialog.app.StatusBar;
import com.polydes.dialog.app.editors.DataItemEditor;
import com.polydes.dialog.app.editors.EditorFactory;
import com.polydes.dialog.app.editors.text.BasicHighlighter;
import com.polydes.dialog.app.editors.text.Highlighter;
import com.polydes.dialog.app.editors.text.TextArea;
import com.polydes.dialog.data.LinkedDataItem;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.res.Resources;

import stencyl.sw.util.UI;

public class SourcePage<T extends LinkedDataItem> extends BasicPage implements NodeCreator<DefaultLeaf,DefaultBranch>
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
	
	//TODO: extend TreePage
	
	public SourcePage(Class<T> cls, DefaultBranch rootFolder)
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
		
		folderModel.setNodeCreator(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
	{
		NodeSelection<DefaultLeaf, DefaultBranch> selection = folderModel.getSelection();
		
		if(currView == folderPage && (selection.getType() == SelectionType.FOLDERS))
			return;
		
		editPane.remove(currView);
		
		multiPage.removeAll();
		currPages.clear();
		
		currView = null;
		
		if(selection.getType() == SelectionType.FOLDERS)
			currView = folderPage;
		else
		{
			ArrayList<T> toEdit = new ArrayList<T>();
			
			for(DefaultLeaf selected : selection)
				if(cls.isInstance(selected))
					toEdit.add((T) selected);
			
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
		
		return new TextSource(nodeName);
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
