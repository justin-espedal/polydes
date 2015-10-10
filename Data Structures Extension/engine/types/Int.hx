	public static function rInt(s:String):Int
	{
		if(Math.isNaN(Std.parseFloat(s)))
			return 0;

		return Std.parseInt(s);
	}