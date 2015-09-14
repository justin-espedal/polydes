package stencyl.ext.polydes.datastruct.ui.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import stencyl.ext.polydes.datastruct.ui.UIConsts;

public class DListCellRenderer extends JLabel implements ListCellRenderer<Object>
{
	public DListCellRenderer()
	{
		setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
		setFont(UIConsts.TREE_LEAF_FONT);
		setForeground(Color.WHITE);
		setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		setText("" + value);
		setBackground(isSelected ? UIConsts.TREE_SELECTION_COLOR : null);
		
		return this;
	}
}