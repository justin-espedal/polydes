package dialog.geom;

class Insets
{
	public var top:Float;
	public var right:Float;
	public var bottom:Float;
	public var left:Float;

	public function new(top:Float, right:Float, bottom:Float, left:Float)
	{
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	public function toString():String
	{
		return '[$top, $right, $bottom, $left]';
	}
}