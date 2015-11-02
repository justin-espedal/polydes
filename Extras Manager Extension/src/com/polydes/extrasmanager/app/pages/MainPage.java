package com.polydes.extrasmanager.app.pages;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreePath;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.extrasmanager.ExtrasManagerExtension;
import com.polydes.extrasmanager.app.list.FileList;
import com.polydes.extrasmanager.app.list.FileListModel;
import com.polydes.extrasmanager.app.list.FileListRenderer;
import com.polydes.extrasmanager.data.FilePreviewer;
import com.polydes.extrasmanager.data.folder.SysFile;
import com.polydes.extrasmanager.data.folder.SysFolder;

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
	protected DarkTree<SysFile,SysFolder> ftree;
	
	protected MiniSplitPane splitPane;
	
	public static MainPage get()
	{
		if (_instance == null)
			_instance = new MainPage();

		return _instance;
	}

	public static void dispose()
	{
		if(_instance != null)
			_instance.localDispose();
		_instance = null;
	}
	
	private void localDispose()
	{
		removeAll();
		
		ftree.dispose();
		flist.dispose();
		flistmodel.dipose();
		ftree = null;
		flist = null;
		flistmodel = null;
		
		flistrenderer = null;
		flistlistener = null;
		currView = null;
		navwindow = null;
		navbar = null;
		
		splitPane.removeAll();
		flistscroller.removeAll();
		ftreescroller.removeAll();
		splitPane = null;
		flistscroller = null;
		ftreescroller = null;
		
		FilePreviewer.endPreview();
	}
	
	protected MainPage()
	{
		super(new BorderLayout());
		
		ftree = new DarkTree<SysFile,SysFolder>(ExtrasManagerExtension.getModel());
		ftree.getTree().setRootVisible(true);
		ftree.disableButtonBar();
		ftreescroller = UI.createScrollPane(ftree);
		ftreescroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		flistmodel = new FileListModel(ExtrasManagerExtension.getModel());
		flistrenderer = new FileListRenderer(-1, -1);
		flistlistener = new ListListener()
		{
			@Override public void pickedItem(Object item)
			{
				setViewedFile((SysFile) item);
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
	
	public SysFolder getViewedFolder()
	{
		return flistmodel.currView;
	}
	
	public void setViewedFile(SysFile f)
	{
		if(f instanceof SysFolder)
		{
			flistmodel.refresh((SysFolder) f);
			setView(flistscroller, flist.getTitlePanel());
			FilePreviewer.endPreview();
		}
		else
		{
			if(flistmodel.currView != f.getParent())
				flistmodel.refresh((SysFolder) f.getParent());
			FilePreviewer.preview(f);
		}
		ftree.getTree().setSelectionPath(new TreePath(ftree.getNode(f).getPath()));
	}
	
	public void update(SysFolder f)
	{
		if(f == flistmodel.currView)
			flistmodel.refresh(f);
	}
	
	public FileListModel getFlistmodel()
	{
		return flistmodel;
	}

	public void setFlistmodel(FileListModel flistmodel)
	{
		this.flistmodel = flistmodel;
	}
}