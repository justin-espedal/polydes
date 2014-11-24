	public static function rRatioInt(s:String):scripts.ds.dialog.RatioInt
	{
		if(s == "")
			return new scripts.ds.dialog.RatioInt(0, 0);
		
		var sa:Array<String> = s.split("");
		while(sa.remove(" ")){}
		s = sa.join("");
		sa = s.split("%");
		
		var v:Int = 0;
		var p:Float = 0;
		if(sa.length == 1)
			v = Std.parseInt(sa[0]);
		else
		{
			v = Std.parseInt(sa[1]);
			p = Std.parseInt(sa[0]) / 100;
		}
		
		return new scripts.ds.dialog.RatioInt(v, p);
	}
	
	