package com.polydes.datastruct.data.structure.elements;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.io.XML;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureUnknown extends StructureDefinitionElement
{
	public String prefix;
	public String tag;
	public HashMap<String, String> atts;
	
	public StructureUnknown(String prefix, String tag, HashMap<String, String> atts)
	{
		this.prefix = prefix;
		this.tag = tag;
		this.atts = atts;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return prefix + ":" + tag;
	}
	
	@Override
	public void disposeEditor() {}
	
	@Override
	public JPanel getEditor() {	return null; }
	
	@Override
	public void revertChanges() {}
	
	public static class UnknownType extends StructureDefinitionElementType<StructureUnknown>
	{
		public UnknownType()
		{
			sdeClass = StructureUnknown.class;
			tag = "unknown";
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
		public Element write(StructureUnknown object, Document doc)
		{
			Element e = doc.createElement(object.tag);
			e.setPrefix(object.prefix);
			XML.writeMap(e, object.atts);
			return e;
		}
		
		@Override
		public StructureUnknown create(StructureDefinition def, String nodeName)
		{
			return null;
		}

		@Override
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureUnknown value)
		{
			group.add(sheet.style.createLabel(value.getDisplayLabel()), sheet.style.createDescriptionRow("This element couldn't be loaded."));
			group.add(sheet.style.rowgap);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureUnknown value)
		{
			
		}
	}
}
