package com.polydes.datastruct;

using Lambda;

abstract Set<T>(Array<T>)
{
	inline public function new()
	{
		this = [];
	}

	public function has(e:T):Bool
	{
		return a.has(e);
	}

	public function set(e:T):Void
	{
		if(!a.has(e))
			a.push(e);
	}

	public static function fromString(s:String):Set<Dynamic>
	{
		var set:Set<Dynamic> = new Set<Dynamic>();
		var i:Int = s.indexOf(":");
		if(i == -1)
			return set;
		var type:String = s.substring(i + 1);
		
		for(s2 in s.substring(1, i-1).split(","))
			set.set(StringData.read(s2, type));
		
		return set;
	}
}