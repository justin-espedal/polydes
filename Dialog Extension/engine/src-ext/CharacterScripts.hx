/**
 * @author Justin Espedal
 */
import com.stencyl.models.Font;
import com.stencyl.Engine;

import nme.display.BitmapData;
import nme.geom.Point;

class CharacterScripts extends DialogExtension
{
	public var nameBitmap:BitmapData;
	public var nameboxWindow:DialogWindow;
	public var nameboxVisible:Bool;
	public var faceBitmap:BitmapData;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Character Scripts";
		
		nameBitmap = null;
		nameboxWindow = null;
		nameboxVisible = false;
		faceBitmap = null;
		
		cmds =
		[
			"showname"=>showname,
			"hidename"=>hidename,
			"face"=>face
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			if(nameboxWindow == null)
				nameboxWindow = new DialogWindow(style.nameboxWindow);
			faceBitmap = null;
		});
		addCallback(Dialog.RESTORE_DEFAULTS, function():Void
		{
			faceBitmap = null;
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			nameboxWindow.update();
		});
		addDrawCallback("Namebox", function():Void
		{
			if(nameboxVisible)
			{
				nameboxWindow.draw(g);
				
				if(nameboxWindow.tween == null)
				{
					G2.drawImage(nameBitmap, nameboxWindow.position.x + nameboxWindow.template.insets.x, nameboxWindow.position.y + nameboxWindow.template.insets.y, false);
				}
			}
		});
		addDrawCallback("Face", function():Void
		{
			if(faceBitmap != null)
			{
				var p:IntPoint = null;
				if(style.faceRelation == "Screen")
				{
					p = Util.getScreenPos(style.facePos);
				}
				else
				{
					var w:DialogWindow = dg.dgBase.window;
					p = Util.getPos(style.facePos, w.size.x, w.size.y);
					p.x += w.position.x;
					p.y += w.position.y;
				}
				
				var origin:IntPoint = Util.getPos(style.faceOrigin, faceBitmap.width, faceBitmap.height);
				G2.drawImage(faceBitmap, p.x - origin.x, p.y - origin.y, false);
			}
		});
	}

	public function showname(nameToDraw:String):Void
	{
		nameboxVisible = true;
		
		var f:DialogFont = DialogFont.get(style.nameboxFont);
		nameBitmap = new BitmapData(f.info.getScaledWidth(nameToDraw), f.info.scaledLineHeight, true, 0);
		nameboxWindow.setContentSize(G2.us(nameBitmap.width), G2.us(nameBitmap.height));
		var x:Int = 0;
		for(i in 0...nameToDraw.length)
		{
			BitmapDataUtil.drawChar(nameToDraw.charAt(i), f, nameBitmap, x, 0);
			x += f.info.getScaledAdvance(nameToDraw.charAt(i));
		}
		nameboxWindow.applyTween(style.nameboxWindow.createTween);
	}
	
	public function hidename():Void
	{
		nameboxWindow.tweenCompleteNotify.push(function():Void
		{
			nameboxVisible = false;
		});
		nameboxWindow.applyTween(style.nameboxWindow.destroyTween);
	}
	
	public function face(facename:String):Void
	{
		if(facename == "none")
		{
			faceBitmap = null;
			dg.msgX = Std.int(dg.defaultBounds.x);
			dg.msgY = Std.int(dg.defaultBounds.y);
			dg.msgW = Std.int(dg.defaultBounds.width);
			dg.msgH = Std.int(dg.defaultBounds.height);
		}
		else
		{
			faceBitmap = Util.scaledImg(style.faceImagePrefix + facename);
			dg.msgX = Std.int(dg.defaultBounds.x + style.faceMsgOffset.x);
			dg.msgY = Std.int(dg.defaultBounds.y + style.faceMsgOffset.y);
			dg.msgW = Std.int(dg.defaultBounds.width + style.faceMsgOffset.width);
			dg.msgH = Std.int(dg.defaultBounds.height + style.faceMsgOffset.height);
		}
	}
}