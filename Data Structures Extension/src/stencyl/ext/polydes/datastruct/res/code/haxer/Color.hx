	public static function rColor(s:String):Int
	{
		if(s.substring(0, 2) == "0x")
			return Std.parseInt(s);
		else if(s.substring(0, 1) == "#")
			return Std.parseInt("0x" + s.substr(1));
		else
			return Std.parseInt("0x" + s);
	}