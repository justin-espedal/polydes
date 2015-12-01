package com.polydes.common.comp.datalist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.core.DataList;
import com.polydes.common.ui.darktree.DTreeUI;
import com.polydes.common.util.IconUtil;

import stencyl.sw.lnf.Theme;

public class DataListCellRenderer extends JPanel implements TableCellRenderer
{
	DataList model;
	JLabel l;
	Function<Object, ImageIcon> iconProvider;
	
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
	
	public void setIconProvider(Function<Object, ImageIcon> iconProvider)
	{
		this.iconProvider = iconProvider;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if(isSelected && table.hasFocus())
			setBackground(DTreeUI.highlightColor);
		else if(isSelected)// && table.hasFocus())
			setBackground(DTreeUI.highlightColor.darker());
		else
			setBackground(table.getBackground());
		
		if(value == null)
			l.setText(StringUtils.EMPTY);
		else
			l.setText(column == 0 ? String.valueOf(value) : model.genType.checkToDisplayString(value));
		
		if(iconProvider != null)
			l.setIcon(IconUtil.getIcon(iconProvider.apply(value), 16));
		
		return this;
	}
}

