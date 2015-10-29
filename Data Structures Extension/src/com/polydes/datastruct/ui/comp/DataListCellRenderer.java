package com.polydes.datastruct.ui.comp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.ui.UIConsts;

import stencyl.sw.lnf.Theme;

public class DataListCellRenderer extends JPanel implements TableCellRenderer
{
	DataList model;
	JLabel l;
	
	public DataListCellRenderer()
	{
		super(new BorderLayout());
		
		setOpaque(true);
		
		l = new JLabel();
		l.setBackground(null);
		l.setForeground(Theme.TEXT_COLOR);
		
		add(l, BorderLayout.CENTER);
	}
	
	public void setModel(DataList model)
	{
		this.model = Objects.requireNonNull(model);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (isSelected && table.hasFocus())
		{
			setBackground(UIConsts.TREE_SELECTION_COLOR);
			setForeground(UIConsts.TREE_SELECTION_COLOR);
		}
		else
			setBackground(table.getBackground());
		
		if(value == null)
			l.setText(StringUtils.EMPTY);
		else
			l.setText(column == 0 ? String.valueOf(value) : model.genType.checkToDisplayString(value));
		
		return this;
	}
}

