/**
 * @author Justin Espedal
 */
import com.stencyl.Data;
import com.stencyl.Engine;
import com.stencyl.models.Font;

import nme.Assets;

import nme.display.BitmapData;
import nme.geom.Point;
import nme.geom.Rectangle;

class DialogFontInfo
{
	public var src:BitmapData;
	public var lineHeight:Int;
	
	public var base:Int;
	public var aboveBase:Int; //(how many pixels should be present above the baseline)
	public var belowBase:Int; //(how many pixels should be present below the baseline)
	
	public var scaledLineHeight:Int;
	public var scaledBase:Int;
	public var scaledAboveBase:Int;
	public var scaledBelowBase:Int;

	public var preScaled:Bool; //whether the data in the xml refers to scaled size or unscaled

	private var charMap:Map<String, CharInfo>;
	
	private static var defaultFont:DialogFontInfo = null;
	private static var loadedFonts:Map<Font, DialogFontInfo> = new Map<Font, DialogFontInfo>();

	public static function get(f:Font):DialogFontInfo
	{
		if(f == null)
		{
			if(defaultFont == null)
				defaultFont = new DialogFontInfo(null);

			return defaultFont;
		}
		
		if(!loadedFonts.exists(f))
			loadedFonts.set(f, new DialogFontInfo(f));

		return loadedFonts.get(f);
	}

	private function new(font:Font)
	{
		var textBytes:String;
		
		if(font == null)
		{
			textBytes = Assets.getText("assets/graphics/default-font.fnt");
			src = BitmapDataUtil.scaleBitmap(Assets.getBitmapData("assets/graphics/default-font.png"), Engine.SCALE, Engine.SCALE);
			preScaled = false;
		}
		
		else
		{
			var id:Int = font.ID;
			textBytes = Data.get().resourceAssets.get(id + ".fnt");
			src = Data.get().getGraphicAsset(id + ".png", "assets/graphics/" + Engine.IMG_BASE + "/font-" + id + ".png");
			preScaled = true;
		}
		
		var xml:Xml = Xml.parse(textBytes);
		
		var chars:Xml = null;
		for (node in xml.elements())
		{
			if(node.nodeName == "font")
			{
				for(nodeChild in node.elements())
				{
					if(nodeChild.nodeName == "common")
					{
						if(preScaled)
						{
							scaledLineHeight = Std.parseInt(nodeChild.get("lineHeight"));
							lineHeight = G2.us(scaledLineHeight);
						}
						else
						{
							lineHeight = Std.parseInt(nodeChild.get("lineHeight"));
							scaledLineHeight = G2.s(lineHeight);
						}
					}
					else if(nodeChild.nodeName == "chars")
					{
						chars = nodeChild;
					}
				}
			}
		}
		
		charMap = new Map<String, CharInfo>();
		
		if (chars != null)
		{
			var ch:CharInfo;
			var rect:Rectangle = new Rectangle(1, 1, 1, 1);
			var point:Point = new Point(0, 0);
			var xadvance:Int = 0;

			for (node in chars.elements())
			{
				if (node.nodeName == "char")
				{
					rect.x = Std.parseInt(node.get("x"));
					rect.y = Std.parseInt(node.get("y"));
					rect.width = Std.parseInt(node.get("width"));
					rect.height = Std.parseInt(node.get("height"));
					
					point.x = Std.parseInt(node.get("xoffset"));
					point.y = Std.parseInt(node.get("yoffset"));

					xadvance = Std.parseInt(node.get("xadvance"));
					
					if(!preScaled)
					{
						rect = G2.sr(rect);
						point = G2.sp(point);
						xadvance = G2.s(xadvance);
					}

					ch = new CharInfo
					(
						Std.parseInt(node.get("id")),
						rect.clone(),
						point.clone(),
						xadvance
					);
					
					charMap.set(String.fromCharCode(Std.parseInt(node.get("id"))), ch);
				}
			}
		}
		
		calculateBase();
	}
	
	private function calculateBase():Void
	{
		var heightMap:Map<Int, Int> = new Map<Int, Int>(); //height -> number with that height
		heightMap.set(0, 0);
		var mostChars:Int = 0; //height that the most characters have
		
		var char:CharInfo;
		var scaledHeight:Int;
		
		for(key in charMap.keys())
		{
			char = charMap.get(key);
			
			scaledHeight = Std.int(char.scaledOffset.y + char.scaledPos.height);
			
			if(!heightMap.exists(scaledHeight))
				heightMap.set(scaledHeight, 1);
			else
				heightMap.set(scaledHeight, heightMap.get(scaledHeight) + 1);
			
			if(heightMap.get(scaledHeight) > heightMap.get(mostChars))
				mostChars = scaledHeight;
		}
		
		scaledAboveBase = scaledBase = mostChars;
		
		scaledBelowBase = 0;
		
		for(key in charMap.keys())
		{
			char = charMap.get(key);
			
			if(char.scaledOffset.y < 0 && scaledBase - char.scaledOffset.y > scaledAboveBase)
				scaledAboveBase = Std.int(scaledBase - char.scaledOffset.y);
			if(char.scaledOffset.y + char.scaledPos.height - scaledBase > scaledBelowBase)
				scaledBelowBase = Std.int(char.scaledOffset.y + char.scaledPos.height - scaledBase);
		}

		base = G2.us(scaledBase);
		aboveBase = G2.us(scaledAboveBase);
		belowBase = G2.us(scaledBelowBase);
	}
	
