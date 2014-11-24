package stencyl.ext.polydes.scenelink.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stencyl.ext.polydes.scenelink.data.Link;
import stencyl.ext.polydes.scenelink.ui.Reflect;
import stencyl.ext.polydes.scenelink.util.ColorUtil;
import stencyl.sw.util.FileHelper;

public class XML
{
	private static class XMLState
	{
		public Element e;
		public ArrayList<Element> layer;
		public int layerIndex;
	}

	public static void saveState()
	{
		XMLState s = new XMLState();
		s.e = e;
		s.layer = layer;
		s.layerIndex = layerIndex;
		states.push(s);
	}
	
	public static void restoreState()
	{
		XMLState s = states.pop();
		e = s.e;
		layer = s.layer;
		layerIndex = s.layerIndex;
	}
	
	private static Element e = null;
	private static ArrayList<Element> layer = null;
	private static int layerIndex = -1;
	
	private static Stack<XMLState> states = new Stack<XMLState>();

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
	
	public static Element getElement()
	{
		return e;
	}

	public static void setElement(Element e)
	{
		XML.e = e;
	}
	
	public static void setFile(String url)
	{
		String s = "";
		try
		{
			s = new File(url).toURI().toURL().toString();
			XML.e = FileHelper.readXMLFromFile(s).getDocumentElement();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void descend()
	{
		saveState();
		
		NodeList itemList = e.getChildNodes();
		layer = new ArrayList<Element>();
		layerIndex = -1;
		
		for(int i = 0; i < itemList.getLength(); i++) 
		{
			if(itemList.item(i) instanceof Element)
			{
				layer.add((Element) itemList.item(i));
			}
		}
	}
	
	public static void ascend()
	{
		restoreState();
	}
	
	public static boolean hasNext()
	{
		return (layerIndex + 1) < layer.size();
	}
	
	public static void next()
	{
		e = layer.get(++layerIndex);
	}
	
	public static boolean elementNameIs(String name)
	{
		return e.getTagName().equals(name);
	}
	
	public static int rint(String name)
	{
		return rint(name, 0);
	}
	
	public static int rint(String name, int defReturn)
	{
		String s = e.getAttribute(name);
		if(s.equals(""))
			return defReturn;
		return Integer.parseInt(s);
	}
	
	public static Integer rInteger(String name)
	{
		return rint(name, 0);
	}
	
	public static void wrint(String name, int i)
	{
		e.setAttribute(name, ""+i);
	}
	
	public static boolean rboolean(String name)
	{
		return Boolean.parseBoolean(e.getAttribute(name));
	}
	
	public static Boolean rBoolean(String name)
	{
		return Boolean.parseBoolean(e.getAttribute(name));
	}
	
	public static void wrboolean(String name, boolean b)
	{
		e.setAttribute(name, b ? "true" : "false");
	}
	
	public static void wrBoolean(String name, Boolean b)
	{
		e.setAttribute(name, b ? "true" : "false");
	}
	
	public static String rString(String name)
	{
		return e.getAttribute(name);
	}
	
	public static void wrString(String name, String s)
	{
		e.setAttribute(name, s);
	}
	
	public static Color rColor(String name)
	{
		return ColorUtil.decode(e.getAttribute(name));
	}
	
	public static void wrColor(String name, Color c)
	{
		e.setAttribute(name, ColorUtil.encode(c));
	}
	
	public static Float rFloat(String name)
	{
		return Float.parseFloat(e.getAttribute(name));
	}
	
	public static void wrFloat(String name, Float f)
	{
		e.setAttribute(name, "" + f);
	}
	
	public static float rfloat(String name)
	{
		return Float.parseFloat(e.getAttribute(name));
	}
	
	public static void wrfloat(String name, float f)
	{
		e.setAttribute(name, "" + f);
	}
	
//	public static BufferedImage rImage(String name)
//	{
//		return Resources.readImage(e.getAttribute(name));
//	}
	
	public static Point rPoint(String name)
	{
		int[] ints = getInts(e.getAttribute(name));
		if(ints == null)
			return new Point(0, 0);
		
		return new Point(ints[0], ints[1]);
	}
	
	public static void wrPoint(String name, Point p)
	{
		e.setAttribute(name, p.x + ", " + p.y);
	}
	
	public static Dimension rDimension(String name)
	{
		int[] ints = getInts(e.getAttribute(name));
		if(ints == null)
			return new Dimension(0, 0);
		
		return new Dimension(ints[0], ints[1]);
	}
	
	public static void wrDimension(String name, Dimension d)
	{
		e.setAttribute(name, d.width + ", " + d.height);
	}
	
	public static Rectangle rRectangle(String name)
	{
		int[] ints = getInts(e.getAttribute(name));
		if(ints == null)
			return new Rectangle(0, 0, 0, 0);
		
		return new Rectangle(ints[0], ints[1], ints[2], ints[3]);
	}
	
	public static void wrRectangle(String name, Rectangle r)
	{
		e.setAttribute(name, r.x + ", " + r.y + ", " + r.width + ", " + r.height);
	}
	
	public static int[] getInts(String fromString)
	{
		if(fromString.length() == 0)
			return null;
		
		String[] splitString = fromString.split(",");
		int[] toReturn = new int[splitString.length];
		for(int i = 0; i < splitString.length; ++i)
			toReturn[i] = Integer.parseInt(splitString[i].trim());
		return toReturn;
	}
	
	public static Link rLink(String name)
	{
		String linkText = e.getAttribute(name);
		if(linkText.equals(""))
			return Link.createBlank();
		
		String[] s = linkText.split(" ");
		return Link.create(s[0], Integer.parseInt(s[1]));
	}
	
	public static void wrLink(String name, Link l)
	{
		e.setAttribute(name, Link.getStringRef(l));
	}

	public static Element wrElement(Document d, String name, Object o, String[] attrs)
	{
		Element e2 = e;
		e = d.createElement(name);
		if(o != null && attrs.length > 0)
		{
			Field[] fields = Reflect.getFields(o, attrs);
			Object[] vals = Reflect.getValues(o, attrs);
			for(int i = 0; i < attrs.length; ++i)
			{
				Method writer = Reflect.getMethod(XML.class, "wr" + fields[i].getType().getSimpleName(), String.class, fields[i].getType());
				Reflect.invoke(writer, null, attrs[i], vals[i]);
			}
		}
		
		Element toReturn = e;
		e = e2;
		return toReturn;
	}
	
	public static void wrObjectToFile(String url, Object o)
	{
		XML.saveState();
		
		Document d = XML.createDocument();
		Element root = XML.wrObjectToElement(d, o);
		d.appendChild(root);
		
		XML.writeDocument(d, url);
		
		XML.restoreState();
	}
	
	public static Element wrObjectToElement(Document d, Object o)
	{
		String name = o.getClass().getSimpleName();
		name = name.toLowerCase();
		if(name.endsWith("model"))
			name = name.substring(0, name.length() - 5);
		
		Element e2 = e;
		e = d.createElement(name);
		
		HashMap<String, Field> fieldMap = Reflect.getDeclaredFieldMap(o);
		
		for(String k : fieldMap.keySet().toArray(new String[]{}))
		{
			Field f = fieldMap.get(k);
			Class<?> type = f.getType();
			
			if(type != HashMap.class && type != ArrayList.class)
			{
				Method writer = Reflect.getMethod(XML.class, "wr" + f.getType().getSimpleName(), String.class, f.getType());
				Reflect.invoke(writer, null, f.getName(), Reflect.getFieldValue(f, o));
				fieldMap.remove(k);
			}
		}
		
		for(String k : fieldMap.keySet().toArray(new String[]{}))
		{
			Field f = fieldMap.get(k);
			Class<?> type = f.getType();
			
			if(type != HashMap.class && type != ArrayList.class)
				continue;
			
			Element listElement = wrListElement(d, o, f);
			fieldMap.remove(k);
			e.appendChild(listElement);
		}
		
		Element toReturn = e;
		e = e2;
		return toReturn;
	}
	
	public static Element wrListElement(Document d, Object o, Field f)
	{
		Element top = d.createElement(f.getName());
		
		Collection<?> c = null;
		if(f.getType() == ArrayList.class)
			c = (ArrayList<?>) Reflect.getFieldValue(f, o);
		else if(f.getType() == HashMap.class)
			c = ((HashMap<?, ?>) Reflect.getFieldValue(f, o)).values();
		
		if(c != null)
		{
			for(Object item : c)
			{
				Element toAdd = wrObjectToElement(d, item);
				top.appendChild(toAdd);
			}
		}
		
		return top;
	}
	
	public static Object rObjectFromFile(String url, Class<?> cls)
	{
		saveState();
		setFile(url);
		
		Object toReturn = rObjectFromElement(cls);
		
		restoreState();
		
		return toReturn;
	}
	
	/*
	 * Read an object from an element.
	 * Basic fields are read from the object representing tags attributes.
	 * HashMap<?, ?> is read from an element with a list of subelements, with the "id" field being used as a storage key
	 * ArrayList<?> is read from an element with a list of subelements
	 */
	public static Object rObjectFromElement(Class<?> cls)
	{
		Object o = Reflect.newInstance(cls);
		
		HashMap<String, Field> fieldMap = Reflect.getDeclaredFieldMap(cls);
		
		for(String k : fieldMap.keySet().toArray(new String[]{}))
		{
			Field f = fieldMap.get(k);
			Class<?> type = f.getType();
			
			if(type != HashMap.class && type != ArrayList.class)
			{
				Method reader = Reflect.getMethod(XML.class, "r" + type.getSimpleName(), String.class);
				Object value = Reflect.invoke(reader, null, k);
				Reflect.setField(f, o, value);
				fieldMap.remove(k);
			}
		}
		
		if(!fieldMap.isEmpty())
		{
			XML.descend();
			while(XML.hasNext())
			{
				XML.next();
				Field f = fieldMap.get(e.getTagName());
				if(f == null)
					continue;
				Class<?> type = f.getType();
				if(type != HashMap.class && type != ArrayList.class)
					continue;
				if(type == HashMap.class)
				{
					Class<?>[] classes = Reflect.getGenericTypes(f);
					Object value = rHashMap(classes[0], classes[1]);
					Reflect.setField(f, o, value);
					fieldMap.remove(e.getTagName());
				}
				else if(type == ArrayList.class)
				{
					Class<?>[] classes = Reflect.getGenericTypes(f);
					Object value = rArrayList(classes[0]);
					Reflect.setField(f, o, value);
					fieldMap.remove(e.getTagName());
				}
			}
			XML.ascend();
		}
		
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public static <T, U> HashMap<T, U> rHashMap(Class<?> keyCls, Class<?> valCls)
	{
		HashMap<T, U> toReturn = new HashMap<T, U>();
		
		Method keyReader = Reflect.getMethod(XML.class, "r" + keyCls.getSimpleName(), String.class);
		
		XML.descend();
		while(XML.hasNext())
		{
			XML.next();
			T keyValue = (T) Reflect.invoke(keyReader, null, "id");
			U value = (U) rObjectFromElement(valCls);
			toReturn.put(keyValue, value);
		}
		XML.ascend();
		
		return toReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> rArrayList(Class<?> gen)
	{
		ArrayList<T> toReturn = new ArrayList<T>();
		
		XML.descend();
		while(XML.hasNext())
		{
			XML.next();
			T val = (T) rObjectFromElement(gen);
			toReturn.add(val);
		}
		XML.ascend();
		
		return toReturn;
	}
}
