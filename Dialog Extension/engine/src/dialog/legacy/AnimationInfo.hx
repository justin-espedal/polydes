package dialog.legacy;

#if stencyl

import nme.display.BitmapData;
import nme.geom.Point;
import nme.geom.Rectangle;

#elseif unity

import unityengine.*;
import dialog.unity.compat.*;
import dialog.unity.compat.Typedefs;

#end

import dialog.core.*;

using dialog.util.BitmapDataUtil;

class AnimationInfo
{
	public var sheet:BitmapData;
	public var durations:Array<Int>;
	public var width:Int;
	public var height:Int;
	public var repeats:Bool;
	public var frameCount:Int;

	public function new(animName:String)
	{
		var textBytes:String = "";

		if(animName == null || animName == "")
		{
			textBytes =
				"<?xml version=\"1.0\"?><anim><info image=\"!DEFAULT!\" width=\"4\" height=\"4\" repeats=\"true\" />" +
				"<frames count=\"1\"><frame id=\"0\" x=\"0\" y=\"0\" width=\"4\" height=\"4\" xoffset=\"0\" yoffset=\"0\" duration=\"100\" /></frames></anim>";
		}

		else
		{
			if(animName.length > 8 && animName.substr(0, 8) == "Stencyl_")
			{
				//future
				//Stencyl_ActorName_AnimName
			}
			else
			{
				textBytes = Util.text(animName);
			}
		}

		var xml:Xml = Xml.parse(textBytes);

		var src:BitmapData = null;
		var frames:Xml = null;
		for (node in xml.elements())
		{
			if(node.nodeName == "anim")
			{
				for(nodeChild in node.elements())
				{
					if(nodeChild.nodeName == "info")
					{
						if(nodeChild.get("image") == "!DEFAULT!")
							src = BitmapData.whiteTexture;
						else
							src = Util.img(nodeChild.get("image"));

						width = Std.parseInt(nodeChild.get("width"));
						height = Std.parseInt(nodeChild.get("height"));
						repeats = nodeChild.get("repeats") == "true";
					}
					else if(nodeChild.nodeName == "frames")
					{
						frames = nodeChild;
						frameCount = Std.parseInt(nodeChild.get("count"));
					}
				}
			}
		}

		if (src != null && frames != null)
		{
			sheet = BitmapDataUtil.newTransparentImg(width * frameCount, height);
			durations = new Array<Int>();

			var rect:Rectangle = new Rectangle(1, 1, 1, 1);
			var point:Point = new Point(0, 0);
			var curFrame:Int = 0;

			for (node in frames.elements())
			{
				if (node.nodeName == "frame")
				{
					curFrame = Std.parseInt(node.get("id"));

					rect.x = Std.parseInt(node.get("x"));
					rect.y = Std.parseInt(node.get("y"));
					rect.width = Std.parseInt(node.get("width"));
					rect.height = Std.parseInt(node.get("height"));

					point.x = Std.parseInt(node.get("xoffset")) + curFrame * width;
					point.y = Std.parseInt(node.get("yoffset"));

					//FIXME
					sheet.copyPixels(src, rect, point);//, null, null, true);
					durations[curFrame] = Std.parseInt(node.get("duration"));
				}
			}
		}
	}
}
