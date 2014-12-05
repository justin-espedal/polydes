package stencyl.ext.polydes.extrasmanager.app.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import stencyl.ext.polydes.extrasmanager.app.MiniSplitPane;
import stencyl.ext.polydes.extrasmanager.app.list.FileList;
import stencyl.ext.polydes.extrasmanager.app.list.FileListModel;
import stencyl.ext.polydes.extrasmanager.app.list.FileListRenderer;
import stencyl.ext.polydes.extrasmanager.app.tree.FileTree;
import stencyl.ext.polydes.extrasmanager.data.ExtrasDirectory;
import stencyl.ext.polydes.extrasmanager.data.FileOperations;
import stencyl.ext.polydes.extrasmanager.data.FilePreviewer;
import stencyl.sw.app.lists.ListListener;
import stencyl.sw.util.UI;

public class MainPage extends JPanel
{
	private static MainPage _instance;
	
	public static final int DEFAULT_SPLITPANE_WIDTH = 180;
	public static final Color BG_COLOR = new Color(43, 43, 43);
	
	protected JComponent currView;
	public JPanel navwindow;
	public JPanel navbar;
	
	protected JScrollPane flistscroller;
	protected FileList flist;
	private FileListModel flistmodel;
	protected FileListRenderer flistrenderer;
	protected ListListener flistlistener;
	
	protected JScrollPane ftreescroller;
	protected FileTree ftree;
	
	protected MiniSplitPane splitPane;
	
	public static MainPage get()
	{
		if (_instance == null)
			_instance = new MainPage();

		return _instance;
	}

	public static void dispose()
	{
		_instance = null;
	}
	
	protected MainPage()
	{
		super(new BorderLayout());
		
		ftree = new FileTree();
		ftreescroller = UI.createScrollPane(ftree);
		ftreescroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		flistmodel = new FileListModel(new File(ExtrasDirectory.extrasFolder));
		flistrenderer = new FileListRenderer(-1, -1);
		flistlistener = new ListListener()
		{
			@Override public void pickedItem(Object item)
			{
				setViewedFile((File) item);
			}
		};
		
		flist = new FileList(flistrenderer, flistlistener, flistmodel, ftree);
		flistscroller = UI.createScrollPane(flist);
		flistscroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		navwindow = new JPanel(new BorderLayout());
		
		setView(flistscroller, flist.getTitlePanel());
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(ftreescroller);
		splitPane.setRightComponent(navwindow);
		
		add(splitPane);
		
		splitPane.setDividerLocation(DEFAULT_SPLITPANE_WIDTH);
	}
	
	public void setView(JComponent view, JPanel nav)
	{
		if(currView != null)
			navwindow.remove(currView);
		if(navbar != null)
			navwindow.remove(navbar);
		
		navbar = nav;
		currView = view;
		
		if(nav != null)
			navwindow.add(nav, BorderLayout.NORTH);
		if(currView != null)
			navwindow.add(view, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	public File getViewedFile()
	{
		return flistmodel.currView;
	}
	
	public void setViewedFile(File f)
	{
		if(f.isDirectory())
		{
			//if(!flistmodel.currView.equals(f))
			flistmodel.refresh(f);
			setView(flistscroller, flist.getTitlePanel());
		}
		else
		{
			if(!flistmodel.currView.equals(f.getParentFile()))
				flistmodel.refresh(f.getParentFile());
			FilePreviewer.preview(f);
		}
		ftree.setSelected(f);
	}
	
	public void update(File f)
	{
		if(ExtrasDirectory.isSame(f, flistmodel.currView))
			flistmodel.refresh(f);
	}
	
	public void deleteSelected()
	{
		FileOperations.deleteFiles(FileOperations.asFiles(flist.getSelectedValues()));
	}

	public FileListModel getFlistmodel()
	{
		return flistmodel;
	}

	public void setFlistmodel(FileListModel flistmodel)
	{
		this.flistmodel = flistmodel;
	}

	public void updateTree(File file)
	{
		ftree.refreshFNodeFor(file);
	}
}