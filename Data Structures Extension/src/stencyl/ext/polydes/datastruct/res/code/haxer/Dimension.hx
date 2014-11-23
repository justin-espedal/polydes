	public static function rDimension(s:String):nme.geom.Point
	{
		var ints:Array<Int> = getInts(s);
		if(ints == null)
			return new nme.geom.Point(0, 0);
		
		return new nme.geom.Point(ints[0], ints[1]);
	}