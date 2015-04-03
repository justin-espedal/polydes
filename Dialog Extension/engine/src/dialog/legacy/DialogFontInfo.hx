import com.stencyl.Data;
import com.stencyl.Engine;

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

	private var charMap:Map<String, CharInfo>;

	public function new(fontName:String)
	{
		var textBytes:String;

		if(fontName == null || fontName == "")
		{
			textBytes = Assets.getText("assets/graphics/default-font.fnt");
			src = Assets.getBitmapData("assets/graphics/default-font.png");
		}

		else
		{
			if(fontName.length > 8 && fontName.substr(0, 8) == "Stencyl_")
			{
				fontName = fontName.substr(8);
				var id:Int = Util.font(fontName).ID;
				textBytes = Data.get().resourceAssets.get(id + ".fnt");
				src = Data.get().getGraphicAsset(id + ".png", "assets/graphics/" + Engine.IMG_BASE + "/font-" + id + ".png");
			}
			else
			{
				textBytes = Util.fontInfo(fontName);
				src = Util.fontImg(fontName);
			}
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
						lineHeight = Std.parseInt(nodeChild.get("lineHeight"));
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

					ch = new CharInfo
					(
						Std.parseInt(node.get("id")),
						rect.clone(),
						point.clone(),
						Std.parseInt(node.get("xadvance"))
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
		var height:Int;

		for(key in charMap.keys())
		{
			char = charMap.get(key);

			height = Std.int(char.offset.y + char.pos.height);

			if(!heightMap.exists(height))
				heightMap.set(height, 1);
			else
				heightMap.set(height, heightMap.get(height) + 1);

			if(heightMap.get(height) > heightMap.get(mostChars))
				mostChars = height;
		}

		aboveBase = base = mostChars;

		belowBase = 0;

		for(key in charMap.keys())
		{
			char = charMap.get(key);

			if(char.offset.y < 0 && base - char.offset.y > aboveBase)
				aboveBase = Std.int(base - char.offset.y);
			if(char.offset.y + char.pos.height - base > belowBase)
				belowBase = Std.int(char.offset.y + char.pos.height - base);
		}
	}

	public function getAdvance(c:String):Int
	{
		if(!charMap.exists(c))
			return 0;

		return charMap.get(c).advance;
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

	public function getOffset(c:String):Point
	{
		return charMap.get(c).offset;
	}

	public function getSize(c:String):Rectangle
	{
		return charMap.get(c).pos;
	}

	private static var zeroPoint = new Point(0, 0);

	public function getImg(c:String):BitmapData
	{
		var char:CharInfo = charMap.get(c);
		var img:BitmapData = new BitmapData(Std.int(char.pos.width), Std.int(char.pos.height), true, 0);
		img.copyPixels(src, char.pos, zeroPoint);
		return img;
	}

	public function getDebugImg(lineWidth:Int):BitmapData
	{
		var x:Int = 0;
		var y:Int = 0;

		var base:BitmapData = new BitmapData(lineWidth, 200, false, 0x333333);
		var img:BitmapData = new BitmapData(lineWidth, 200, true, 0);

		var topColor:UInt = 0xffff0000;
		var baseColor:UInt = 0xff00ff00;
		var bottomColor:UInt = 0xff0000ff;

		var lineRect:Rectangle = new Rectangle(0, 0, lineWidth, 1);

		lineRect.y = y;
		base.fillRect(lineRect, topColor);

		lineRect.y = y + aboveBase + belowBase;
		base.fillRect(lineRect, bottomColor);

		lineRect.y = y + aboveBase;
		base.fillRect(lineRect, baseColor);

		var char:CharInfo;
		for(key in charMap.keys())
		{
			char = charMap.get(key);

			if(x + char.offset.x + char.pos.width > lineWidth)
			{
				x = 0;
				y += (aboveBase + belowBase + 5);
				lineRect.y = y;
				base.fillRect(lineRect, topColor);

				lineRect.y = y + aboveBase + belowBase;
				base.fillRect(lineRect, bottomColor);

				lineRect.y = y + aboveBase;
				base.fillRect(lineRect, baseColor);
			}

			img.copyPixels(src, char.pos, new Point(x + char.offset.x, y + char.offset.y));
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

	public function new(id:Int, pos:Rectangle, offset:Point, advance:Int)
	{
		this.id = id;
		this.pos = pos;
		this.offset = offset;
		this.advance = advance;
	}
}
