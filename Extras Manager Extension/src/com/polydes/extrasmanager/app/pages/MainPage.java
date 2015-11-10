package com.polydes.extrasmanager.app.pages;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.common.sys.SysFile;
import com.polydes.common.sys.SysFolder;
import com.polydes.common.ui.filelist.TreePage;
import com.polydes.extrasmanager.ExtrasManagerExtension;
import com.polydes.extrasmanager.data.FilePreviewer;

import stencyl.sw.util.UI;

public class MainPage extends JPanel
{
	private static MainPage _instance;
	
	public static final int DEFAULT_SPLITPANE_WIDTH = 180;
	public static final Color BG_COLOR = new Color(43, 43, 43);
	
	protected JComponent currView;
	public JPanel navwindow;
	public JPanel navbar;
	public JPanel sidebar;
	
	public TreePage<SysFile,SysFolder> treePage;
	
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
		
		treePage.dispose();
		
		currView = null;
		navwindow = null;
		navbar = null;
		sidebar = null;
		
		splitPane.removeAll();
		splitPane = null;
		
		FilePreviewer.endPreview();
	}
	
	protected MainPage()
	{
		super(new BorderLayout());
		
		treePage = new TreePage<SysFile, SysFolder>(ExtrasManagerExtension.getModel());
		
		treePage.getTree().getTree().setRootVisible(true);
		treePage.getTree().disableButtonBar();
		
		JScrollPane treescroller = UI.createScrollPane(treePage.getTree());
		treescroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		navwindow = new JPanel(new BorderLayout());
		navwindow.add(currView = treePage, BorderLayout.CENTER);
		
		revalidate();
		repaint();
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treescroller);
		splitPane.setRightComponent(navwindow);
		
		add(splitPane);
		
		splitPane.setDividerLocation(DEFAULT_SPLITPANE_WIDTH);
	}
}