package dialog.geom;

class RatioPoint
{
	public var xv:Int;
	public var xp:Float;
	
	public var yv:Int;
	public var yp:Float;
	
	public function new(xv:Int, xp:Float, yv:Int, yp:Float)
	{
		this.xv = xv;
		this.xp = xp;
		
		this.yv = yv;
		this.yp = yp;
	}
	
	public function clone():RatioPoint
	{
		return new RatioPoint(xv, xp, yv, yp);
	}
	
	public function toString():String
	{
		return "(" + xp + "% " + xv + ", " + yp + "% " + yv + ")";
	}

	public static function fromString(s:String):RatioPoint
	{
		if(s == "")
			return new RatioPoint(0, 0, 0, 0);
		
		var sa:Array<String> = s.substring(1,s.length - 1).split(",");
		
		if(sa.length == 1)
			sa.push("");
		
		var x:RatioInt = RatioInt.fromString(sa[0]);
		var y:RatioInt = RatioInt.fromString(sa[1]);
		
		return new RatioPoint(x.v, x.p, y.v, y.p);
	}
}