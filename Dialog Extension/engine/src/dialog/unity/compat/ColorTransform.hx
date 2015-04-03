package dialog.unity.compat;

// Openfl code

class ColorTransform
{
	public var alphaMultiplier:Float;
	public var alphaOffset:Float;

	public var blueMultiplier:Float;
	public var blueOffset:Float;

	public var color (get, set):Int;

	public var greenMultiplier:Float;
	public var greenOffset:Float;

	public var redMultiplier:Float;
	public var redOffset:Float;

	public function new (
    redMultiplier:Float = 1, greenMultiplier:Float = 1, blueMultiplier:Float = 1, alphaMultiplier:Float = 1,
    redOffset:Float = 0, greenOffset:Float = 0, blueOffset:Float = 0, alphaOffset:Float = 0)
  {
		this.redMultiplier = redMultiplier;
		this.greenMultiplier = greenMultiplier;
		this.blueMultiplier = blueMultiplier;
		this.alphaMultiplier = alphaMultiplier;
		this.redOffset = redOffset;
		this.greenOffset = greenOffset;
		this.blueOffset = blueOffset;
		this.alphaOffset = alphaOffset;
	}

	@:noCompletion private function get_color ():Int
  {
		return ((Std.int (redOffset) << 16) | (Std.int (greenOffset) << 8) | Std.int (blueOffset));
	}

	@:noCompletion private function set_color (value:Int):Int
  {
		redOffset = (value >> 16) & 0xFF;
		greenOffset = (value >> 8) & 0xFF;
		blueOffset = value & 0xFF;

		redMultiplier = 0;
		greenMultiplier = 0;
		blueMultiplier = 0;

		return color;
	}
}
