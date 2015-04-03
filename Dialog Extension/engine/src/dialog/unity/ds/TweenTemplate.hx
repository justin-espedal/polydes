package dialog.unity.ds;

import unityengine.*;

@:build(dialog.unity.EditorBuilder.markForMenuGeneration())
class TweenTemplate extends ScriptableObject
{
	public var duration:Int;
	public var positionStart:Null<RatioPoint>;
	public var positionStop:Null<RatioPoint>;
	public var scaleStart:Null<Vector2>;
	public var scaleStop:Null<Vector2>;
	public var opacityStart:Null<Float>;
	public var opacityStop:Null<Float>;
}
