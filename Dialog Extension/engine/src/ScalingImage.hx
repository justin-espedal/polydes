import scripts.ds.dialog.ScalingImageTemplate;

import nme.display.BitmapData;

class ScalingImage
{
	public var template:ScalingImageTemplate;
	public var image:BitmapData;

	public function new(template:ScalingImageTemplate)
	{
		this.template = template;
		image = template.image.clone();
	}

	public function setSize(w:Int, h:Int)
	{
		if(image.width == w && image.height == h)
			return;
		
		if(template.part == "Inside Border")
			image = BitmapDataUtil.scaleBitmapEdges(w, h, template.image, G2.s(template.border.x), G2.s(template.border.y), template.type == "Stretch Image");
		else
		{
			if(template.type == "Stretch Image")
				image = BitmapDataUtil.scaleBitmap(template.image, w / image.width, h / image.height);
			else if(template.type == "Tile Image")
				image = BitmapDataUtil.tileBitmap(template.image, w / image.width, h / image.height);
		}
	}
}