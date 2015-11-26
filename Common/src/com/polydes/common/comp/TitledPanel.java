package com.polydes.common.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.IconUtil;

public class TitledPanel extends JPanel
{
	public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
	public static final int ICON_SIZE = 32;
	
	public JLabel label;
	
	public TitledPanel(String title, ImageIcon icon)
	{
		super(new BorderLayout());
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		label = new JLabel(title);
		label.setBackground(PropertiesSheetStyle.DARK.pageBg);
		label.setForeground(new Color(0x717171));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setFont(TITLE_FONT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		label.setOpaque(true);
		label.setIcon(IconUtil.getIcon(icon, ICON_SIZE));
		
		add(label, BorderLayout.NORTH);
		
		revalidate();
	}
	
	public void dispose()
	{
		removeAll();
		
		label = null;
	}
}
