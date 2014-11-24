/**
 * @author Justin Espedal
 */
import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Sprite;
import com.stencyl.models.Font;
import com.stencyl.models.Resource;
import com.stencyl.models.Sound;
import com.stencyl.Data;
import com.stencyl.Engine;

import nme.Assets;
import nme.display.BitmapData;
import nme.geom.Point;

import scripts.ds.dialog.RatioInt;
import scripts.ds.dialog.RatioPoint;

class Util
{
	public static function sound(name:String):Sound
	{
		return cast Data.get().resourceMap.get(name);
	}

	public static function font(name:String):Font
	{
		return cast Data.get().resourceMap.get(name);
	}	

	public static function img(name:String):BitmapData
	{
		return Assets.getBitmapData("assets/data/[ext] dialog/images/"+ name +".png");
	}

	public static function scaledImg(name:String):BitmapData
	{
		if(Engine.IMG_BASE == "1x")
			return Assets.getBitmapData("assets/data/[ext] dialog/images/"+ name +".png");
		else
		{
			var toReturn = Assets.getBitmapData("assets/data/[ext] dialog/images/"+ name + "@" + Engine.IMG_BASE + ".png");
			if(toReturn == null)
				toReturn = BitmapDataUtil.scaleBitmap(Assets.getBitmapData("assets/data/[ext] dialog/images/"+ name +".png"), Engine.SCALE, Engine.SCALE);
			return toReturn;
		}
	}
	
	public static function text(name:String):String
	{
		return Assets.getText("assets/data/[ext] dialog/"+ name);
	}

	public static function getTitleIndices(s_split:Array<String>, titleMarker:String):Array<Int>
	{
		var indices:Array<Int> = new Array<Int>();
		
		for(i in 0...s_split.length)
		{
			if(s_split[i].charAt(0) == titleMarker)
				indices.push(i);
		}
		
		return indices;
	}

	public static function valueOfString(s:String):Dynamic
	{
		//Hexadicimal Int
		if(s.substring(0, 2) == "0x")
		{
			return Std.parseInt(s);
		}
		
		if(s.substring(0, 1) == "#")
		{
			return Std.parseInt("0x" + s.substr(1));
		}
		
		//Float or Int
		if(!Math.isNaN(Std.parseFloat(s)))
			return Std.parseFloat(s);
		
		//Bool
		if(s == "true")
			return true;
		
		if(s == "false")
			return false;
		
		return s;
	}
	
	public static function trim(s:String):String
	{
		if(s.charAt(0) != " " && s.charAt(s.length - 1) != " ")
			return s;
		
		var a:Array<String> = s.split("");
		
		while(a[0] == " ")
			a.shift();
		while(a[a.length - 1] == " ")
			a.pop();
		
		return a.join("");
	}
	
	public static var newlinePattern:EReg = ~/[\r\n]+/g;
	
	public static function getLines(s:String):Array<String>
	{
		return newlinePattern.split(s);
	}
	
	public static function getFileLines(filename:String):Array<String>
	{
		return newlinePattern.split(nme.Assets.getText("assets/data/[ext] dialog/"+ filename));
	}

	public static function getX(i:RatioInt, base:Int):Int
	{
		return Std.int(i.v + i.p * base);
	}

	public static function getY(i:RatioInt, base:Int):Int
	{
		return Std.int(i.v + i.p * base);
	}

	public static function getScreenPos(point:RatioPoint):IntPoint
	{
		return new IntPoint(point.xv + point.xp * Engine.screenWidth, point.yv + point.yp * Engine.screenHeight);
	}

	public static function getPos(point:RatioPoint, w:Int, h:Int):IntPoint
	{
		return new IntPoint(point.xv + point.xp * w, point.yv + point.yp * h);
	}	
}