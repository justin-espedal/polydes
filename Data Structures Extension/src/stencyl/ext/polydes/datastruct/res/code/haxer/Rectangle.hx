	public static function rRectangle(s:String):nme.geom.Rectangle
	{
		var ints:Array<Int> = getInts(s);
		if(ints == null)
			return new nme.geom.Rectangle(0, 0, 0, 0);
		
		return new nme.geom.Rectangle(ints[0], ints[1], ints[2], ints[3]);
	}