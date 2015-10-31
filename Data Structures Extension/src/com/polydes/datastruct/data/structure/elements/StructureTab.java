package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureTabPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.Deck;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureTab extends SDE
{
	private String label;
	
	public StructureTab(String label)
	{
		this.label = label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	private StructureTabPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureTabPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}
	
	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}
	
	@Override
	public void revertChanges()
	{
		editor.revert();
	}

	@Override
	public String getDisplayLabel()
	{
		return label;
	}
	
	public static class TabType extends SDEType<StructureTab>
	{
		public TabType()
		{
			sdeClass = StructureTab.class;
			tag = "tab";
			isBranchNode = true;
			icon = Resources.thumb("tab.png", 16);
			childTypes = Lang.arraylist(
				StructureCondition.class,
				StructureField.class,
				StructureHeader.class,
				StructureTabset.class,
				StructureText.class
			);
		}
		
		@Override
		public StructureTab read(StructureDefinition model, Element e)
		{
			return new StructureTab(XML.read(e, "label"));
		}

		@Override
		public void write(StructureTab object, Element e)
		{
			XML.write(e, "label", object.getLabel());
		}

		@Override
		public StructureTab create(StructureDefinition def, String nodeName)
		{
			return new StructureTab(nodeName);
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureTab value, int i)
		{
			Deck deckParent = getFirstDeckParent(sheet, parent);
			
			Card card = new Card(value.getLabel(), true);
			deckParent.addCard(card, i);
			
			return card;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTab value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTab value)
		{
			Card card = (Card) gui;
			if(card.deck != null)
				card.deck.removeCard(card);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTab value)
		{
			((Card) gui).button.setText(value.getLabel());
		}
		
		private Deck getFirstDeckParent(PropertiesSheet sheet, DataItem n)
		{
			while(!(n.getObject() instanceof StructureTabset))
				n = (DataItem) n.getParent();
			
			return (Deck) ((RowGroup) sheet.guiMap.get(n)).rows[3].components[0].getComponent(0);
		}
	}
}
