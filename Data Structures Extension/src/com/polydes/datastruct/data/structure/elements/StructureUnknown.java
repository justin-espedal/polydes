package com.polydes.datastruct.data.structure.elements;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureUnknown extends SDE
{
	public String namespace;
	public String tag;
	public HashMap<String, String> atts;
	
	public StructureUnknown(String namespace, String tag, HashMap<String, String> atts)
	{
		this.namespace = namespace;
		this.tag = tag;
		this.atts = atts;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return namespace + ":" + tag;
	}
	
	@Override
	public void disposeEditor() {}
	
	@Override
	public JPanel getEditor() {	return BLANK_EDITOR; }
	
	@Override
	public void revertChanges() {}
	
	public static class UnknownType extends SDEType<StructureUnknown>
	{
		public UnknownType(String tag)
		{
			sdeClass = StructureUnknown.class;
			this.tag = tag;
			isBranchNode = true;
			icon = Resources.loadIcon("question-white.png");
			childTypes = new ArrayList<>();
		}
		
		@Override
		public StructureUnknown read(StructureDefinition model, Element e)
		{
			return new StructureUnknown(e.getPrefix(), e.getLocalName(), XML.readMap(e));
		}

		@Override
		public void write(StructureUnknown object, Element e)
		{
			XML.writeMap(e, object.atts);
		}
		
		@Override
		public StructureUnknown create(StructureDefinition def, String nodeName)
		{
			return null;
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureUnknown value, int i)
		{
			if(parent.getObject() instanceof StructureUnknown)
				return null;
			
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			group.add(sheet.style.createLabel(value.getDisplayLabel()), sheet.style.createDescriptionRow("This element couldn't be loaded."));
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureUnknown value)
		{
			
		}

		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureUnknown value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureUnknown value)
		{
			
		}
	}
}
