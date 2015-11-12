package com.polydes.datastruct.io.read;

import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;

public class StructureDefinitionReader
{
//	private static final Logger log = Logger.getLogger(StructureDefinitionReader.class);
	
	public static void read(Element root, StructureDefinition model)
	{
		if(root.hasAttribute("extends"))
			model.parent = DataStructuresExtension.get().getStructureDefinitions().getItem(root.getAttribute("extends"));
		readFields(root, model, model.guiRoot);
	}
	
	private static void readFields(Element parent, StructureDefinition model, Folder gui)
	{
		if(parent != null)
		{
			for(Element e : XML.children(parent))
			{
				String ns = e.getNamespaceURI();
				if(ns == null)
					ns = SDETypes.BASE_OWNER;
				SDEType<?> type = DataStructuresExtension.get().getSdeTypes().getItem(ns, e.getLocalName());
				SDE newItem = type.read(model, e);
				
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
