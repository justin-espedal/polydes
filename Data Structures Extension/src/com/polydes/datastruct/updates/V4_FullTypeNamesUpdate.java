package com.polydes.datastruct.updates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polydes.common.io.XML;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.io.Text;

import stencyl.core.lib.Game;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;
import stencyl.sw.util.VerificationHelper;

public class V4_FullTypeNamesUpdate implements Runnable
{
	private static final Logger log = Logger.getLogger(V4_FullTypeNamesUpdate.class);
	
	@Override
	public void run()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		File data = new File(Locations.getExtensionExtrasDataLocation(Game.getGame(), dse.getManifest().id), "data");
		File defs = new File(Locations.getExtensionGameDataLocation(Game.getGame(), dse.getManifest().id), "defs");
		
		HashMap<String, String> typeBackMap = Lang.hashmap(
			"Boolean", "Bool",
			"Color", "com.polydes.datastruct.Color",
			"Control", "com.polydes.datastruct.Control",
			"Image", "com.polydes.datastruct.ExtrasImage",
			"Selection", "com.polydes.datastruct.Selection",
			"Set", "com.polydes.datastruct.Set",
			"ActorType", "com.stencyl.models.actor.ActorType",
			"Background", "com.stencyl.models.Background",
			"Font", "com.stencyl.models.Font",
			"Sound", "com.stencyl.models.Sound",
			"Tileset", "com.stencyl.models.scene.Tileset"
		);
		for(File xmlFile : FileHelper.listFiles(defs, "xml"))
		{
			String name = xmlFile.getName();
			name = name.substring(0, name.length() - ".xml".length());
			try
			{
				Document doc = FileHelper.readXMLFromFile(xmlFile);
				String classname = doc.getDocumentElement().getAttribute("classname");
				typeBackMap.put(name, classname);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		for(File xmlFile : FileHelper.listFiles(defs, "xml"))
		{
			try
			{
				Document doc = FileHelper.readXMLFromFile(xmlFile);
				
				NodeList nl = doc.getElementsByTagName("field");
				for(int i = 0; i < nl.getLength(); ++i)
				{
					Element e = (Element) nl.item(i);
					String type = e.getAttribute("type");
					e.setAttribute("type", Lang.or(typeBackMap.get(type), type));
				}
				
				nl = doc.getElementsByTagName("if");
				for(int i = 0; i < nl.getLength(); ++i)
				{
					Element e = (Element) nl.item(i);
					
					if(e.hasAttribute("condition"))
						continue;
					
					Element conditionElement = XML.child(e, 0);
					String conditionString = subFromXML(conditionElement);
					
					e.removeChild(conditionElement);
					e.setAttribute("condition", conditionString);
				}
				
				FileHelper.writeXMLToFile(doc, xmlFile);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		for(File dataFile : FileHelper.listFiles(data, ""))
		{
			if(dataFile.getName().endsWith(".txt"))
				continue;
			
			HashMap<String, String> props = Text.readKeyValues(dataFile);
			String type = props.get("struct_type");
			props.put("struct_type", Lang.or(typeBackMap.get(type), type));
			Text.writeKeyValues(dataFile, props);
		}
	}
	
	private static String subFromXML(Element e)
	{
		if(e.getTagName().equals("is"))
		{
			return XML.read(e, "field") + " == " + codeRepresentation(XML.read(e, "value"));
		}
		else if(e.getTagName().equals("not"))
		{
			if(XML.child(e, 0).getTagName().equals("is"))
			{
				Element sub = XML.child(e, 0);
				return XML.read(sub, "field") + " != " + codeRepresentation(XML.read(sub, "value"));
			}
			else
				return "!(" + subFromXML(XML.child(e, 0)) + ")";
		}
		else if(e.getTagName().equals("and"))
		{
			return subFromXML(XML.child(e, 0)) + " && " + subFromXML(XML.child(e, 1));
		}
		else if(e.getTagName().equals("or"))
		{
			return subFromXML(XML.child(e, 0)) + " || " + subFromXML(XML.child(e, 1));
		}
		else
			return "";
	}
	
	private static String codeRepresentation(String value)
	{
		if(VerificationHelper.isInteger(value) || VerificationHelper.isFloat(value) || value.equals("true") || value.equals("false"))
			return value;
		else
			return "\"" + value + "\"";
	}
}