package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.Deck;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureTabset extends SDE
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
	
	public static class TabsetType extends SDEType<StructureTabset>
	{
		public TabsetType()
		{
			sdeClass = StructureTabset.class;
			tag = "tabset";
			isBranchNode = true;
			icon = Resources.thumb("tabset.png", 16);
			childTypes = Lang.arraylist(StructureTab.class);
		}
		
		@Override
		public StructureTabset read(StructureDefinition model, Element e)
		{
			return new StructureTabset();
		}

		@Override
		public void write(StructureTabset object, Element e)
		{	
		}
		
		@Override
		public StructureTabset create(StructureDefinition def, String nodeName)
		{
			return new StructureTabset();
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureTabset value, int i)
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
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTabset value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTabset value)
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
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTabset value)
		{
			
		}
	}
}
