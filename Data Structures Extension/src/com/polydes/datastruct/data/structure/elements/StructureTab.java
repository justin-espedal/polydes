package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureTabPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureTab extends StructureDefinitionElement
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
	
	public static class TabType extends StructureDefinitionElementType<StructureTab>
	{
		public TabType()
		{
			sdeClass = StructureTab.class;
			tag = "tab";
			isBranchNode = true;
			icon = Resources.thumb("tab.png", 16);
			childTypes = SDETypes.standardChildren;
		}
		
		@Override
		public StructureTab read(StructureDefinition model, Element e)
		{
			return new StructureTab(XML.read(e, "label"));
		}

		@Override
		public Element write(StructureTab object, Document doc)
		{
			Element e = doc.createElement("tab");
			XML.write(e, "label", object.getLabel());
			return e;
		}

		@Override
		public StructureTab create(StructureDefinition def, String nodeName)
		{
			return new StructureTab(nodeName);
		}

		@Override
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureTab value)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureTab value)
		{
			((Card) gui).button.setText(value.getLabel());
		}
	}
}
