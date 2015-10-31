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

		if(template.type == "Stretch Image")
		{
			if(template.part == "Inside Border")
				image = template.image.get9Scaled(w, h, G2.si(template.border), true);
			else 
				image = template.image.getScaled(w / image.width, h / image.height);
		}
		else if(template.type == "Tile Image")
		{
			if(template.part == "Inside Border")
				image = template.image.get9Scaled(w, h, G2.si(template.border), false);
			else 
				image = template.image.getTiled(w / image.width, h / image.height);
		}
	}
}
