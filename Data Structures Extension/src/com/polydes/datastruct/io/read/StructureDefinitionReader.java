package com.polydes.datastruct.io.read;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.io.XML;

public class StructureDefinitionReader
{
	public static void read(Element root, StructureDefinition model)
	{
		readFields(root, model, model.guiRoot);
	}
	
	private static void readFields(Element parent, StructureDefinition model, Folder gui)
	{
		if(parent != null)
		{
			for(Element e : XML.children(parent))
			{
				StructureDefinitionElementType<?> type = SDETypes.fromTag(e.getNamespaceURI(), e.getLocalName());
				StructureDefinitionElement newItem = type.read(model, e);
				
				if(type.isBranchNode)
				{
					Folder item = new Folder(newItem.getDisplayLabel(), newItem);
					readFields(e, model, item);
					gui.addItem(item);
				}
				else
				{
					gui.addItem(new DataItem(newItem.getDisplayLabel(), newItem));
				}
			}
		}
	}
}
