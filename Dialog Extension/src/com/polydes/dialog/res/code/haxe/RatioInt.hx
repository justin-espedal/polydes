package scripts.ds.dialog;

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
}