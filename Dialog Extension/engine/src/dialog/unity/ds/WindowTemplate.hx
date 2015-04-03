package dialog.unity.ds;

import unityengine.*;

@:build(dialog.unity.EditorBuilder.markForMenuGeneration())
class WindowTemplate extends ScriptableObject
{
	public var position:RatioPoint;
	public var image:ScalingImageTemplate;
	public var createTween:TweenTemplate;
	public var destroyTween:TweenTemplate;
	public var scaleWidth:String;
	public var scaleWidthSize:RatioInt;
	public var scaleHeight:String;
	public var scaleHeightSize:RatioInt;
	public var insets:Rect;
}
