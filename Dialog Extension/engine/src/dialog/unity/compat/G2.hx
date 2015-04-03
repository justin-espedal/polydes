package dialog.unity.compat;

import dialog.core.*;
import unityengine.*;

using dialog.unity.extension.VectorUtil;
using dialog.unity.extension.RectUtil;

class G2
{
	public static inline function drawImage(img:Texture2D, x:Float, y:Float, ?scale:Bool = false, ?scaleXY:Bool = false):Void
	{
		Graphics.DrawTexture(new Rect(x, y, img.width, img.height), img);
	}

	public static inline function s(val:Float):Int
	{
		return Std.int(val);
	}

	public static inline function sr(val:Rect):Rect
	{
		return val.clone();
	}

	public static inline function sp(val:Vector2):Vector2
	{
		return val.clone();
	}

	public static inline function us(val:Float):Int
	{
		return Std.int(val);
	}

	public static inline function usr(val:Rect):Rect
	{
		return val.clone();
	}

	public static inline function usp(val:Vector2):Vector2
	{
		return val.clone();
	}
}
