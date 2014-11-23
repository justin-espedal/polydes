package stencyl.ext.polydes.datastruct.ui.table;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;

import stencyl.ext.polydes.datastruct.ui.utils.AdaptingCardLayout;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;

public class Deck extends JPanel implements GuiObject
{
	public Card card;
	public JComponent buttons;
	public ButtonGroup bgroup;
	public JPanel wrapper;
	
	public Card[] cards = null;
	public String[] labels = null;
	private CardLayout layout;
	
	public Deck()
	{
		setLayout(layout = new AdaptingCardLayout());
		buttons = Layout.horizontalBox();
		bgroup = new ButtonGroup();
		wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		wrapper.add(this, BorderLayout.CENTER);
	}
	
	public int getDepth()
	{
		if(card instanceof Table)
			return card.rows.length == 1 ? 0 : 1;
		else
			return card.deck.getDepth() + 1;
	}
	
	public void setCard(Card card)
	{
		this.card = card;
		
		if(card != null)
		{
			setBackground(getDepth() % 2 == 0 ? PropertiesSheetStyle.DARK.pageBg : PropertiesSheetStyle.DARK.pageBg.darker());
			wrapper.setBackground(getBackground());
		}
	}
	
	public void addCard(Card card)
	{
		card.setDeck(this);
		
		cards = ArrayUtils.add(cards, card);
		labels = ArrayUtils.add(labels, card.name);
		refreshButtons();
		bgroup.add(card.button);
		add(card, card.name);
	}
	
	public void removeCard(Card card)
	{
		card.setDeck(null);
		
		cards = ArrayUtils.remove(cards, ArrayUtils.indexOf(cards, card));
		labels = ArrayUtils.remove(labels, ArrayUtils.indexOf(labels, card.name));
		refreshButtons();
		bgroup.remove(card.button);
		remove(card);
	}
	
	private void refreshButtons()
	{
		ArrayList<JButton> jbuttons = new ArrayList<JButton>();
		for(Card card : cards)
			jbuttons.add(card.button);
		Layout.horizontalBoxExisting(buttons, jbuttons.toArray(new JButton[0]));
	}
	
	public void show(String cardName)
	{
		layout.show(this, cardName);
	}
	
	@Override
	public void makeShown()
	{
		card.makeShown();
	}
	
	public Table getRoot()
	{
		return card.getRoot();
	}
}