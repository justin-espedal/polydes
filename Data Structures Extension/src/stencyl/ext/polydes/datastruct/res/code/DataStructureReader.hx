/**
 * @author Justin Espedal
 */
package scripts.ds;

import scripts.DataStructures;

[IMPORTS]
class DataStructureReader
{
	public static function readData():Void
	{
		var files:Array<String> = getFileLines("MyDataStructures.txt");
		
		var classmap:Map<String, Class<Dynamic>> = new Map<String, Class<Dynamic>>();
[CLASSMAP]
[DATATYPES]
		var fmaps:Map<String, Map<String, String>> = new Map<String, Map<String, String>>();
		for(fname in files)
		{
			if(fname == "")
				continue;
			
			var map:Map<String, String> = getFileKeyValues(fname);
			fmaps.set(fname, map);
			
			var name:String = fname.split("/").pop();
			var id:Int = Std.parseInt(map.get("struct_id"));
			var type:String = map.get("struct_type");
			map.remove("struct_type");
			
			var cls:Class<Dynamic> = classmap.get(type);
			
			var structure:Dynamic = Type.createInstance(cls, []);
			structure.id = id;
			structure.name = name;
			DataStructures.idMap.set(id, structure);
		}
		for(fname in files)
		{
			if(fname == "")
				continue;
			
			var map:Map<String, String> = fmaps.get(fname);
			var data:Dynamic = DataStructures.idMap.get(Std.parseInt(map.get("struct_id")));
			map.remove("struct_id");
			for(key in map.keys())
			{
				if(key == null || key == "")
					continue;
				try
				{
					Reflect.setProperty(data, key, StringData.read(map.get(key), data.getTypeInfo(key)));
				}
				catch(e:Dynamic)
				{
					trace("Warning: Couldn't load data.");
					trace("- " + fname + "::" + key);
				}
			}
			
			DataStructures.nameMap.set(data.name, data);
		}
	}
	
	public static function read(id:Int, path:String):Void
	{
		
	}

	public static var newlinePattern:EReg = ~/[\r\n]+/g;

	public static function getFileLines(filename:String):Array<String>
	{
		return newlinePattern.split(nme.Assets.getText("assets/data/"+ filename));
	}

	public static function getFileKeyValues(filename:String):Map<String,String>
	{
		var lines:Array<String> = getFileLines("stencyl.ext.polydes.datastruct/data/" + filename);
		var map:Map<String, String> = new Map<String, String>();
		for(line in lines)
		{
			var parts:Array<String> = line.split("=");
			map.set(parts[0], parts[1]);
		}
		return map;
	}
}