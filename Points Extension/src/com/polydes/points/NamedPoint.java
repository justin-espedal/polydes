package com.polydes.points;

import java.awt.Point;

public class NamedPoint extends Point
{
	boolean pointName;
	String name;
	
	public NamedPoint(Point p)
	{
		this(encodePoint(p), p);
	}
	
	public NamedPoint(String name, Point p)
	{
		super(p);
		this.name = name;
		pointName = name.equals(encodePoint(p));
	}
	
	public static NamedPoint fromKeyValue(String[] s)
	{
		if(s.length < 2)
			return null;
		
		return new NamedPoint(s[0], decodePoint(s[1]));
	}
	
	public static String toKeyValue(NamedPoint p)
	{
		return p.name + "=" + encodePoint(p);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		pointName = name.equals(encodePoint(this));
	}
	
	@Override
	public void setLocation(int x, int y)
	{
		super.setLocation(x, y);
		if(pointName)
			name = encodePoint(getLocation());
	}
	
	public static Point decodePoint(String s)
	{
		int[] ints = getInts(s);
		if(ints == null)
			return new Point(0, 0);
		if(ints.length == 1)
			return new Point(ints[0], 0);
		
		return new Point(ints[0], ints[1]);
	}

	public static String encodePoint(Point p)
	{
		return "[" + p.x + "," + p.y + "]";
	}
	
	public static int[] getInts(String fromString)
	{
		if(fromString.length() == 0)
			return null;
		
		String[] splitString = fromString.substring(1, fromString.length() - 1).split(",");
		
		int[] toReturn = new int[splitString.length];
		for(int i = 0; i < splitString.length; ++i)
		{
			try
			{
				toReturn[i] = Integer.parseInt(splitString[i].trim());
			}
			catch(NumberFormatException ex)
			{
				toReturn[i] = 0;
			}
		}
		return toReturn;
	}
}