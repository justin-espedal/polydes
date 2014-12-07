package stencyl.ext.polydes.paint.app.pages;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import stencyl.ext.polydes.paint.app.StatusBar;
import stencyl.ext.polydes.paint.app.editors.text.BasicHighlighter;
import stencyl.ext.polydes.paint.app.editors.text.Highlighter;
import stencyl.ext.polydes.paint.app.editors.text.TextArea;
import stencyl.ext.polydes.paint.app.tree.SelectionType;
import stencyl.ext.polydes.paint.data.Folder;
import stencyl.ext.polydes.paint.data.TextSource;
import stencyl.ext.polydes.paint.res.Resources;
import stencyl.sw.util.UI;

@SuppressWarnings("serial")
public class TextSourcePage extends BasicPage
{
	protected JScrollPane multiScroller;
	protected JPanel multiPage;
	protected JPanel editPane;
	protected JPanel folderPage;
	
	protected JLabel folderIcon;
	protected int folderWidth = Resources.loadIcon("page/folder-large.png").getIconWidth();
	
	protected JComponent currView;
	
	protected ArrayList<JPanel> currPages;
	
	protected Highlighter textAreaHighlighter;
	
	public TextSourcePage(Folder rootFolder)
	{
		super(rootFolder);
		
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
	}
	
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
			ArrayList<TextSource> toEdit = new ArrayList<TextSource>();
			
			for(DefaultMutableTreeNode node : selectionState.nodes)
			{
				if(node.getUserObject() instanceof TextSource)
					toEdit.add((TextSource) node.getUserObject());
			}
			
			TextSource item;
			TextArea editor;
			
			for(int i = 0; i < toEdit.size(); ++i)
			{
				item = toEdit.get(i);
				if(item.getEditor() == null)
					new TextArea(item, textAreaHighlighter);
				editor = (TextArea) item.getEditor();
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
}
