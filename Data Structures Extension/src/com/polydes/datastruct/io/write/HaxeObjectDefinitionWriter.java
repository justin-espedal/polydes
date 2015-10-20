package com.polydes.datastruct.io.write;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;
import com.polydes.datastruct.io.XML;

import stencyl.sw.util.FileHelper;

public class HaxeObjectDefinitionWriter
{
	public static void write(HaxeObjectDefinition def, String path)
	{
		Document doc = FileHelper.newDocument();
		Element type = doc.createElement("type");
		type.setAttribute("class", def.haxeClass);
		
		Element fields = doc.createElement("fields");
		for(HaxeField hf : def.fields)
		{
			Element field = doc.createElement("field");
			field.setAttribute("name", hf.name);
			field.setAttribute("type", hf.type.haxeType);
			if(hf.defaultValue != null)
				field.setAttribute("default", hf.defaultValue);
			if(!hf.editorData.isEmpty())
			{
				Element editor = doc.createElement("editor");
				XML.writeMap(editor, hf.editorData);
				field.appendChild(editor);
			}
			fields.appendChild(field);
		}
		
		if(def.haxereaderExpression != null)
		{
			Element haxereader = doc.createElement("haxereader");
			haxereader.setAttribute("expr", def.haxereaderExpression);
			type.appendChild(haxereader);
		}
		
		doc.appendChild(type);
		try
		{
			FileHelper.writeXMLToFile(doc, new File(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
