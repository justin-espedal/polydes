	public static function rImage(s:String):nme.display.BitmapData
	{
		if(s == "")
			return null;
		if(com.stencyl.Engine.IMG_BASE == "1x")
			return nme.Assets.getBitmapData("assets/data/"+ s +".png");
		else
		{
			var toReturn = nme.Assets.getBitmapData("assets/data/"+ s + "@" + com.stencyl.Engine.IMG_BASE + ".png");
			if(toReturn == null)
				toReturn = scaleBitmap(nme.Assets.getBitmapData("assets/data/"+ s +".png"), com.stencyl.Engine.SCALE);
			return toReturn;
		}
	}
	
	public static function scaleBitmap(src:nme.display.BitmapData, s:Float):nme.display.BitmapData
	{
		var newImg:nme.display.BitmapData = new nme.display.BitmapData(Std.int(src.width * s), Std.int(src.height * s), true, 0);
		var matrix:nme.geom.Matrix = new nme.geom.Matrix();
		matrix.scale(s, s);
		newImg.draw(src, matrix);
		return newImg;
	}