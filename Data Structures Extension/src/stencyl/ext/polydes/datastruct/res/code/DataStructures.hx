/**
 * @author Justin Espedal
 */
package scripts;

import scripts.ds.DataStructureReader;

class DataStructures
{
	private static var init:Bool = false;
	
	public static function get(s:String):Dynamic
	{
		if(!init)
		{
			DataStructureReader.readData();
			init = true;
		}
		
		return nameMap.get(s);
	}
	
	public static function getByID(i:Int):Dynamic
	{
		if(!init)
		{
			DataStructureReader.readData();
			init = true;
		}
		
		return idMap.get(i);
	}
	
	public static var idMap:Map<Int, Dynamic> = new Map<Int, Dynamic>();
	public static var nameMap:Map<String, Dynamic> = new Map<String, Dynamic>();
	public static var types:Map<String, String> = new Map<String, String>();
}