	public function getAdvance(c:String):Int
	{
		if(!charMap.exists(c))
			return 0;
		
		return charMap.get(c).advance;
	}

	public function getScaledAdvance(c:String):Int
	{
		if(!charMap.exists(c))
			return 0;
		
		return charMap.get(c).scaledAdvance;
	}
	
	public function getWidth(chars:String):Int
	{
		var w:Int = 0;
		
		for(i in 0...chars.length)
		{
			w += charMap.get(chars.charAt(i)).advance;
		}
		
		return w;
	}

	public function getScaledWidth(chars:String):Int
	{
		var w:Int = 0;
		
		for(i in 0...chars.length)
		{
			w += charMap.get(chars.charAt(i)).scaledAdvance;
		}
		
		return w;
	}
	
	public function getOffset(c:String):Point
	{
		return charMap.get(c).offset;
	}

	public function getScaledOffset(c:String):Point
	{
		return charMap.get(c).scaledOffset;
	}
	
	public function getSize(c:String):Rectangle
	{
		return charMap.get(c).pos;
	}

	public function getScaledSize(c:String):Rectangle
	{
		return charMap.get(c).scaledPos;
	}
	
	private static var zeroPoint = new Point(0, 0);
	
	public function getScaledImg(c:String):BitmapData
	{
		var char:CharInfo = charMap.get(c);
		var img:BitmapData = new BitmapData(Std.int(char.scaledPos.width), Std.int(char.scaledPos.height), true, 0);
		img.copyPixels(src, char.scaledPos, zeroPoint);
		return img;
	}
	
	public function getDebugImg(lineWidth:Int):BitmapData
	{
		var x:Int = 0;
		var y:Int = 0;
		
		var base:BitmapData = new BitmapData(G2.s(lineWidth), G2.s(200), false, 0x333333);
		var img:BitmapData = new BitmapData(G2.s(lineWidth), G2.s(200), true, 0);
		
		var topColor:UInt = 0xffff0000;
		var baseColor:UInt = 0xff00ff00;
		var bottomColor:UInt = 0xff0000ff;
		
		var lineRect:Rectangle = new Rectangle(0, 0, G2.s(lineWidth), G2.s(1));
		
		lineRect.y = y;
		base.fillRect(lineRect, topColor);
		
		lineRect.y = G2.s(y + aboveBase + belowBase);
		base.fillRect(lineRect, bottomColor);
		
		lineRect.y = G2.s(y + aboveBase);
		base.fillRect(lineRect, baseColor);
		
		var char:CharInfo;
		for(key in charMap.keys())
		{
			char = charMap.get(key);
			
			if(x + char.offset.x + char.pos.width > lineWidth)
			{
				x = 0;
				y += (aboveBase + belowBase + 5);
				lineRect.y = G2.s(y);
				base.fillRect(lineRect, topColor);
				
				lineRect.y = G2.s(y + aboveBase + belowBase);
				base.fillRect(lineRect, bottomColor);
				
				lineRect.y = G2.s(y + aboveBase);
				base.fillRect(lineRect, baseColor);
			}
			
			img.copyPixels(src, G2.sr(char.pos), new Point(G2.s(x + char.offset.x), G2.s(y + char.offset.y)));
			x += char.advance;
		}
		
		base.copyPixels(img, img.rect, zeroPoint, null, true);
		
		return base;
	}
}

private class CharInfo
{
	public var id:Int;
	public var pos:Rectangle;
	public var offset:Point;
	public var advance:Int;

	//above are for 1x math, these are the "real" numbers
	public var scaledPos:Rectangle;
	public var scaledOffset:Point;
	public var scaledAdvance:Int;
	
	public function new(id:Int, pos:Rectangle, offset:Point, advance:Int)
	{
		this.id = id;
		scaledPos = pos;
		scaledOffset = offset;
		scaledAdvance = advance;

		this.pos = new Rectangle
		(
			Std.int(pos.x / Engine.SCALE),
			Std.int(pos.y / Engine.SCALE),
			Std.int(pos.width / Engine.SCALE),
			Std.int(pos.height / Engine.SCALE)
		);

		this.offset = new Point
		(
			Std.int(offset.x / Engine.SCALE),
			Std.int(offset.y / Engine.SCALE)
		);

		this.advance = Std.int(advance / Engine.SCALE);
	}
}