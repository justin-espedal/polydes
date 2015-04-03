import com.stencyl.models.Font;

import nme.display.BitmapData;
import nme.geom.ColorTransform;
import nme.geom.Point;

class DialogFont
{
	public var name:String;
	public var info:DialogFontInfo;
	public var color:Int;
	public var dropshadowColor:Int;
	public var dropshadowX:Int;
	public var dropshadowY:Int;

	public var tempColor:Int;

	private var ct_main:ColorTransform;
	private var ct_ds:ColorTransform;
	private var ct_temp:ColorTransform;
	private var dsPoint:Point;

	private var chars:Map<String, BitmapData>;

	public function new(info:String, color:Int, dropshadowColor:Int, dropshadowX:Int, dropshadowY:Int)
	{
		name = info;
		if(info == "null")
			info = "";
		this.info = DialogFontLibrary.getFontInfo(info);
		this.color = color;
		this.dropshadowColor = dropshadowColor;
		this.dropshadowX = dropshadowX;
		this.dropshadowY = dropshadowY;
		tempColor = -1;

		chars = new Map<String, BitmapData>();
		ct_main = new ColorTransform();
		ct_ds = new ColorTransform();
		ct_temp = new ColorTransform();
		if(color != -1)
			ct_main.color = color;
		if(dropshadowColor != -1)
			ct_ds.color = dropshadowColor;
		dsPoint = new Point(dropshadowX, dropshadowY);
	}

	public function getChar(c:String):BitmapData
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
			return new BitmapData(1, 1, true, 0);

		var w:Int = Std.int(info.getSize(c).width);
		var h:Int = Std.int(info.getSize(c).height);
		var usingDS:Bool = (dropshadowColor != -1);

		var src:BitmapData = info.getImg(c);
		var img:BitmapData = new BitmapData(w + (usingDS ? dropshadowX : 0), h + (usingDS ? dropshadowY : 0), true, 0);

		if(usingDS)
		{
			img.copyPixels(src, src.rect, dsPoint, null, true);
			img.colorTransform(img.rect, ct_ds);
		}
		if(tempColor != -1)
		{
			ct_temp.color = tempColor;
			var tempImg:BitmapData = new BitmapData(w, h, true, 0);
			tempImg.copyPixels(src, src.rect, zeroPoint, null, true);
			tempImg.colorTransform(tempImg.rect, ct_temp);
			img.copyPixels(tempImg, tempImg.rect, zeroPoint, null, true);
		}
		else if(color != -1)
		{
			var tempImg:BitmapData = new BitmapData(w, h, true, 0);
			tempImg.copyPixels(src, src.rect, zeroPoint, null, true);
			tempImg.colorTransform(tempImg.rect, ct_main);
			img.copyPixels(tempImg, tempImg.rect, zeroPoint, null, true);
		}
		else
		{
			img.copyPixels(src, src.rect, zeroPoint, null, true);
		}

		return img;
	}
}
