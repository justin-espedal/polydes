package com.polydes.datastruct.ui.table;

import javax.swing.JComponent;

import layout.TableLayout;

import org.apache.commons.lang3.ArrayUtils;

public class RowGroup implements GuiObject
{
	public Card card;
	public Object data; //structure field, header, deck
	public Row[] rows = null;
	int firstRow;
	
	// Subcard
	
	private boolean subcarded = false;
	
	public RowGroup(Object data)
	{
		this.data = data;
	}
	
	public void addSubcard(Card card, int pad)
	{
		add(card);
		add(pad);
		card.setCard(this.card);
		subcarded = true;
	}
	
	public boolean hasSubcard()
	{
		return subcarded;
	}
	
	public Card getSubcard()
	{
		return (Card) rows[0].components[0];
	}
	
	public Card removeSubcard()
	{
		Card c = (Card) rows[0].components[0];
		c.setCard(null);
		subcarded = false;
		return c;
	}
	
	// Normal
	
	public void add(JComponent... comps)
	{
		rows = ArrayUtils.add(rows, new Row(-2, comps));
	}
	
	public void add(int height)
	{
		rows = ArrayUtils.add(rows, new Row(height));
	}
	
	public void setConditionallyVisible(boolean visible)
	{
		TableLayout layout = card.layout;
		
		for(int i = 0; i < rows.length; ++i)
		{
			for(JComponent component : rows[i].components)
				if(component != null) component.setVisible(visible);
			if(!visible)
				layout.setRow(firstRow + i, 0);
			else
				layout.setRow(firstRow + i, rows[i].height == -2 ? TableLayout.PREFERRED : rows[i].height);
		}
	}
	
	@Override
	public void makeShown()
	{
		card.makeShown();
	}
}