/**
 * @author Justin Espedal
 */

import com.stencyl.models.actor.Animation;

import nme.display.BitmapData;
import nme.geom.Point;
import nme.geom.Rectangle;

class AnimatedImage
{
	public var anim:Animation;
	public var width:Int;
	public var height:Int;
	public var repeats:Bool;
	public var elapsed:Int;
	public var curFrame:Int;
	public var numFrames:Int;
	public var framesAcross:Int;
	public var durations:Array<Int>;
	public var sheet:BitmapData;

	public var done:Bool;

	public var curFrameImg:BitmapData;
	
	private static var loadedAnimations:Map<scripts.ds.dialog.Animation, Animation> = new Map<scripts.ds.dialog.Animation, Animation>();

	public function new(animRef:scripts.ds.dialog.Animation)
	{
		if(!loadedAnimations.exists(animRef))
			loadedAnimations.set(animRef, BitmapDataUtil.getActorTypeAnimation(animRef.actor, animRef.anim));

		anim = loadedAnimations.get(animRef);
		width = Std.int(anim.imgWidth / anim.framesAcross);
		height = Std.int(anim.imgHeight / anim.framesDown);
		repeats = anim.looping;
		elapsed = 0;
		curFrame = 0;
		numFrames = anim.frameCount;
		framesAcross = anim.framesAcross;
		durations = anim.durations;
		sheet = anim.imgData;

		curFrameImg = copyFrame(0);
	}

	public function start():Void
	{
		Dialog.get().addAnimation(this);
	}

	public function end():Void
	{
		Dialog.get().removeAnimation(this);
	}

	public function draw(x:Int, y:Int):Void
	{
		G2.drawImage(curFrameImg, x, y, false);
	}
	
	public function update():Void
	{
		if(done)
			return;

		elapsed += 10;
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
		var img:BitmapData = new BitmapData(width, height, true, 0);
		img.copyPixels(sheet, new Rectangle(Std.int(width * (frame % framesAcross)), Std.int(height * Std.int(frame / framesAcross)), width, height), zeroPoint, null, null, true);
		return img;
	}
}