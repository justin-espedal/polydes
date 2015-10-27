package com.polydes.datastruct.io.write;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;

public class StructureDefinitionWriter
{
	public static void write(Document doc, Element root, StructureDefinition def)
	{
		root.setAttribute("classname", def.getClassname());
		if(def.parent != null)
			root.setAttribute("extends", def.parent.getClassname());
		for(Leaf<DataItem> n : def.guiRoot.getItems())
			writeNode(doc, root, n);
	}
	
	@SuppressWarnings("unchecked")
	public static <S extends SDE> void writeNode(Document doc, Element parent, Leaf<DataItem> gui)
	{
		S obj = (S) ((DataItem) gui).getObject();
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(obj.getClass());
		
		Element e = type.write(obj, doc);
		
		if(gui instanceof Folder)
			for(Leaf<DataItem> n : ((Folder) gui).getItems())
				writeNode(doc, e, n);
		
		parent.appendChild(e);
	}
}
