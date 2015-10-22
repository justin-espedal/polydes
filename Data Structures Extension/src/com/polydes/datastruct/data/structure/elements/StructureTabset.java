package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.res.Resources;
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
		return new JPanel();
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
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureTabset value)
		{
			final Deck newDeck = new Deck();
			
			group.add(sheet.style.rowgap);
			group.add(newDeck.buttons = Layout.horizontalBox());
			group.add(sheet.style.tabsetgap);
			group.add(newDeck.wrapper);
			group.add(sheet.style.rowgap);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTabset value)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
