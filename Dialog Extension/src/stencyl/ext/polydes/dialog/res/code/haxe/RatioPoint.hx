package scripts.ds.dialog;

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
}