class IntPoint
{
	public var x:Int;
	public var y:Int;

	public function new(x:Float, y:Float)
	{
		this.x = Std.int(x);
		this.y = Std.int(y);
	}

	public function add(to:IntPoint):IntPoint
	{
		return new IntPoint(x + to.x, y + to.y);
	}

	public function toString():String
	{
		return "(" + x + ", " + y + ")";
	}
}