package com.polydes.datastruct.data.core;

public class HaxeObjectDefinition
{
	public String haxeClass;
	public HaxeField[] fields;
	public String haxereaderExpression;
	public boolean showLabels;
	
	public HaxeObjectDefinition(String haxeClass, HaxeField[] fields)
	{
		this.haxeClass = haxeClass;
		this.fields = fields;
		
		haxereaderExpression = null;
	}
}
