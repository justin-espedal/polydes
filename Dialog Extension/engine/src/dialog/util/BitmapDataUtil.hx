package dialog.util;

#if stencyl

import nme.display.*;
import nme.geom.*;

import com.stencyl.graphics.G;
import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Animation;
import com.stencyl.models.actor.Sprite;
import com.stencyl.models.Font;
import com.stencyl.Data;
import com.stencyl.Engine;

#elseif unity

import unityengine.*;

import dialog.unity.compat.ColorTransform;
import dialog.unity.compat.Typedefs;
import dialog.unity.extension.TextureUtil;

#end

import dialog.core.DialogFont;

using dialog.util.BitmapDataUtil;

class BitmapDataUtil
{
	public static function get9Scaled(src:BitmapData, width:Int, height:Int, cornerWidth:Int, cornerHeight:Int, stretch:Bool):BitmapData
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
		bg.copyPixels(src.getScaledPartial(rect, xScale, 1), newRect, p);
		rect.y = h - cH;
		p.y = height - cH;
		bg.copyPixels(src.getScaledPartial(rect, xScale, 1), newRect, p);
		//left and right edges
		newRect.width = cW;
		newRect.height = height - (cH * 2);
		rect.x = 0;
		rect.y = cH;
		rect.width = cW;
		rect.height = mH;
		p.x = 0;
		p.y = cH;
		bg.copyPixels(src.getScaledPartial(rect, 1, yScale), newRect, p);
		rect.x = w - cW;
		p.x = width - cW;
		bg.copyPixels(src.getScaledPartial(rect, 1, yScale), newRect, p);

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
			bg.copyPixels(src.getScaledPartial(rect, xScale, yScale), newRect, p);
		else
			bg.copyPixels(src.getTiledPartial(rect, xScale, yScale), newRect, p);

		return bg;
	}

	public static function getPartial(src:BitmapData, rect:Rectangle):BitmapData
  {
		var newImg:BitmapData = new BitmapData(Std.int(rect.width), Std.int(rect.height));
		newImg.copyPixels(src, rect, zeroPoint);
		return newImg;

		//TODO: This may be buggy (swapping y pixels?)
		//return TextureUtil.getSubTexture(src, rect);
  }

	public static function getScaled(src:BitmapData, sX:Float, sY:Float):BitmapData
  {
		#if stencyl

		var newWidth = Std.int(src.width * sX);
		var newHeight = Std.int(src.height * sY);
		
		if(newWidth <= 0 || newHeight <= 0)
		{
			return new BitmapData(1, 1, true, 0);
		}

		var newImg:BitmapData = new BitmapData(newWidth, newHeight, true, 0);

		var matrix:Matrix = new Matrix();
		matrix.scale(sX, sY);
		newImg.draw(src, matrix);
		return newImg;

		#elseif unity

		return TextureUtil.getScaled(src, sX, sY);

		#end
  }

	public static function getTiled(src:BitmapData, sX:Float, sY:Float):BitmapData
	{
		#if stencyl

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

		#elseif unity

		return TextureUtil.getTiled(src, sX, sY);

		#end

	}

	public static function getScaledPartial(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		return src.getPartial(rect).getScaled(sX, sY);
	}

	public static function getTiledPartial(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		return src.getPartial(rect).getTiled(sX, sY);
	}

	public static function drawChar(img:BitmapData, c:String, font:DialogFont, x:Int, y:Int):Void
	{
		var src:BitmapData = font.getScaledChar(c);
		var offset = font.info.getScaledOffset(c);
		img.drawImage(src, cast x + offset.x, cast y + offset.y);
	}

	public static function drawImage(img:BitmapData, brush:BitmapData, x:Int, y:Int):Void
	{
		#if stencyl

		img.copyPixels(brush, brush.rect, new Point(x, y));

		#elseif unity

		TextureUtil.drawTexture(img, brush, x, y);

		#end
	}

	public static function newTransparentImg(w:Int, h:Int):BitmapData
	{
		var bmd = new BitmapData(w, h #if stencyl, true, 0 #end);
		#if unity
		bmd.fillColor(Color.clear);
		#end
		return bmd;
	}

	#if stencyl

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

	#elseif unity

	public static function clone(tex:Texture2D)
	{
		return Object.Instantiate(tex);
	}

	public static function fillColor(tex:Texture2D, color:Color):Void
  {
    var fillColorArray = tex.GetPixels();

    for(i in 0...fillColorArray.Length)
    {
      fillColorArray[i] = color;
    }

    tex.SetPixels(fillColorArray);
    tex.Apply();
  }

	public static function colorTransform(tex:Texture2D, rect:Rectangle, ct:ColorTransform)
	{
		TextureUtil.colorTransform(tex, rect, ct);
	}

	public static function copyPixels(tex:Texture2D, brush:Texture2D, src:Rectangle, p:Point)
	{
		TextureUtil.copyPixels(tex, brush, src, p);
	}

	#end

	private static var zeroPoint = new Point(0, 0);
}
