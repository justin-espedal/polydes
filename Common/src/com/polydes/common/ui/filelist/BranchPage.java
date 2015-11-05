package com.polydes.common.ui.filelist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.sys.FileRenderer;
import com.polydes.common.ui.filelist.LeafList.LeafRenderer;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.IconUtil;

public class BranchPage<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel
{
	public static final Font DISPLAY_NAME_FONT = new Font("Arial", Font.BOLD, 20);
	
	private JLabel label;
	private LeafList<T,U> list;
	
	private static ImageIcon folderThumb = IconUtil.getIcon(FileRenderer.folderThumb, 32);
	
	public BranchPage(U folder, LeafRenderer<T,U> renderer)
	{
		super(new BorderLayout());
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		label = new JLabel(folder.getName());
		label.setBackground(null);
		label.setForeground(new Color(0x717171));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setFont(DISPLAY_NAME_FONT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		label.setOpaque(true);
		label.setIcon(folderThumb);
		
		add(label, BorderLayout.NORTH);
		add(list = new LeafList<T,U>(folder, renderer), BorderLayout.CENTER);
	}
	
	public void dispose()
	{
		removeAll();
		list.dispose();
		list = null;
	}
}
