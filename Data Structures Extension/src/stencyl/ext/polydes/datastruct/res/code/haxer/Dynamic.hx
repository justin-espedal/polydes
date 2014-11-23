	public static function rDynamic(s:String):Dynamic
	{
		var i:Int = s.indexOf(":");
		if(i == -1)
			return null;
		
		var value:String = s.substring(0, i);
		var type:String = s.substring(i + 1);
		return StringData.read(value, type);
	}