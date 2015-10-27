package dialog.core;

#if stencyl

import nme.display.BitmapData;

#elseif unity

import unityengine.*;
import dialog.unity.compat.*;
import dialog.unity.compat.Typedefs;

#end

import dialog.ds.*;

using dialog.util.BitmapDataUtil;

class ScalingImage
{
	public var template:ScalingImageTemplate;
	public var image:BitmapData;

	public function new(template:ScalingImageTemplate)
	{
		this.template = template;
		if(template != null)
			image = (template.image:BitmapData).clone();
	}

	public function setSize(w:Int, h:Int)
	{
		if(template == null)
			return;

		if(image.width == w && image.height == h)
			return;

		if(template.part == "Inside Border")
			image = template.image.get9Scaled(w, h, G2.s(template.border.x), G2.s(template.border.y), template.type == "Stretch Image");
		else
		{
			if(template.type == "Stretch Image")
				image = template.image.getScaled(w / image.width, h / image.height);
			else if(template.type == "Tile Image")
				image = template.image.getTiled(w / image.width, h / image.height);
		}
	}
}
