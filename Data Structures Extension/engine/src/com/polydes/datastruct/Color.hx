package com.polydes.datastruct;

abstract Color(Int)
{
	inline public function new(i:Int)
	{
		this = i;
	}

	public static function fromString(s:String):Color
	{
		if(s.substring(0, 2) == "0x")
			return Std.parseInt(s);
		else if(s.substring(0, 1) == "#")
			return Std.parseInt("0x" + s.substr(1));
		else
			return Std.parseInt("0x" + s);
	}
}