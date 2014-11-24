/**
 * @author Justin Espedal
 */
import com.stencyl.graphics.G;
import com.stencyl.Engine;

import nme.display.BitmapData;
import nme.geom.ColorTransform;

import scripts.ds.dialog.RatioPoint;
import scripts.ds.dialog.TweenTemplate;
import scripts.ds.dialog.WindowTemplate;

class DialogWindow
{
	public var template:WindowTemplate;
	public var img:ScalingImage;

	public var position:IntPoint;
	public var size:IntPoint;
	public var tween:WindowTween;
	
	public var tweenCompleteNotify:Array<Void->Void>;
	
	public function new(template:WindowTemplate)
	{
		this.template = template;
		this.img = new ScalingImage(template.image);

		var imgWidth:Int = img.image.width;
		var imgHeight:Int = img.image.height;

		if(template.scaleWidth == "Custom")
			imgWidth = Util.getX(template.scaleWidthSize, Engine.screenWidth);
		if(template.scaleHeight == "Custom")
			imgHeight = Util.getY(template.scaleHeightSize, Engine.screenHeight);
		img.setSize(imgWidth, imgHeight);
		
		size = new IntPoint(imgWidth, imgHeight);
		reposition();
		
		tween = null;
		tweenCompleteNotify = new Array<Void->Void>();
	}

	public var floating:Bool = false;

	public function setContentPos(x:Int, y:Int)
	{
		floating = true;
		position.x = Std.int(x - template.insets.x);
		position.y = Std.int(y - template.insets.y);
	}
	
	public function setContentSize(w:Int, h:Int)
	{
		var imgWidth:Int = img.image.width;
		var imgHeight:Int = img.image.height;
		if(template.scaleWidth == "Fit Contents")
			imgWidth = Std.int(w + template.insets.x + template.insets.width);
		if(template.scaleHeight == "Fit Contents")
			imgHeight = Std.int(h + template.insets.y + template.insets.height);
		img.setSize(imgWidth, imgHeight);
		size.x = imgWidth;
		size.y = imgHeight;
		
		if(!floating)
			reposition();
	}
	
	public function reposition():Void
	{
		position = Util.getScreenPos(template.position);
		var origin:IntPoint = Util.getPos(img.template.origin, size.x, size.y);
		position.x -= origin.x;
		position.y -= origin.y;
	}
	
	public function applyTween(tween:TweenTemplate):Void
	{
		var origin:IntPoint = Util.getPos(img.template.origin, size.x, size.y);
		var pos:IntPoint = new IntPoint(position.x + origin.x, position.y + origin.y);
		
		this.tween = new WindowTween(img.image, pos, tween);
	}
	
	public function update():Void
	{
		if(tween == null)
			return;
		
		tween.update(10);
		var progress:Float = tween.elapsed / tween.duration;
		
		var newImg:BitmapData = BitmapDataUtil.scaleBitmap(tween.srcImg, tween.scale.get(progress).x, tween.scale.get(progress).y);
		var ct:ColorTransform = new ColorTransform();
		ct.alphaMultiplier = tween.opacity.get(progress);
		newImg.colorTransform(newImg.rect, ct);
		
		img.image = newImg;
		position = tween.pos1.get(progress);
		var origin:IntPoint = Util.getPos(img.template.origin, newImg.width, newImg.height);
		position.x -= origin.x;
		position.y -= origin.y;

		if(tween.elapsed >= tween.duration)
		{
			tween = null;
			for(f in tweenCompleteNotify)
				f();
			
			tweenCompleteNotify = new Array<Void->Void>();
		}
	}
	
	public function draw(g:G):Void
	{
		G2.drawImage(img.image, position.x, position.y);
	}
}