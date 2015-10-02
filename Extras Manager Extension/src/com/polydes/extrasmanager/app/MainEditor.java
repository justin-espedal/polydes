package com.polydes.extrasmanager.app;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import com.polydes.extrasmanager.app.pages.MainPage;

public class MainEditor extends JPanel
{
	private static MainEditor _instance;
	
	private MainPage page;
	
	public static final Color SIDEBAR_COLOR = new Color(62, 62, 62);
	
	private MainEditor()
	{
		super(new BorderLayout());
		
		page = MainPage.get();
		
		add(page);
	}
	
	public static MainEditor get()
	{
		if(_instance == null)
			_instance = new MainEditor();
		
		return _instance;
	}
	
	public static void disposePages()
	{
		MainPage.dispose();
		_instance = null;
	}

	public void gameSaved()
	{
		revalidate();
		repaint();
	}
}
