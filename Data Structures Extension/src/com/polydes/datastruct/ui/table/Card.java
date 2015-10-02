package com.polydes.datastruct.ui.table;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.data.structure.cond.StructureCondition;
import com.polydes.datastruct.ui.utils.Geometry;

import stencyl.sw.util.Fonts;

public class Card extends JPanel implements GuiObject
{
	public static TableLayoutConstraints labelC = new TableLayoutConstraints();
	public static TableLayoutConstraints fieldC = new TableLayoutConstraints();
	public static TableLayoutConstraints deckC = new TableLayoutConstraints();
	static
	{
		labelC.hAlign = TableLayoutConstants.RIGHT;
		labelC.col1 = labelC.col2 = 0;
		fieldC.col1 = fieldC.col2 = 2;
		deckC.col1 = 0;
		deckC.col2 = 3;
	}
	
	private Table table;
	public Card card;
	public Deck deck;
	public String name;
	public JButton button;
	private ActionListener buttonListener;
	
	public TableLayout layout;
	public RowGroup[] rows;
	
	//conditions are nontab cards.
	public boolean isTab;
	
	public Card(String name, boolean isTab)
	{
		this.name = name;
		
		setLayout(layout = new TableLayout(new double[][] {Table.defaultColumns, {}}));
		setBackground(null);
		button = new JButton(name);
		button.setBackground(null);
		rows = new RowGroup[0];
		
		this.isTab = isTab;
		if(!isTab)
		{
			setCondition(null);
		}
	}
	
	public void setDeck(final Deck deck)
	{
		this.deck = deck;
		if(deck != null)
		{
			table = getRoot();
			layout.setColumn(table.layout.getColumn());
		}
		else
			table = null;
		
		if(buttonListener != null)
			button.removeActionListener(buttonListener);
		if(deck != null)
		{
			button.addActionListener(buttonListener = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					deck.show(name);
				}
			});
		}
	}
	
	public void setCard(Card card)
	{
		this.card = card;
		if(card != null)
		{
			table = getRoot();
			layout.setColumn(table.layout.getColumn());
		}
		else
			table = null;
	}
	
	public void addGroup(int i, RowGroup group)
	{
		group.card = this;
		
		int insertAt = 0;
		if(rows.length > i && rows[i] != null)
			insertAt = rows[i].firstRow;
		else if(rows.length > 0)
			insertAt = rows[rows.length - 1].firstRow + rows[rows.length - 1].rows.length;
		
		int numRows = group.rows.length;
		
		for(int j = i; j < rows.length; ++j)
			rows[j].firstRow += numRows;
		group.firstRow = insertAt;
		
		for(Row row : group.rows)
		{
			layout.insertRow(insertAt, row.height == -2 ? TableLayout.PREFERRED : row.height);
			labelC.row1 = labelC.row2 = fieldC.row1 = fieldC.row2 = deckC.row1 = deckC.row2 = insertAt;
			switch(row.components.length)
			{
				case 0:
					break;
				case 1:
					add(row.components[0], deckC);
					break;
				case 2:
					if(row.components[0] != null)
						add(row.components[0], labelC);
					add(row.components[1], fieldC);
					break;
			}
			++insertAt;
		}
		
		rows = ArrayUtils.add(rows, i, group);
	}
	
	public int indexOf(RowGroup group)
	{
		for(int i = 0; i < rows.length; ++i)
		{
			if(rows[i] == group)
				return i;
		}
		
		return -1;
	}
	
	public void removeGroup(int i)
	{
		if(i >= rows.length)
			return;
		
		RowGroup group = rows[i];
		
		group.card = null;
		
		int numRows = group.rows.length;
		
		for(int j = i + 1; j < rows.length; ++j)
			rows[j].firstRow -= numRows;
		
		int removeAt = group.firstRow;
		for(Row row : group.rows)
		{
			for(JComponent comp : row.components)
				if(comp != null)
					remove(comp);
			layout.deleteRow(removeAt);
		}
		
		group.firstRow = 0;
		
		rows = ArrayUtils.remove(rows, i);
	}
	
	@Override
	public void removeAll()
	{
		super.removeAll();
		rows = new RowGroup[]{};
		layout.setRow(new double[]{});
		layoutContainer();
	}
	
	public void layoutContainer()
	{
		layout.layoutContainer(this);
	}
	
	public StructureCondition condition;
	protected boolean visible = true;
	
	public void setCondition(StructureCondition condition)
	{
		this.condition = condition;
		
		if(condition == null)
		{
			lineBorder = null;
			setBorder(BorderFactory.createEmptyBorder());
		}
		else
		{
			if(lineBorder == null)
				setBorder(lineBorder = createTitledBorder("" + condition));
			else
				lineBorder.setTitle("" + condition);
		}
	}
	
	public boolean checkCondition()
	{
		return true;
	}
	
	private TitledBorder lineBorder;
	
	private TitledBorder createTitledBorder(String text)
	{
		return BorderFactory.createTitledBorder
		(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			text,
			TitledBorder.CENTER,
			TitledBorder.TOP,
			Fonts.getNormalFont(),
			table.style.labelColor
		);
	}
	
	public void check()
	{
		if(visible != checkCondition())
		{
			visible = !visible;
			for(RowGroup row : rows)
				row.setConditionallyVisible(visible);
			
			if(visible)
				setBorder(lineBorder);
//			else if(condition.hideWhenFalse)
//				setBorder(BorderFactory.createEmptyBorder());
			else
				setBorder(BorderFactory.createCompoundBorder(lineBorder, BorderFactory.createEmptyBorder(10, 0, 10, 0)));
			
			revalidate();
			repaint();
		}
	}
	
	@Override
	public void makeShown()
	{
		if(deck != null)
		{
			deck.makeShown();
			if(isTab)
				deck.show(name);
		}
		else if(card != null)
			card.makeShown();
	}
	
	public Table getRoot()
	{
		if(isTab)
			return deck.getRoot();
		else
			return card.getRoot();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(!visible)// && !condition.hideWhenFalse)
		{
			String draw = "Hidden";
			Font font = Fonts.getNormalFont().deriveFont(Font.BOLD|Font.ITALIC);
			
			Point drawAt = Geometry.getCenteredStringTopLeft(draw, g, font, this);
			
			g.setFont(font);
			g.setColor(table.style.softLabelColor);
			g.drawString(draw, drawAt.x, drawAt.y + 5);
		}
	}
}