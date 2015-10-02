package com.polydes.datastruct.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureField;
import com.polydes.datastruct.data.structure.StructureHeader;
import com.polydes.datastruct.data.structure.StructureTab;
import com.polydes.datastruct.data.structure.StructureTabset;
import com.polydes.datastruct.data.structure.cond.StructureCondition;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.utils.DelayedInitialize;

import stencyl.sw.util.FileHelper;

public class XML
{
	public static Document createDocument()
	{
		DocumentBuilder builder = null;
		try
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		return builder.newDocument();
	}
	
	public static void writeDocument(Document d, String url)
	{
		try
		{
			OutputStream out = new FileOutputStream(url);
			
			Result result = new StreamResult(new OutputStreamWriter(out, "utf-8"));
			DOMSource source = new DOMSource(d);
		
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			xformer.transform(source, result);
			out.close();
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static Element getFile(String url)
	{
		String s = "";
		try
		{
			s = new File(url).toURI().toURL().toString();
			return FileHelper.readXMLFromFile(s).getDocumentElement();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Element getStream(InputStream is)
	{
		try
		{
			return FileHelper.readXMLFromStream(is).getDocumentElement();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Element child(Element e, int index)
	{
		NodeList itemList = e.getChildNodes();
		int j = 0;
		
		for(int i = 0; i < itemList.getLength(); i++) 
		{
			if(itemList.item(i) instanceof Element)
			{
				if(j++ == index)
					return (Element) itemList.item(i);
			}
		}
		
		return null;
	}
	
	public static Element child(Element e, String name)
	{
		NodeList itemList = e.getChildNodes();
		
		for(int i = 0; i < itemList.getLength(); i++) 
		{
			if(itemList.item(i) instanceof Element)
			{
				Element e2 = (Element) itemList.item(i);
				if(e2.getTagName().equals(name))
					return e2;
			}
		}
		
		return null;
	}
	
	public static List<Element> children(Element e)
	{
		ArrayList<Element> toReturn = new ArrayList<Element>();
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); ++i)
		{
			if(nl.item(i) instanceof Element)
				toReturn.add((Element) nl.item(i));
		}
		return toReturn;
	}
	
	public static String read(Element e, String name)
	{
		if(e.hasAttribute(name))
			return StringEscapeUtils.unescapeXml(e.getAttribute(name));
		else
			return "";
	}
	
	public static void readDefinition(Element root, StructureDefinition model)
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
					HashMap<String, String> map = readMap(e);
					
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
	
	public static HashMap<String, String> readMap(Element e)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		NamedNodeMap atts = e.getAttributes();
		for(int i = 0; i < atts.getLength(); ++i)
		{
			org.w3c.dom.Node n = atts.item(i);
			map.put(n.getNodeName(), StringEscapeUtils.unescapeXml(n.getNodeValue()));
		}
		return map;
	}
	
	private static String take(HashMap<String, String> map, String name)
	{
		if(map.containsKey(name))
			return map.remove(name);
		else
			return "";
	}
	
	public static void writeDefinition(Document doc, Element root, StructureDefinition def)
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
			else if(gui.getObject() instanceof StructureField)
			{
				StructureField f = (StructureField) gui.getObject();
				e = doc.createElement("field");
				e.setAttribute("name", f.getVarname());
				e.setAttribute("type", f.getType().xml);
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