package stencyl.ext.polydes.paint.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stencyl.core.api.Reference;
import stencyl.core.lib.Resource;
import stencyl.core.lib.io.AbstractReader;
import stencyl.core.lib.io.IOHelper;
import stencyl.sw.util.FileHelper;

//http://www.angelcode.com/products/bmfont/doc/file_format.html
public class BitmapFont extends LinkedDataItem
{
	//font
	public String face;
	public int size;
	public boolean bold;
	public boolean italic;
	public String charset;
	public boolean unicode;
	public int stretchH;
	public boolean smooth;
	public int aa;
	public int[] padding;
	public int[] spacing;
	public int outline;
	
	//common
	public int lineHeight;
	public int base;
	public int scaleW;
	public int scaleH;
	public int pages;
	public boolean packed;
	public int alphaChnl;
	public int redChnl;
	public int greenChnl;
	public int blueChnl;
	
	//page
	public HashMap<Integer, String> pageFiles;
	public HashMap<Integer, BufferedImage> pageImages;
	
	//chars
	public ArrayList<BitmapGlyph> chars;
	
	//kerning
	public ArrayList<KerningPair> kerningPairs;
	
	public class KerningPair
	{
		public int first;
		public int second;
		public int amount;
	}
	
	public BitmapFont(String name)
	{
		super(name);
	}
	
