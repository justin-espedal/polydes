package com.polydes.datastruct.io.read;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureCondition;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureField;
import com.polydes.datastruct.data.structure.StructureHeader;
import com.polydes.datastruct.data.structure.StructureTab;
import com.polydes.datastruct.data.structure.StructureTabset;
import com.polydes.datastruct.data.structure.StructureText;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.io.XML;
import com.polydes.datastruct.utils.DelayedInitialize;

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
				if(e.getTagName().equals("if"))
				{
					StructureCondition c;
					
					if(e.hasAttribute("condition"))
					{
						c = new StructureCondition(model, XML.read(e, "condition"));
					}
					else
					{
						//backwards compatibility
						c = StructureCondition.fromXML(model, e);
					}
					
					Folder ifNode = new Folder(c.toString(), c);
					readFields(e, model, ifNode);
					gui.addItem(ifNode);
				}
				else if(e.getTagName().equals("header"))
				{
					StructureHeader h = new StructureHeader(XML.read(e, "label"));
					DataItem headerNode = new DataItem(h.getLabel(), h);
					gui.addItem(headerNode);
				}
				else if(e.getTagName().equals("text"))
				{
					StructureText t = new StructureText(XML.read(e, "label"), XML.read(e, "text"));
					DataItem textNode = new DataItem(t.getLabel(), t);
					gui.addItem(textNode);
				}
				else if(e.getTagName().equals("tabset"))
				{
					Folder tabsetNode = new Folder("Tabset", new StructureTabset());
					readFields(e, model, tabsetNode);
					gui.addItem(tabsetNode);
				}
				else if(e.getTagName().equals("tab"))
				{
					StructureTab tab = new StructureTab(XML.read(e, "label"));
					Folder tabNode = new Folder(tab.getLabel(), tab);
					readFields(e, model, tabNode);
					gui.addItem(tabNode);
				}
				else if(e.getTagName().equals("field"))
				{
					HashMap<String, String> map = XML.readMap(e);
					
					String name = take(map, "name");
					String type = take(map, "type");
					String label = take(map, "label");
					String hint = take(map, "hint");
					boolean optional = take(map, "optional").equals("true");
					ExtrasMap emap = new ExtrasMap();
					emap.putAll(map);
					
					//DataType<?> dtype = Types.fromXML(type);
					StructureField toAdd = new StructureField(model, name, null, label, hint, optional, emap);
					gui.addItem(new DataItem(toAdd.getLabel(), toAdd));
					model.addField(toAdd);
					
					DelayedInitialize.addObject(toAdd, "type", type);
					DelayedInitialize.addMethod(toAdd, "loadExtras", new Object[]{emap}, type);
				}
			}
		}
	}
	
	private static String take(HashMap<String, String> map, String name)
	{
		if(map.containsKey(name))
			return map.remove(name);
		else
			return "";
	}
}
