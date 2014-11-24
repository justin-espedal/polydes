/**
 * @author Justin Espedal
 */
import nme.display.BitmapData;
import nme.geom.Point;

import com.stencyl.Engine;

class ExtraGlyphs extends DialogExtension
{
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Extra Glyphs";
		
		cmds =
		[
			"glyph"=>glyph
		];
	}

	public function glyph(glyphName:String):Void
	{
		var img:BitmapData = Util.scaledImg(glyphName);
		
		if(dg.drawX + img.width > dg.msgW)
			dg.startNextLine();
		if(dg.drawHandler != null)
		{
			var charID:Int = dg.drawHandler.addImg(img, G2.s(dg.msgX + dg.drawX), G2.s(dg.curLine.pos.y + dg.curLine.aboveBase) - img.height, false);
			dg.curLine.drawHandledChars.push(new DrawHandledImage(dg.drawHandler, charID));
		}
		else
			dg.curLine.img.copyPixels(img, img.rect, new Point(G2.s(dg.drawX), G2.s(dg.curLine.aboveBase) - img.height));
		dg.drawX += G2.us(img.width) + style.glyphPadding;
		dg.typeDelay = Std.int(dg.msgTypeSpeed * 1000);
		dg.runCallbacks(Dialog.WHEN_CHAR_TYPED);
	}
}