package com.polydes.datastruct.data.structure.elements;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureHeaderPanel;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.sw.util.comp.RoundedLabel;

public class StructureHeader extends StructureDefinitionElement
{
	private String label;
	
	public StructureHeader(String label)
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
	
	private StructureHeaderPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureHeaderPanel(this, PropertiesSheetStyle.LIGHT);
		
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
	
	public static class HeaderType extends StructureDefinitionElementType<StructureHeader>
	{
		public HeaderType()
		{
			sdeClass = StructureHeader.class;
			tag = "header";
			isBranchNode = false;
			icon = Resources.thumb("header.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureHeader read(StructureDefinition model, Element e)
		{
			return new StructureHeader(XML.read(e, "label"));
		}

		@Override
		public Element write(StructureHeader object, Document doc)
		{
			Element e = doc.createElement("header");
			XML.write(e, "label", object.getLabel());
			return e;
		}

		@Override
		public StructureHeader create(StructureDefinition def, String nodeName)
		{
			return new StructureHeader(nodeName);
		}

		@Override
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureHeader value)
		{
			group.add(sheet.style.rowgap);
			group.add(null, sheet.style.createRoundedLabel("<html><b>" + value.getLabel() + "</b></html>"));
			group.add(sheet.style.rowgap);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureHeader value)
		{
			((RoundedLabel) ((RowGroup) gui).rows[1].components[1]).setText(value.getLabel());
		}
	}
}
