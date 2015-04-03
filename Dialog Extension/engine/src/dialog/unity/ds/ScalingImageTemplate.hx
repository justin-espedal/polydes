package dialog.unity.ds;

import unityengine.*;

@:build(dialog.unity.EditorBuilder.markForMenuGeneration())
class ScalingImageTemplate extends ScriptableObject
{
	public var image:Texture2D;
	public var origin:RatioPoint;
	public var type:String;
	public var part:String;
	public var border:Vector2;
}
