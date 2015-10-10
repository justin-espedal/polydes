	public static function rSet(s:String):de.polygonal.ds.ListSet<Dynamic>
	{
		var set:de.polygonal.ds.ListSet<Dynamic> = new de.polygonal.ds.ListSet<Dynamic>();
		var i:Int = s.indexOf(":");
		if(i == -1)
			return set;
		var type:String = s.substring(i + 1);
		
		for(s2 in s.substring(1, i-1).split(","))
			set.set(StringData.read(s2, type));
		
		return set;
	}