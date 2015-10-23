package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.Deck;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.ui.utils.Layout;

public class StructureTabset extends StructureDefinitionElement
{
	@Override
	public String toString()
	{
		return "Tabset";
	}
	
	@Override
	public JPanel getEditor()
	{
		return BLANK_EDITOR;
	}
	
	@Override
	public void disposeEditor()
	{
	}
	
	@Override
	public void revertChanges()
	{
	}
	
	@Override
	public String getDisplayLabel()
	{
		return "tabset";
	}
	
	public static class TabsetType extends StructureDefinitionElementType<StructureTabset>
	{
		public TabsetType()
		{
			sdeClass = StructureTabset.class;
			tag = "tabset";
			isBranchNode = true;
			icon = Resources.thumb("tabset.png", 16);
			childTypes = SDETypes.tabsetChildren;
		}
		
		@Override
		public StructureTabset read(StructureDefinition model, Element e)
		{
			return new StructureTabset();
		}

		@Override
		public Element write(StructureTabset object, Document doc)
		{
			return doc.createElement("tabset");
		}
		
		@Override
		public StructureTabset create(StructureDefinition def, String nodeName)
		{
			return new StructureTabset();
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureTabset value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			Deck newDeck = new Deck();
			
			group.add(sheet.style.rowgap);
			group.add(newDeck.buttons = Layout.horizontalBox());
			group.add(sheet.style.tabsetgap);
			group.add(newDeck.wrapper);
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			newDeck.setCard(parentCard);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTabset value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTabset value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			Deck deck = (Deck) group.rows[3].components[0].getComponent(0);
			deck.setCard(null);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTabset value)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
