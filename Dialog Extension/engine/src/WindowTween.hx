/**
 * @author Justin Espedal
 */
import com.stencyl.Engine;

import nme.display.BitmapData;

import nme.geom.Point;

import scripts.ds.dialog.RatioPoint;
import scripts.ds.dialog.TweenTemplate;

class WindowTween
{
	public var elapsed:Int;
	public var duration:Int;
	
	public var pos1:IntPointTween; //pos as if image isn't scaled
	public var scale:PointTween;
	public var opacity:FloatTween;
	
	public var srcImg:BitmapData;
	
	public function new(img:BitmapData, p:IntPoint, tween:TweenTemplate)
	{
		elapsed = 0;
		duration = tween.duration;
		
		var rp1:RatioPoint;
		var rp2:RatioPoint;

		if(tween.positionStart != null)
			rp1 = tween.positionStart.clone();
		else
			rp1 = new RatioPoint(p.x, 0, p.y, 0);
		if(tween.positionStop != null)
			rp2 = tween.positionStop.clone();
		else
			rp2 = rp1.clone();

		pos1 = new IntPointTween(Util.getScreenPos(rp1), Util.getScreenPos(rp2));

		var p1:Point;
		var p2:Point;

		if(tween.scaleStart != null)
			p1 = tween.scaleStart.clone();
		else
			p1 = new Point(1, 1);
		if(tween.scaleStop != null)
			p2 = tween.scaleStop.clone();
		else
			p2 = p1.clone();

		scale = new PointTween(p1, p2);
		
		var f1:Float;
		var f2:Float;
		
		if(tween.opacityStart != null)
			f1 = tween.opacityStart;
		else f1 = 1.0;
		if(tween.opacityStop != null)
			f2 = tween.opacityStop;
		else f2 = 1.0;
		
		opacity = new FloatTween(f1, f2);
		
		srcImg = img;
	}
	
	private function getProgressOnSlide(begin:Float, end:Float, ratio:Float):Float
	{
		return begin + (end - begin) * ratio;
	}
	
	public function update(elapsed:Int):Void
	{
		this.elapsed += elapsed;
		if(elapsed > duration)
			elapsed = duration;
	}
}

private class IntPointTween
{
	public var xStart:Float;
	public var xEnd:Float;
	public var yStart:Float;
	public var yEnd:Float;
	
	public function new(start:IntPoint, end:IntPoint)
	{
		xStart = start.x;
		xEnd = end.x;
		yStart = start.y;
		yEnd = end.y;
	}
	
	public function get(progress:Float):IntPoint
	{
		return new IntPoint(xStart + (xEnd - xStart) * progress, yStart + (yEnd - yStart) * progress);
	}
	
	public function toString():String
	{
		return xStart + ", " + yStart + "   " + xEnd + ", " + yEnd;
	}
}

private class PointTween
{
	public var xStart:Float;
	public var xEnd:Float;
	public var yStart:Float;
	public var yEnd:Float;
	
	public function new(start:Point, end:Point)
	{
		xStart = start.x;
		xEnd = end.x;
		yStart = start.y;
		yEnd = end.y;
	}
	
	public function get(progress:Float):Point
	{
		return new Point(xStart + (xEnd - xStart) * progress, yStart + (yEnd - yStart) * progress);
	}
	
	public function toString():String
	{
		return xStart + ", " + yStart + "   " + xEnd + ", " + yEnd;
	}
}

private class FloatTween
{
	public var start:Float;
	public var end:Float;
	
	public function new(start:Float, end:Float)
	{
		this.start = start;
		this.end = end;
	}
	
	public function get(progress:Float):Float
	{
		return start + (end - start) * progress;
	}
}