package com.polydes.datastruct.io.read;

import org.w3c.dom.Element;

import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.io.XML;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;

public class HaxeObjectDefinitionReader
{
	public static HaxeObjectDefinition read(String path)
	{
		Element root = XML.getFile(path);
		String haxeClass = root.getAttribute("class");
		Element fields = XML.child(root, "fields");
		
		HaxeField[] hfs = Lang.mapCA(XML.children(fields), HaxeField.class, (field) ->
		{
			String name = field.getAttribute("name");
			String type = field.getAttribute("type");
			ExtrasMap editorData = null;
			
			if(field.hasChildNodes())
			{
				Element editor = XML.child(field, "editor");
				if(editor != null)
				{
					editorData = new ExtrasMap();
					editorData.putAll(XML.readMap(editor));
				}
			}
			
			HaxeField hf = new HaxeField(name, null, editorData);
			DataStructuresExtension.get().getHaxeTypes().requestValue(type, hf);
			
			if(field.hasAttribute("default"))
				hf.defaultValue = field.getAttribute("default");
			
			return hf;
		});
		
		HaxeObjectDefinition def = new HaxeObjectDefinition(haxeClass, hfs);
		
		def.showLabels = XML.readBoolean(fields, "showlabels", true);
		
		Element reader = XML.child(root, "haxereader");
		if(reader != null)
			def.haxereaderExpression = reader.getAttribute("expr");
		
		return def;
	}
}