	public void loadFromFile(File loc)
	{
		Element root;
		try
		{
			root = IOHelper.readXMLFromFile(loc);
		}
		catch (IOException e3)
		{
			e3.printStackTrace();
			return;
		}
		
		for(int i = 0; i < root.getChildNodes().getLength(); ++i)
		{
			Node node = root.getChildNodes().item(i);
			
			if(!(node instanceof Element))
				continue;
			
			Element e = (Element) node;
			
			String name = node.getNodeName();
			if(name.equals("info"))
			{
				face = R.readString(e, "face");
				size = R.readInt(e, "size");
				bold = R.readInt(e, "bold") > 0;
				italic = R.readInt(e, "italic") > 0;
				charset = R.readString(e, "charset");
				unicode = R.readInt(e, "unicode") > 0;
				stretchH = R.readInt(e, "stretchH");
				smooth = R.readInt(e, "smooth") > 0;
				aa = R.readInt(e, "aa");
				padding = R.readIntArray(e, "padding", 4);
				spacing = R.readIntArray(e, "spacing", 2);
				outline = R.readInt(e, "outline"); 
			}
			else if(name.equals("common"))
			{
				lineHeight = R.readInt(e, "lineHeight");
				base = R.readInt(e, "base");
				scaleW = R.readInt(e, "scaleW");
				scaleH = R.readInt(e, "scaleH");
				pages = R.readInt(e, "pages");
				packed = R.readInt(e, "packed") > 0;
				if(packed)
				{
					alphaChnl = R.readInt(e, "alphaChnl");
					redChnl = R.readInt(e, "redChnl");
					greenChnl = R.readInt(e, "greenChnl");
					blueChnl = R.readInt(e, "blueChnl");
				}
			}
			else if(name.equals("pages"))
			{
				pageFiles = new HashMap<Integer, String>();
				pageImages = new HashMap<Integer, BufferedImage>();
				
				NodeList nl = e.getElementsByTagName("page");
				
				File parent = loc.getParentFile();
				
				for(int j = 0; j < nl.getLength(); ++j)
				{
					Element e2 = (Element) nl.item(j);
					Integer id = (Integer) R.readInt(e2, "id");
					String filename = R.readString(e2, "file");
					pageFiles.put(id, filename);
					
					try
					{
						pageImages.put(id, ImageIO.read(new File(parent, filename)));
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
			else if(name.equals("chars"))
			{
				NodeList nl = e.getElementsByTagName("char");
				chars = new ArrayList<BitmapGlyph>(nl.getLength());
				
				for(int j = 0; j < nl.getLength(); ++j)
				{
					Node n2 = nl.item(j);
					
					Element e2 = (Element) n2;
					chars.add(readGlyph(e2));
				}
			}
			else if(name.equals("kernings"))
			{
				NodeList nl = e.getElementsByTagName("kerning");
				kerningPairs = new ArrayList<KerningPair>(nl.getLength());
				
				for(int j = 0; j < nl.getLength(); ++j)
				{
					Node n2 = nl.item(j);
					
					Element e2 = (Element) n2;
					KerningPair p = new KerningPair();
					p.first = R.readInt(e2, "first");
					p.second = R.readInt(e2, "second");
					p.amount = R.readInt(e2, "amount");
					kerningPairs.add(p);
				}
			}
		}
	}
	
	public void saveToFile(File loc)
	{
		try
		{
			Document document = FileHelper.newDocument();
			
			Element root = document.createElement("font");
			
			Element infoNode = document.createElement("info");
			infoNode.setAttribute("face", face);
			infoNode.setAttribute("size", "" + size);
			infoNode.setAttribute("bold", bold ? "1" : "0");
			infoNode.setAttribute("italic", italic ? "1" : "0");
			infoNode.setAttribute("charset", charset);
			infoNode.setAttribute("unicode", unicode ? "1" : "0");
			infoNode.setAttribute("stretchH", "" + stretchH);
			infoNode.setAttribute("smooth", smooth ? "1" : "0");
			infoNode.setAttribute("aa", "" + aa);
			infoNode.setAttribute("padding", join(padding));
			infoNode.setAttribute("spacing", join(spacing));
			infoNode.setAttribute("outline", "" + outline);
			root.appendChild(infoNode);
			
			Element commonNode = document.createElement("common");
			commonNode.setAttribute("lineHeight", "" + lineHeight);
			commonNode.setAttribute("base", "" + base);
			commonNode.setAttribute("scaleW", "" + scaleW);
			commonNode.setAttribute("scaleH", "" + scaleH);
			commonNode.setAttribute("pages", "" + pages);
			commonNode.setAttribute("packed", packed ? "1" : "0");
			if(packed)
			{
				commonNode.setAttribute("alphaChnl", "" + alphaChnl);
				commonNode.setAttribute("redChnl", "" + redChnl);
				commonNode.setAttribute("greenChnl", "" + greenChnl);
				commonNode.setAttribute("blueChnl", "" + blueChnl);
			}
			root.appendChild(commonNode);
			
			Element pagesNode = document.createElement("pages");
			for(Integer i : pageFiles.keySet())
			{
				Element e = document.createElement("page");
				e.setAttribute("id", "" + i);
				e.setAttribute("file", pageFiles.get(i));
				pagesNode.appendChild(e);
				
				String filename = new File(loc.getParentFile(), pageFiles.get(i)).getAbsolutePath();
				System.out.println(filename);
				try
				{
					FileHelper.writeToPNG(filename, pageImages.get(i));
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			root.appendChild(pagesNode);
			
			Element charsNode = document.createElement("chars");
			charsNode.setAttribute("count", "" + chars.size());
			for(BitmapGlyph g : chars)
			{
				Element e = document.createElement("char");
				writeGlyph(e, g);
				charsNode.appendChild(e);
			}
			root.appendChild(charsNode);
			
			if(kerningPairs != null)
			{
				Element kerningsNode = document.createElement("kernings");
				kerningsNode.setAttribute("count", "" + kerningPairs.size());
				for(KerningPair p : kerningPairs)
				{
					Element e = document.createElement("kerning");
					e.setAttribute("first", "" + p.first);
					e.setAttribute("second", "" + p.second);
					e.setAttribute("amount", "" + p.amount);
					kerningsNode.appendChild(e);
				}
				root.appendChild(kerningsNode);
			}
			
			document.appendChild(root);
		
			FileHelper.writeXMLToFile(document, loc.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public BitmapGlyph readGlyph(Element e)
	{
		BitmapGlyph g = new BitmapGlyph();
		g.font = this;
		g.id = R.readInt(e, "id");
		g.x = R.readInt(e, "x");
		g.y = R.readInt(e, "y");
		g.width = R.readInt(e, "width");
		g.height = R.readInt(e, "height");
		g.xoffset = R.readInt(e, "xoffset");
		g.yoffset = R.readInt(e, "yoffset");
		g.xadvance = R.readInt(e, "xadvance");
		g.page = R.readInt(e, "page");
		if(g.page == -1)
			g.page = 0;
		g.chnl = R.readInt(e, "chnl");
		if(g.chnl == -1)
			g.chnl = 15;
		
		g.updateRect();
		
		return g;
	}
	
	public void writeGlyph(Element e, BitmapGlyph g)
	{
		e.setAttribute("id", "" + g.id);
		e.setAttribute("x", "" + g.x);
		e.setAttribute("y", "" + g.y);
		e.setAttribute("width", "" + g.width);
		e.setAttribute("height", "" + g.height);
		e.setAttribute("xoffset", "" + g.xoffset);
		e.setAttribute("yoffset", "" + g.yoffset);
		e.setAttribute("xadvance", "" + g.xadvance);
		e.setAttribute("page", "" + g.page);
		e.setAttribute("chnl", "" + g.chnl);
	}
	
	class R extends AbstractReader
	{
		@Override
		public boolean accepts(String arg0)
		{
			return false;
		}

		@Override
		public Resource read(Reference arg0, String arg1, String arg2, Element arg3, HashMap<Integer, Integer> arg4)
		{
			return null;
		}
	}
	
	public static String join(int[] a)
	{
		String toReturn = "";
		
		for(int i = 0; i < a.length; ++i)
		{
			toReturn += a[i];
			
			if(i + 1 < a.length)
			{
				toReturn += ",";
			}
		}
		
		return toReturn;
	}
	
	public static void p(Object o)
	{
		System.out.println(o);
	}
}