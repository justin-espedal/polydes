package dialog.unity.compat;

import unityengine.*;

class GlobalMap
{
	public static var map:Map<String, Dynamic> = new Map<String, Dynamic>();

	public static function get(name:String):Dynamic
	{
		return map.get(name);
	}

	public static function set(name:String, object:Dynamic):Void
	{
		map.set(name, object);
	}
}
