/**
 * @author Justin Espedal
 */
import nme.display.Bitmap;
import nme.display.BitmapData;
import nme.display.Graphics;
import nme.display.Shape;
import nme.geom.ColorTransform;
import nme.geom.Matrix;
import nme.geom.Point;
import nme.geom.Rectangle;

import com.stencyl.graphics.G;
import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Animation;
import com.stencyl.models.actor.Sprite;
import com.stencyl.models.Font;
import com.stencyl.Data;
import com.stencyl.Engine;

class BitmapDataUtil
{
	public static function scaleBitmapEdges(width:Int, height:Int, src:BitmapData, cornerWidth:Int, cornerHeight:Int, stretch:Bool):BitmapData
	{
		if(width <= 0) width = src.width;
		if(height <= 0) height = src.height;
		if(cornerWidth <= 0) cornerWidth = 1;
		if(cornerHeight <= 0) cornerHeight = 1;
		
		var bg:BitmapData = new BitmapData(width, height);
		var w:Int = src.width;
		var h:Int = src.height;
		var cW:Int = cornerWidth;
		var cH:Int = cornerHeight;
		var mW:Int = w - (cW * 2);
		var mH:Int = h - (cH * 2);
		var p:Point = new Point(0, 0);
		var rect:Rectangle = new Rectangle(0, 0, 0, 0);
		
		//copy corners
		rect.width = cW;
		rect.height = cH;
		//top left
		rect.x = 0;
		rect.y = 0;
		bg.copyPixels(src, rect, p);
		//top right
		rect.x = w - cW;
		p.x = width - cW;
		bg.copyPixels(src, rect, p);
		//bottom right
		rect.y = h - cH;
		p.y = height - cH;
		bg.copyPixels(src, rect, p);
		//bottom left
		rect.x = 0;
		p.x = 0;
		bg.copyPixels(src, rect, p);
		
		//copy edges
		var newRect:Rectangle = new Rectangle(0, 0, 0, 0);
		var xScale = (width - (cW * 2)) / mW;
		var yScale = (height - (cH * 2)) / mH;
		//top and bottom edges
		newRect.x = 0;
		newRect.y = 0;
		newRect.width = width - (cW * 2);
		newRect.height = cH;
		rect.x = cW;
		rect.y = 0;
		rect.width = mW;
		rect.height = cH;
		p.x = cW;
		p.y = 0;
		bg.copyPixels(scalePartialBitmap(src, rect, xScale, 1), newRect, p);
		rect.y = h - cH;
		p.y = height - cH;
		bg.copyPixels(scalePartialBitmap(src, rect, xScale, 1), newRect, p);
		//left and right edges
		newRect.width = cW;
		newRect.height = height - (cH * 2);
		rect.x = 0;
		rect.y = cH;
		rect.width = cW;
		rect.height = mH;
		p.x = 0;
		p.y = cH;
		bg.copyPixels(scalePartialBitmap(src, rect, 1, yScale), newRect, p);
		rect.x = w - cW;
		p.x = width - cW;
		bg.copyPixels(scalePartialBitmap(src, rect, 1, yScale), newRect, p);
		
		//copy center
		newRect.width = width - (cW * 2);
		newRect.height = height - (cH * 2);
		rect.x = cW;
		rect.y = cH;
		rect.width = mW;
		rect.height = mH;
		p.x = cW;
		p.y = cH;
		
		if(stretch)
			bg.copyPixels(scalePartialBitmap(src, rect, xScale, yScale), newRect, p);
		else
			bg.copyPixels(tilePartialBitmap(src, rect, xScale, yScale), newRect, p);
		
		return bg;
	}

	public static function scaleBitmap(src:BitmapData, sX:Float, sY:Float):BitmapData
	{
		var newImg:BitmapData = new BitmapData(Std.int(src.width * sX), Std.int(src.height * sY), true, 0);
		var matrix:Matrix = new Matrix();
		matrix.scale(sX, sY);
		newImg.draw(src, matrix);
		return newImg;
	}

	public static function scalePartialBitmap(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		var oldImg:BitmapData = new BitmapData(Std.int(rect.width), Std.int(rect.height));
		oldImg.copyPixels(src, rect, new Point(0, 0));
		return BitmapDataUtil.scaleBitmap(oldImg, sX, sY);
	}

	public static function tileBitmap(src:BitmapData, sX:Float, sY:Float):BitmapData
	{
		var tilesX:Int = Math.ceil(sX);
		var tilesY:Int = Math.ceil(sY);
		
		var newImg:BitmapData = new BitmapData(Std.int(src.width * sX), Std.int(src.height * sY), true, 0);
		var matrix:Matrix = new Matrix();
		for(y in 0...tilesY)
		{
			for(x in 0...tilesX)
			{
				newImg.draw(src, matrix);
				matrix.translate(src.width, 0);
			}
			matrix.translate(src.width * (-tilesX), src.height);
		}
		
		return newImg;
	}

	public static function tilePartialBitmap(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		var oldImg:BitmapData = new BitmapData(Std.int(rect.width), Std.int(rect.height));
		oldImg.copyPixels(src, rect, new Point(0, 0));
		return BitmapDataUtil.tileBitmap(oldImg, sX, sY);
	}

	public static function drawChar(c:String, font:DialogFont, img:BitmapData, x:Int, y:Int):Void
	{
		var src:BitmapData = font.getScaledChar(c);
		img.copyPixels(src, src.rect, new Point(x, y).add(font.info.getScaledOffset(c)), null, true);
	}

	public static function getImageFromAnimation(type:ActorType, animName:String):BitmapData
	{
		var sprite:Sprite = cast(Data.get().resources.get(type.spriteID), Sprite);
		var a:Animation = null;
		for(i in sprite.animations.keys())
		{
			if(sprite.animations.get(i) == null) continue;
			if(cast(sprite.animations.get(i), Animation).animName == animName)
			{
				a = cast(sprite.animations.get(i), Animation);
			}
		}
		if(a == null) return null;
		if(a.imgData == null)
			a.loadGraphics();
		
		return a.imgData;
	}

	public static function getActorTypeAnimation(type:ActorType, animName:String):Animation
	{
		var sprite:Sprite = cast(Data.get().resources.get(type.spriteID), Sprite);
		var a:Animation = null;
		for(i in sprite.animations.keys())
		{
			if(sprite.animations.get(i) == null) continue;
			if(cast(sprite.animations.get(i), Animation).animName == animName)
			{
				a = cast(sprite.animations.get(i), Animation);
			}
		}
		
		return a;
	}

	public static function asBitmapData(o:Dynamic):BitmapData
	{
		return cast(o, BitmapData);
	}
}