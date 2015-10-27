package dialog.ext;

#if stencyl

import nme.display.BitmapData;
import nme.geom.Point;

import com.stencyl.Engine;

#elseif unity

import dialog.unity.compat.G2;
import unityengine.*;

#end

import dialog.core.*;

using dialog.util.BitmapDataUtil;

class ExtraGlyphs extends dialog.core.DialogExtension
{
	#if unity
	private var style:ExtraGlyphs;

	public var glyphPadding:Int;
	#elseif stencyl
	private var style:dialog.ds.ext.ExtraGlyphs;
	#end

	public function new()
	{
		super();

		#if unity
		style = this;
		#end
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Extra Glyphs";

		cmds =
		[
			"glyph"=>glyph
		];
	}

	public function glyph(glyphName:String):Void
	{
		#if stencyl
		var img:BitmapData = Util.scaledImg(glyphName);
		#elseif unity
		var img:Texture2D = Resources.Load(glyphName);
		#end

		if(dg.drawX + img.width > dg.msgW)
			dg.startNextLine();
		if(dg.drawHandler != null)
		{
			var charID:Int = dg.drawHandler.addImg(img, G2.s(dg.msgX + dg.drawX), G2.s(dg.curLine.pos.y + dg.curLine.aboveBase) - img.height, false);
			dg.curLine.drawHandledChars.push(new DrawHandledImage(dg.drawHandler, charID));
		}
		else
			dg.curLine.img.drawImage(img, G2.s(dg.drawX), G2.s(dg.curLine.aboveBase) - img.height);
		dg.drawX += G2.us(img.width) + style.glyphPadding;
		dg.typeDelay = Std.int(dg.msgTypeSpeed * 1000);
		dg.runCallbacks(Dialog.WHEN_CHAR_TYPED);
	}
}
