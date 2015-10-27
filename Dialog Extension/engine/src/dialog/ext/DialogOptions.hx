package dialog.ext;

#if stencyl

import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import com.stencyl.Input;

import nme.display.BitmapData;
import nme.geom.Point;

#elseif unity

import unityengine.*;
import dialog.unity.compat.*;
import dialog.unity.compat.Typedefs;

#end

import dialog.core.*;
import dialog.ds.*;
import dialog.geom.*;

using dialog.util.BitmapDataUtil;

class DialogOptions extends dialog.core.DialogExtension
{
	private var visible:Bool;
	private var options:Array<String>;
	private var targets:Array<String>;
	private var coords:Array<Point>;
	private var curCoord:Point;
	private var usedFont:DialogFont;
	private var selectedIndex:Int;
	private var window:DialogWindow;

	private var useImage:Bool;
	private var selectionImg:BitmapData;
	private var selectionWindow:DialogWindow;

	private var choiceImg:BitmapData;
	private var upElapsed:Int;
	private var downElapsed:Int;
	private var scrolling:Int;
	private var scrollElapsed:Int;
	private var windowPause:Int;
	private var selectedTarget:String;

	#if unity
	private var style:DialogOptions;

	public var windowTemplate:WindowTemplate;
	public var windowFont:Font;
	public var cursorType:String;
	public var cursorImage:Null<BitmapData>;
	public var cursorOffset:Point;
	public var cursorWindow:Null<WindowTemplate>;
	public var choiceLayout:String;
	public var selectButton:String;
	public var scrollWait:Int;
	public var scrollDuration:Int;
	public var appearSound:Null<Sound>;
	public var changeSound:Null<Sound>;
	public var confirmSound:Null<Sound>;
	public var itemPadding:Int;
	public var inactiveTime:Int;
	#elseif stencyl
	private var style:dialog.ds.ext.DialogOptions;
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

		name = "Dialog Options";

		visible = false;
		options = new Array<String>();
		targets = new Array<String>();
		coords = new Array<Point>();
		curCoord = new Point(0, 0);
		usedFont = null;
		selectedIndex = -1;
		window = null;
		useImage = false;
		selectionImg = null;
		selectionWindow = null;
		choiceImg = null;
		upElapsed = 0;
		downElapsed = 0;
		scrolling = 0;
		scrollElapsed = 0;
		windowPause = 0;
		selectedTarget = "";

		cmds =
		[
			"option"=>option,
			"choice"=>option,
		];

