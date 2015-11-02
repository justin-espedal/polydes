package com.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class FolderPage extends JPanel
{
	private JLabel label;
	private DataItemList list;
	
	public FolderPage(Folder folder)
	{
		super(new BorderLayout());
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		label = new JLabel(folder.getName());
		label.setBackground(null);
		label.setForeground(new Color(0x717171));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setFont(UIConsts.displayNameFont);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		label.setOpaque(true);
		label.setIcon(Folder.folderIcon);
		
		add(label, BorderLayout.NORTH);
		add(list = new DataItemList(folder), BorderLayout.CENTER);
	}
	
	public void dispose()
	{
		removeAll();
		list.dispose();
		list = null;
	}
}
