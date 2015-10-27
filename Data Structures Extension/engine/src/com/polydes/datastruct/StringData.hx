/**
 * @author Justin Espedal
 */
package com.polydes.datastruct;

using Lambda;

class StringData
{
	public static var readers:Map<String, String->Dynamic> = new Map<String, String->Dynamic>();

	public static function registerReader(type:String, reader:String->Dynamic):Void
	{
		readers.set(type, reader);
	}

	public static function registerBasicReaders():Void
	{
		//TODO: Map would be good to add.
		registerReader("Array", readArray);
		registerReader("Bool", readBool);
		registerReader("Dynamic", readDynamic);
		registerReader("Float", readFloat);
		registerReader("Int", readInt);
		registerReader("String", readString);

		registerReader("com.polydes.datastruct.Color", Color.fromString);
		registerReader("com.polydes.datastruct.Control", Control.fromString);
		registerReader("com.polydes.datastruct.ExtrasImage", ExtrasImage.fromString);
		registerReader("com.polydes.datastruct.Selection", Selection.fromString);
		registerReader("com.polydes.datastruct.Set", Set.fromString);

		#if stencyl

		var stencylTypeReader = function(s) {
			if(s == "")
				return null;

			return com.stencyl.Data.get().resources.get(Std.parseInt(s));
		};

		for(type in ["actor.ActorType", "Background", "Font", "Sound", "scene.Tileset"])
		{
			registerReader('com.stencyl.models.$type', stencylTypeReader);
		}

		#end
	}

	public static function registerHaxeObjectReader(type:String, argTypes:Array<String>):Void
	{
		var cls = Type.resolveClass(type);

		registerReader(type, function(s) {
			return Type.createInstance(cls, parseArgs(s, argTypes));
		});
	}

	public static function parseArgs(s:String, types:Array<String>):Array<Dynamic>
	{
		var a:Array<Dynamic> = [];
		
		var char:String;
		var k:Int = 0;
		var commas:Array<Int> = [];
		for(j in 1...s.length)
		{
			char = s.charAt(j);
			if(char == "[")
				++k;
			else if(char == "]")
				--k;
			else if(char == "," && k == 0)
				commas.push(j);
		}
		
		var curArg = 0;

		var lastComma:Int = 0;
		for(comma in commas)
		{
			a.push(StringData.read(s.substring(lastComma + 1, comma), types[curArg++]));
			lastComma = comma;
		}
		a.push(StringData.read(s.substring(lastComma + 1, s.length - 1), types[curArg++]));
		
		return a;
	}

	public static function registerStructureReader(type:String):Void
	{
		registerReader(type, function(s) {
			if(s == "")
				return null;

			return DataStructures.idMap.get(Std.parseInt(s));
		});
	}

	public static function read(s:String, type:String):Dynamic
	{
		if(readers.exists(type))
			return readers.get(type)(s);

		trace('Error: No reader registered for type "$type"');

		return null;

		//var rt = Type.resolveClass(type);
		//return Reflect.callMethod(rt, Reflect.field(rt, "fromString"), [s]);
	}

	public static function getInts(s:String):Array<Int>
	{
		if(s.length == 0)
			return null;

		var splitString:Array<String> = s.substring(1,s.length - 1).split(",");
		var toReturn:Array<Int> = [];
		for(sub in splitString)
			toReturn.push(readInt(sub));
		return toReturn;
	}

	public static function readArray(s:String):Array<Dynamic>
	{
		var a:Array<Dynamic> = [];
		
		var i:Int = s.lastIndexOf(":");
		var type:String = s.substring(i + 1);
		
		var char:String;
		var k:Int = 0;
		var commas:Array<Int> = [];
		for(j in 1...i)
		{
			char = s.charAt(j);
			if(char == "[")
				++k;
			else if(char == "]")
				--k;
			else if(char == "," && k == 0)
				commas.push(j);
		}
		
		var lastComma:Int = 0;
		for(comma in commas)
		{
			a.push(StringData.read(s.substring(lastComma + 1, comma), type));
			lastComma = comma;
		}
		a.push(StringData.read(s.substring(lastComma + 1, i - 1), type));
		
		return a;
	}

	public static function readBool(s:String):Bool
	{
		return s == "true";
	}

	public static function readDynamic(s:String):Dynamic
	{
		var i:Int = s.indexOf(":");
		if(i == -1)
			return null;
		
		var value:String = s.substring(0, i);
		var type:String = s.substring(i + 1);
		return StringData.read(value, type);
	}

	public static function readFloat(s:String):Float
	{
		if(Math.isNaN(Std.parseFloat(s)))
			return 0;

		return Std.parseFloat(s);
	}

	public static function readInt(s:String):Int
	{
		if(Math.isNaN(Std.parseFloat(s)))
			return 0;

		return Std.parseInt(s);
	}

	public static function readString(s:String):String
	{
		return s;
	}
}
