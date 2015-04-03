package dialog.legacy;

#if stencyl

import nme.display.BitmapData;
import nme.geom.Point;
import nme.geom.Rectangle;

#elseif unity

import unityengine.*;
import dialog.unity.compat.*;
import dialog.unity.compat.Typedefs;

#end

import dialog.core.*;

using dialog.util.BitmapDataUtil;

class AnimatedImage
{
	private static var animInfo:Map<String, AnimationInfo>;

	public static function getAnimInfo(name:String):AnimationInfo
	{
		if(animInfo == null)
			animInfo = new Map<String, AnimationInfo>();

		if(!animInfo.exists(name))
			animInfo.set(name, new AnimationInfo(name));

		return animInfo.get(name);
	}

	public var info:AnimationInfo;
	public var repeats:Bool;
	public var elapsed:Int;
	public var curFrame:Int;
	public var numFrames:Int;
	public var durations:Array<Int>;
	public var sheet:BitmapData;

	public var done:Bool;

	public var curFrameImg:BitmapData;

	public function new(name:String)
	{
		info = getAnimInfo(name);
		repeats = info.repeats;
		elapsed = 0;
		curFrame = 0;
		numFrames = info.frameCount;
		durations = info.durations;
		sheet = info.sheet;

		curFrameImg = copyFrame(0);
	}

	public function start(dg:DialogBox):Void
	{
		dg.addAnimation(this);
	}

	public function end(dg:DialogBox):Void
	{
		dg.removeAnimation(this);
	}

	public function draw(x:Int, y:Int):Void
	{
		G2.drawImage(curFrameImg, x, y);
	}

	public function update():Void
	{
		if(done)
			return;

		elapsed += Engine.STEP_SIZE;
		if(elapsed >= durations[curFrame])
		{
			++curFrame;
			elapsed = 0;
			if(curFrame >= numFrames)
			{
				if(!repeats)
				{
					--curFrame;
					done = true;
				}
				else
					curFrame = 0;
			}

			curFrameImg = copyFrame(curFrame);
		}
	}

	private static var zeroPoint:Point = new Point(0, 0);

	public function copyFrame(frame:Int):BitmapData
	{
		//FIXME
		var img:BitmapData = BitmapDataUtil.newTransparentImg(info.width, info.height);
		img.copyPixels(sheet, new Rectangle(info.width * frame, 0, info.width, info.height), zeroPoint);//, null, null, true);
		return img;
	}
}
