	public static function rFloat(s:String):Float
	{
		if(Math.isNaN(Std.parseFloat(s)))
			return 0;

		return Std.parseFloat(s);
	}