package stencyl.ext.polydes.scenelink.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import stencyl.sw.lnf.Theme;

public class GridTable extends JPanel
{
	public static final String HORIZONTAL = "X";
	public static final String VERTICAL = "Y";
	
	private boolean isVert;
	private int count;
	private Dimension size;
	
	public GridTable(String orientation, int i, Dimension cellSize)
	{
		super(null);
		
		isVert = (orientation.equals(VERTICAL));
		count = i;
		size = cellSize;
		
		NumberList list = new NumberList(count);
		list.setLayoutOrientation(isVert ? JList.VERTICAL : JList.VERTICAL_WRAP);
		if(!isVert)
			list.setVisibleRowCount(1);
		list.setCellRenderer(new NumberListRenderer());
		list.setFixedCellWidth(size.width);
		list.setFixedCellHeight(size.height);
		list.setBorder(BorderFactory.createEmptyBorder());
		setPreferredSize(cellSize);
		
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(Theme.DARK_BG_COLOR);
		add(list);
		if(isVert)
			list.setBounds(new Rectangle(0, 0, size.width, count * size.height));
		else
			list.setBounds(new Rectangle(0, 0, count * size.width, size.height));
		
		repaint();
	}
	
	public class NumberList extends JList<Integer>
	{
		public NumberList(int i)
		{
			int count = i;
			Integer[] ints = new Integer[i];
			for(i = 0; i < count; ++i)
			{
				ints[i] = i;
			}
			setListData(ints);
		}
	}
	
	private static Font linkFont = new Font("Verdana", Font.BOLD, 10);
	
	public static int getStringWidth(Graphics g, String s)
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
		Rectangle2D rect = fm.getStringBounds(s, g);
		return (int)Math.round(rect.getWidth());
	}
	
	public static int getFontHeight(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
		return fm.getHeight() - fm.getLeading();
	}
	
	public class NumberListRenderer implements ListCellRenderer<Integer>
	{
		NumberPaintPanel numberPaintPanel = new NumberPaintPanel();
		
		@Override
		public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus)
		{
			numberPaintPanel.value = "" + value;
			return numberPaintPanel;
		}
	}
	
	public class NumberPaintPanel extends JPanel
	{
		public String value = "";
		
		public NumberPaintPanel()
		{
			setBackground(Theme.DARK_BG_COLOR);
			setForeground(Color.WHITE);
			setSize(size);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			g.setFont(linkFont);
			g.drawString(value, size.width / 2 - getStringWidth(g, value) / 2, size.height / 2);
			g.setColor(getBackground().darker());
			g.drawRect(0, 0, size.width - 1, size.height -1 );
		};
	}
}
