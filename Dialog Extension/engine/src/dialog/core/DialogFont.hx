package dialog.core;

#if stencyl

import com.stencyl.models.Font;

import nme.display.BitmapData;
import nme.geom.ColorTransform;
import nme.geom.Point;

#elseif unity

import dialog.unity.compat.*;
import dialog.unity.compat.Typedefs;
import unityengine.*;

#end

using dialog.util.BitmapDataUtil;

class DialogFont
{
	public var info:DialogFontInfo;
	public var color:Int;

	public var tempColor:Int;

	private var ct_main:ColorTransform;
	private var ct_temp:ColorTransform;

	private var chars:Map<String, BitmapData>;

	#if stencyl
	private static var defaultFont:DialogFont = null;
	#end

	private static var loadedFonts:Map<Font, DialogFont> = new Map<Font, DialogFont>();

	public static function get(f:Font):DialogFont
	{
		#if stencyl
		if(f == null)
		{
			if(defaultFont == null)
				defaultFont = new DialogFont(DialogFontInfo.get(null), -1);

			return defaultFont;
		}
		#end

		if(!loadedFonts.exists(f))
			loadedFonts.set(f, new DialogFont(DialogFontInfo.get(f), 0xFFFFFF));

		return loadedFonts.get(f);
	}

	public function new(info:DialogFontInfo, color:Int)
	{
		this.info = info;
		this.color = color;
		tempColor = -1;

		chars = new Map<String, BitmapData>();
		ct_main = new ColorTransform();
		ct_temp = new ColorTransform();
		if(color != -1)
			ct_main.color = color;
	}

	public function getScaledChar(c:String):BitmapData
	{
		if(tempColor != -1)
		{
			return createChar(c);
		}

		if(!chars.exists(c))
		{
			chars.set(c, createChar(c));
		}

		return chars.get(c);
	}

	private static var zeroPoint:Point = new Point(0, 0);

	public function createChar(c:String):BitmapData
	{
		if(c == " ")
			return BitmapDataUtil.newTransparentImg(1, 1);

		var src:BitmapData = info.getScaledImg(c);
		var w:Int = src.width;
		var h:Int = src.height;

		var img:BitmapData = BitmapDataUtil.newTransparentImg(w, h);

		if(tempColor != -1)
		{
			ct_temp.color = tempColor;
			var tempImg:BitmapData = BitmapDataUtil.newTransparentImg(w, h);
			tempImg.drawImage(src, 0, 0);
			//tempImg.copyPixels(src, src.rect, zeroPoint, null, true);
			tempImg.colorTransform(#if stencyl tempImg.rect #elseif unity new Rectangle(0, 0, w, h) #end, ct_temp);
			img.drawImage(tempImg, 0, 0);
			//img.copyPixels(tempImg, tempImg.rect, zeroPoint, null, true);
		}
		else if(color != -1)
		{
			var tempImg:BitmapData = BitmapDataUtil.newTransparentImg(w, h);
			tempImg.drawImage(src, 0, 0);
			//tempImg.copyPixels(src, src.rect, zeroPoint, null, true);
			tempImg.colorTransform(#if stencyl tempImg.rect #elseif unity new Rectangle(0, 0, w, h) #end, ct_main);
			img.drawImage(tempImg, 0, 0);
			//img.copyPixels(tempImg, tempImg.rect, zeroPoint, null, true);
		}
		else
		{
			//img.copyPixels(src, src.rect, zeroPoint, null, true);
			img.drawImage(src, 0, 0);
		}

		return img;
	}
}
