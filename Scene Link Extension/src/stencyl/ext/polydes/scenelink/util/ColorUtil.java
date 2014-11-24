package stencyl.ext.polydes.scenelink.util;

import java.awt.Color;

public class ColorUtil
{
	public static Color decode(String cs)
	{
		if(cs.startsWith("#"))
			cs = cs.substring(1);
		else if(cs.startsWith("0x"))
			cs = cs.substring(2);
		
		if(cs.length() < 6)
			return new Color(255, 255, 255, 255);;
		
		boolean hasAlpha = cs.length() == 8;
		int rgbOffset = hasAlpha ? 2 : 0;
		
		int a = hasAlpha ? Integer.parseInt(cs.substring(0, 2), 16) : 255;
		int r = Integer.parseInt(cs.substring(0 + rgbOffset, 2 + rgbOffset), 16);
		int g = Integer.parseInt(cs.substring(2 + rgbOffset, 4 + rgbOffset), 16);
		int b = Integer.parseInt(cs.substring(4 + rgbOffset, 6 + rgbOffset), 16);
		
		return new Color(r, g, b, a);
	}
	
	public static String encode(Color c)
	{
		return "#" + hex(c.getAlpha(), 2) + hex(c.getRed(), 2) + hex(c.getGreen(), 2) + hex(c.getBlue(), 2);
	}
	
	public static String hex(int i, int places)
	{
		String s = Integer.toHexString(i);
		while(s.length() < places)
			s = "0" + s;
		return s;
	}
}
