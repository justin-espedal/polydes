	public static function rRatioPoint(s:String):scripts.ds.dialog.RatioPoint
	{
		if(s == "")
			return new scripts.ds.dialog.RatioPoint(0, 0, 0, 0);
		
		var sa:Array<String> = s.substring(1,s.length - 1).split(",");
		
		if(sa.length == 1)
			sa.push("");
		
		var x:scripts.ds.dialog.RatioInt = rRatioInt(sa[0]);
		var y:scripts.ds.dialog.RatioInt = rRatioInt(sa[1]);
		
		return new scripts.ds.dialog.RatioPoint(x.v, x.p, y.v, y.p);
	}