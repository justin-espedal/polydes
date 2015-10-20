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
	
	public static String readString(Element e, String name, String defaultValue)
	{
		if(e.hasAttribute(name))
			return StringEscapeUtils.unescapeXml(e.getAttribute(name));
		else
			return defaultValue;
	}
	
	public static int readInt(Element e, String name, int defaultValue)
	{
		if(e.hasAttribute(name))
			return Integer.parseInt(e.getAttribute(name));
		else
			return defaultValue;
	}
	
	public static float readFloat(Element e, String name, float defaultValue)
	{
		if(e.hasAttribute(name))
			return Float.parseFloat(e.getAttribute(name));
		else
			return defaultValue;
	}
	
	public static boolean readBoolean(Element e, String name, boolean defaultValue)
	{
		if(e.hasAttribute(name))
			return Boolean.parseBoolean(e.getAttribute(name));
		else
			return defaultValue;
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
}