		addCallback(Dialog.ALWAYS, function():Void
		{
			if(!visible || window == null)
				return;

			window.update();

			if(windowPause > 0)
			{
				windowPause -= 10;
				return;
			}

			if(window.tween != null)
				return;

			if(scrolling == 0)
			{
				if(Input.check("up") || Input.check("left"))
				{
					downElapsed = 0;
					upElapsed += 10;
					if(upElapsed > style.scrollWait)
					{
						upElapsed = 0;
						scrolling = -1;
					}
				}
				else if(Input.check("down") || Input.check("right"))
				{
					upElapsed = 0;
					downElapsed += 10;
					if(downElapsed > style.scrollWait)
					{
						downElapsed = 0;
						scrolling = 1;
					}
				}
				else
				{
					upElapsed = downElapsed = 0;
				}
			}
			else
			{
				if(scrolling == -1 && !(Input.check("up") || Input.check("left")))
					scrolling = 0;
				else if(scrolling == 1 && !(Input.check("down") || Input.check("right")))
					scrolling = 0;
				else
				{
					scrollElapsed += 10;
					if(scrollElapsed > style.scrollDuration)
					{
						scrollElapsed = 0;
						moveSelection(scrolling);
					}
				}
			}

			if(scrolling == 0)
			{
				if(Input.pressed("up") || Input.pressed("left"))
				{
					moveSelection(-1);
				}
				else if(Input.pressed("down") || Input.pressed("right"))
				{
					moveSelection(1);
				}
			}
			if(Input.pressed(style.selectButton))
			{
				targetSelected();
			}
		});
		addDrawCallback("Dialog Choice", function():Void
		{
			if(!visible || window == null)
				return;

			window.draw();

			if(window.tween == null)
			{
				if(useImage)
					G2.drawImage(selectionImg, Std.int(curCoord.x + style.cursorOffset.x), Std.int(curCoord.y + style.cursorOffset.y));
				else
					selectionWindow.draw();

				G2.drawImage(choiceImg, Std.int(window.position.x + window.template.insets.left), Std.int(window.position.y + window.template.insets.top), false);
			}
		});
	}

	public function option(args:Array<Array<Dynamic>>):Void
	{
		window = new DialogWindow(style.windowTemplate);

		visible = true;
		selectedIndex = 0;

		for(arg in args)
		{
			if(arg.length < 3 || arg[2] == true)
			{
				options.push(arg[0]);
				targets.push(arg[1]);
			}
		}

		useImage = (style.cursorType == "Use Image");
		if(useImage)
			selectionImg = style.cursorImage; //Used to be .clone()?
		else
			selectionWindow = new DialogWindow(style.cursorWindow);

		var f:DialogFont = usedFont = DialogFont.get(style.windowFont);

		//Figure out dimensions
		var w:Int = 0;
		var h:Int = 0;
		var i:Int = 0;
		while(i < options.length)
		{
			if(style.choiceLayout == "Vertical")
			{
				h += f.info.scaledLineHeight + G2.s(style.itemPadding);
				if(f.info.getScaledWidth(options[i]) > w)
					w = f.info.getScaledWidth(options[i]);
			}
			else
			{
				w += f.info.getScaledWidth(options[i]) + G2.s(style.itemPadding);
			}
			++i;
		}
		if(style.choiceLayout == "Horizontal")
		{
			w -= G2.s(style.itemPadding);
			h = f.info.scaledLineHeight + f.info.belowBase;
		}
		else
		{
			h -= G2.s(style.itemPadding) - f.info.belowBase;
		}

		//Write text
		choiceImg = BitmapDataUtil.newTransparentImg(w, h);
		var x:Int = 0;
		var y:Int = 0;
		for(i in 0...options.length)
		{
			for(j in 0...options[i].length)
			{
				choiceImg.drawChar(options[i].charAt(j), f, x, y);
				x += f.info.getScaledAdvance(options[i].charAt(j));
			}

			if(style.choiceLayout == "Vertical")
			{
				x = 0;
				y += f.info.scaledLineHeight + G2.s(style.itemPadding);
			}
			else
				x += G2.s(style.itemPadding);
		}

		window.setContentSize(G2.us(choiceImg.width), G2.us(choiceImg.height));
		updateSelectionGraphic();

		dg.paused = true;

		windowPause = style.inactiveTime;

		window.tweenCompleteNotify.push(function():Void
		{
			setCoords();
		});
		window.applyTween(style.windowTemplate.createTween);

		var snd:Sound = style.appearSound;
		if(snd != null)
			Script.playSound(snd);
	}

	public function setCoords():Void
	{
		coords = new Array<Point>();
		var f:DialogFont = usedFont;

		var x:Int = Std.int(window.position.x + window.template.insets.left);
		var y:Int = Std.int(window.position.y + window.template.insets.top);

		for(i in 0...options.length)
		{
			coords.push(new Point(x, y));
			if(style.choiceLayout == "Vertical")
				y += f.info.lineHeight + style.itemPadding;
			else
				x += f.info.getWidth(options[i]) + style.itemPadding;
		}

		if(useImage)
			curCoord = new Point(coords[0].x, coords[0].y);
		else
			selectionWindow.setContentPos(Std.int(coords[0].x), Std.int(coords[0].y));
	}

	public function moveSelection(dir:Int):Void
	{
		if(dir == -1)
		{
			if(selectedIndex > 0)
				selectedIndex -= 1;
			else
				selectedIndex = options.length - 1;
			tweenSelection();
		}
		else if(dir == 1)
		{
			if(selectedIndex < options.length - 1)
				selectedIndex += 1;
			else
				selectedIndex = 0;
			tweenSelection();
		}

		var snd:Sound = style.changeSound;
		if(snd != null)
			Script.playSound(snd);
	}

	private function tweenSelection():Void
	{
		if(useImage)
		{
			curCoord.x = coords[selectedIndex].x;
			curCoord.y = coords[selectedIndex].y;
		}
		else
			selectionWindow.setContentPos(Std.int(coords[selectedIndex].x), Std.int(coords[selectedIndex].y));

		updateSelectionGraphic();
	}

	private function updateSelectionGraphic():Void
	{
		var f:DialogFont = usedFont;
		var itemDim:IntPoint = new IntPoint(G2.us(choiceImg.width), G2.us(choiceImg.height));
		if(style.choiceLayout == "Vertical")
			itemDim.y = f.info.lineHeight;
		else
			itemDim.x = f.info.getWidth(options[selectedIndex]);

		if(!useImage)
			selectionWindow.setContentSize(itemDim.x, itemDim.y);
	}

	private function targetSelected():Void
	{
		selectedTarget = targets[selectedIndex];

		var snd:Sound = style.confirmSound;
		if(snd != null)
			Script.playSound(snd);

		window.tweenCompleteNotify.push(function():Void
		{
			visible = false;
			options = new Array<String>();
			targets = new Array<String>();
			coords = new Array<Point>();
			curCoord = new Point(0, 0);
			usedFont = null;
			selectedIndex = -1;
			selectionImg = null;
			selectionWindow = null;
			choiceImg = null;

			var t:String = selectedTarget;
			selectedTarget = "";

			dg.goToDialog(t);
		});
		window.applyTween(style.windowTemplate.destroyTween);
	}
}
