package com.polydes.datastruct.io.write;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureCondition;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureField;
import com.polydes.datastruct.data.structure.StructureHeader;
import com.polydes.datastruct.data.structure.StructureTab;
import com.polydes.datastruct.data.structure.StructureTabset;
import com.polydes.datastruct.data.structure.StructureText;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtrasMap;

public class StructureDefinitionWriter
{
	public static void write(Document doc, Element root, StructureDefinition def)
	{
		root.setAttribute("classname", def.getClassname());
		for(Leaf<DataItem> n : def.guiRoot.getItems())
			writeNode(doc, root, n);
	}
	
	public static void writeNode(Document doc, Element parent, Leaf<DataItem> guii)
	{
		Element e = null;
		DataItem gui = (DataItem) guii;
		
		if(gui instanceof Folder)
		{
			if(gui.getObject() instanceof StructureTabset)
			{
				e = doc.createElement("tabset");
			}
			else if(gui.getObject() instanceof StructureTab)
			{
				e = doc.createElement("tab");
				e.setAttribute("label", StringEscapeUtils.escapeXml10(((StructureTab) gui.getObject()).getLabel()));
			}
			else if(gui.getObject() instanceof StructureCondition)
			{
				e = doc.createElement("if");
				e.setAttribute("condition", StringEscapeUtils.escapeXml10(((StructureCondition) gui.getObject()).getText()));
			}
			for(Leaf<DataItem> n : ((Folder) gui).getItems())
				writeNode(doc, e, n);
		}
		else
		{
			if(gui.getObject() instanceof StructureHeader)
			{
				e = doc.createElement("header");
				e.setAttribute("label", StringEscapeUtils.escapeXml10(((StructureHeader) gui.getObject()).getLabel()));
			}
			else if(gui.getObject() instanceof StructureText)
			{
				e = doc.createElement("text");
				e.setAttribute("label", StringEscapeUtils.escapeXml10(((StructureText) gui.getObject()).getLabel()));
				e.setAttribute("text", StringEscapeUtils.escapeXml10(((StructureText) gui.getObject()).getText()));
			}
			else if(gui.getObject() instanceof StructureField)
			{
				StructureField f = (StructureField) gui.getObject();
				e = doc.createElement("field");
				e.setAttribute("name", f.getVarname());
				e.setAttribute("type", f.getType().haxeType);
				e.setAttribute("label", StringEscapeUtils.escapeXml10(f.getLabel()));
				if(!f.getHint().isEmpty())
					e.setAttribute("hint", StringEscapeUtils.escapeXml10(f.getHint()));
				if(f.isOptional())
					e.setAttribute("optional", "true");
				
				DataType<?> dtype = f.getType();
				ExtrasMap emap = dtype.saveExtras(f.getExtras());
				if(emap != null)
				{
					for(Entry<String,String> entry : emap.entrySet())
					{
						e.setAttribute(entry.getKey(), entry.getValue());
						//e.setAttribute(field.getName(), StringEscapeUtils.escapeXml10(writeValue));
					}
				}
			}
		}
		
		parent.appendChild(e);
	}
}
