package com.polydes.points.app;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.points.PointsExtension;

import stencyl.sw.SW;

public class PointEditorWindow extends JDialog
{
	private static PointEditorWindow _instance;
	
	public static PointEditorWindow get()
	{
		if(_instance == null)
			_instance = new PointEditorWindow();
		
		return _instance;
	}
	
	private MiniSplitPane splitPane;
	private JPanel contents;
	private boolean initialized;
	
	public PointEditorWindow()
	{
		super(SW.get(), "Point Editor", true);
		
		contents = new JPanel(new BorderLayout());
		
		contents.add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		setContentPane(contents);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeWindow();
			}
		});
		
		setVisible(false);
		initialized = false;
	}
	
	private void closeWindow()
	{
		PointsExtension.pointWindowPos = new Rectangle(getBounds());
		PointsExtension.pointWindowSideWidth = splitPane.getDividerLocation();
		
		setVisible(false);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if(!initialized && visible)
			init();
		
		super.setVisible(visible);
	}
	
	private void init()
	{
		initialized = true;
		
		Rectangle r = PointsExtension.pointWindowPos;
		
		splitPane.setLeftComponent(PointEditorPage.get().getSidebar());
		splitPane.setRightComponent(PointEditorPage.get());
		splitPane.setDividerLocation(PointsExtension.pointWindowSideWidth);
		
		setSize(r.width, r.height);
		
		if(r.x == -1 || r.y == -1)
			setLocationRelativeTo(SW.get());
		else
			setLocation(r.x, r.y);
	}
	
	@Override
	public void dispose()
	{
		splitPane.removeAll();
		contents.removeAll();
		
		super.dispose();
	}
	
	public static void disposeWindow()
	{
		if(_instance != null)
		{
			_instance.setVisible(false);
			_instance.dispose();
			_instance = null;
		}
	}
}
