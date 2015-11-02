package com.polydes.datastruct.data.folder;

import javax.swing.JPanel;

public interface ViewableObject
{
	public static JPanel BLANK_VIEW = new JPanel();
	
	public JPanel getView();
	public void disposeView();
}
