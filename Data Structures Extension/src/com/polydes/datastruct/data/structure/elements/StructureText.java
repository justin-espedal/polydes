package com.polydes.datastruct.data.structure.elements;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureTextPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureText extends SDE
{
	private String label;
	private String text;
	
	public StructureText(String label, String text)
	{
		this.label = label;
		this.text = text;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	private StructureTextPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureTextPanel(this, PropertiesSheetStyle.LIGHT);
		
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
	
	public static class TextType extends SDEType<StructureText>
	{
		public TextType()
		{
			sdeClass = StructureText.class;
			tag = "text";
			isBranchNode = false;
			icon = Resources.thumb("text.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureText read(StructureDefinition model, Element e)
		{
			return new StructureText(XML.read(e, "label"), XML.read(e, "text"));
		}

		@Override
		public Element write(StructureText object, Document doc)
		{
			Element e = doc.createElement("text");
			XML.write(e, "label", object.getLabel());
			XML.write(e, "text", object.getText());
			return e;
		}
		
		@Override
		public StructureText create(StructureDefinition def, String nodeName)
		{
			return new StructureText(nodeName, "");
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureText value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			group.add(sheet.style.createLabel(value.getLabel()), sheet.style.createDescriptionRow(value.getText()));
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureText value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureText value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureText value)
		{
			Row r = ((RowGroup) gui).rows[0];
			((JLabel) r.components[0]).setText(value.getLabel());
			((JLabel) r.components[1]).setText(value.getText());
		}
	}
}
