import com.stencyl.graphics.G;
import com.stencyl.Engine;

import nme.display.BitmapData;
import nme.geom.Rectangle;
import nme.geom.Point;
import nme.geom.Matrix;

class G2
{
	private static var rect:Rectangle = new Rectangle(0, 0, 1, 1);
	private static var rect2:Rectangle = new Rectangle(0, 0, 1, 1);
	private static var point:Point = new Point(0, 0);
	private static var point2:Point = new Point(0, 0);
	private static var mtx:Matrix = new Matrix();
	
	public static function drawImage(img:BitmapData, x:Float, y:Float, scale:Bool = true, scaleXY:Bool = true)
	{
		if(Dialog.graphicsReference == null)
			return;
		
		if(scale && Engine.SCALE != 1)
			img = BitmapDataUtil.scaleBitmap(img, Engine.SCALE, Engine.SCALE);
		
		var g:G = Dialog.graphicsReference;
		
		if(scaleXY)
		{
			x *= g.scaleX;
			y *= g.scaleY;
		}
		
		rect.x = 0;
		rect.y = 0;
		rect.width = img.width;
		rect.height = img.height;
		
		point.x = g.x + x;
		point.y = g.y + y;
		
		#if (js)
		canvas.copyPixels(img, rect, point);
		#end
		
		#if (flash || cpp)
		
  		mtx.identity();
 	 	mtx.translate(point.x, point.y);
 	 	
 	 	if(g.alpha != 1)
 	 	{
 	 		point2.x = 0;
 	 		point2.y = 0;
 	 		
 	 		rect2.width = img.width;
 	 		rect2.height = img.height;
 	 	
 	 		//TODO: Can we avoid making a new one each time?
 	 		var temp:BitmapData = new BitmapData(img.width, img.height, true, toARGB(0x000000, Std.int(g.alpha * 255)));
 	 		var temp2:BitmapData = new BitmapData(img.width, img.height, true, 0);
 	 		
			temp2.copyPixels(img, rect2, point2, temp, null, true);
			img = temp2;
		}

		g.graphics.beginBitmapFill(img, mtx);
 	 	g.graphics.drawRect(point.x, point.y, img.width, img.height);
	 	g.graphics.endFill();
	 	
		#end
	}
	
	private static function toARGB(rgb:Int, newAlpha:Int):Int
	{
		var argb = 0; 
		argb = (rgb); 
		argb += (newAlpha << 24); 
		
		return argb; 
	}

	public static function s(val:Float):Int
	{
		return Std.int(val * Engine.SCALE);
	}

	public static function sr(val:Rectangle):Rectangle
	{
		var r:Rectangle = val.clone();
		r.x *= Engine.SCALE;
		r.y *= Engine.SCALE;
		r.width *= Engine.SCALE;
		r.height *= Engine.SCALE;

		return r;
	}

	public static function sp(val:Point):Point
	{
		var p:Point = val.clone();
		p.x *= Engine.SCALE;
		p.y *= Engine.SCALE;
		
		return p;
	}

	public static function us(val:Float):Int
	{
		return Std.int(val / Engine.SCALE);
	}

	public static function usr(val:Rectangle):Rectangle
	{
		var r:Rectangle = val.clone();
		r.x /= Engine.SCALE;
		r.y /= Engine.SCALE;
		r.width /= Engine.SCALE;
		r.height /= Engine.SCALE;

		return r;
	}

	public static function usp(val:Point):Point
	{
		var p:Point = val.clone();
		p.x /= Engine.SCALE;
		p.y /= Engine.SCALE;
		
		return p;
	}
}