package dialog.geom;

class RatioInt
{
	public var v:Int;
	public var p:Float;
	
	public function new(v:Int, p:Float)
	{
		this.v = v;
		this.p = p;
	}
	
	public function clone():RatioInt
	{
		return new RatioInt(v, p);
	}

	public static function fromString(s:String):RatioInt
	{
		if(s == "")
			return new RatioInt(0, 0);
		
		var sa:Array<String> = s.substring(1,s.length - 1).split("");
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
		
		return new RatioInt(v, p);
	}

	public function toString():String
	{
		return '${p * 100}% + $v';
	}
}