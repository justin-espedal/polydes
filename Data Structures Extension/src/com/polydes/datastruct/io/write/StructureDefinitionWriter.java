package com.polydes.datastruct.io.write;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.elements.StructureUnknown;

public class StructureDefinitionWriter
{
	private static HashMap<String, String> namespaces = new HashMap<>();
	
	private static String getNS(String ext)
	{
		if(!namespaces.containsKey(ext))
		{
			//generate a short namespace key.
			String largeKey = StringUtils.substringAfterLast(ext, ".");
			String key = largeKey.substring(0, 1);
			while(namespaces.containsValue(key) && key.length() < largeKey.length())
				key = largeKey.substring(0, key.length() + 1);
			
			if(namespaces.containsKey(key))
			key = StringUtils.replace(ext, ".", "");
			
			namespaces.put(ext, key);
		}
		
		return namespaces.get(ext);
	}
	
	public static void write(Document doc, Element root, StructureDefinition def)
	{
		root.setAttribute("classname", def.getFullClassname());
		if(def.parent != null)
			root.setAttribute("extends", def.parent.getFullClassname());
		for(Leaf<DataItem> n : def.guiRoot.getItems())
			writeNode(doc, root, n);
	}
	
	@SuppressWarnings("unchecked")
	public static <S extends SDE> void writeNode(Document doc, Element parent, Leaf<DataItem> gui)
	{
		S obj = (S) ((DataItem) gui).getObject();
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(obj.getClass());
		
		String namespace = (obj instanceof StructureUnknown) ?
			((StructureUnknown) obj).namespace : type.owner;
		
		Element e = (namespace != null) ?
				doc.createElementNS(namespace, getNS(namespace) + ":" + type.tag) :
				doc.createElement(type.tag);
		type.write(obj, e);
		
		if(gui instanceof Folder)
			for(Leaf<DataItem> n : ((Folder) gui).getItems())
				writeNode(doc, e, n);
		
		parent.appendChild(e);
	}
}
