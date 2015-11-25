package dialog.ext;

#if stencyl

import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import openfl.geom.Rectangle;

#elseif unity

import cs.NativeArray;
import dialog.unity.compat.Typedefs;
import dialog.unity.compat.*;
import unityeditor.*;
import unityengine.*;

#end

import dialog.ds.*;
import dialog.core.*;

class DialogBase extends dialog.core.DialogExtension
{
	private var window:Null<DialogWindow>;
	private var messageBegan:Bool;

	#if unity
	private var style:DialogBase;

	public var msgWindow:Null<WindowTemplate>;
	public var msgBounds:Rectangle;
	public var msgFont:Font;
	public var msgTypeSpeed:Float;
	public var msgStartSound:Null<Sound>;
	public var controlAttribute:String;
	public var lineSpacing:Int;
	public var charSpacing:Int;
	public var clearSound:Null<Sound>;
	public var closeSound:Null<Sound>;
	public var endSound:Null<Sound>;
	#elseif stencyl
	private var style:dialog.ds.ext.DialogBase;
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

		name = "Dialog Base";

		window = null;
		messageBegan = false;

		cmds =
		[
			"br"=>br,
			"end"=>end,
			"clear"=>clear,
			"close"=>close,
			"dg"=>dgGo
		];

		if(style.msgWindow != null)
		{

			addCallback(Dialog.WHEN_CREATED, function():Void
			{
				dg.paused = true;

				window = new DialogWindow(style.msgWindow);
				if(style.msgBounds != null)
					window.setContentSize(dg.msgW, dg.msgH);
				window.tweenCompleteNotify.push(function():Void
				{
					dg.paused = false;
				});
				window.applyTween(style.msgWindow.createTween);
			});
			addCallback(Dialog.ALWAYS, function():Void
			{
				window.update();
			});

		}

		addCallback(Dialog.WHEN_MESSAGE_BEGINS, function():Void
		{
			if(!messageBegan)
			{
				messageBegan = true;
				var snd:Sound = style.msgStartSound;
				if(snd != null)
					Script.playSound(snd);
			}

			Script.setGameAttribute(style.controlAttribute, true);
		});
		addCallback(Dialog.WHEN_MESSAGE_ENDS, function():Void
		{
			Script.setGameAttribute(style.controlAttribute, false);
			messageBegan = false;
		});

		addCallback(Dialog.RESTORE_DEFAULTS, function():Void
		{
			#if stencyl
			if(style.msgBounds == null)
			{
				if(window != null)
				{
					dg.msgX = Std.int(window.position.x + window.template.insets.left);
					dg.msgY = Std.int(window.position.y + window.template.insets.top);
					dg.msgW = Std.int(window.size.x - window.template.insets.left - window.template.insets.right);
					dg.msgH = Std.int(window.size.y - window.template.insets.top - window.template.insets.bottom);
				}
			}
			else
			{
			#end
				dg.msgX = Std.int(style.msgBounds.x);
				dg.msgY = Std.int(style.msgBounds.y);
				dg.msgW = Std.int(style.msgBounds.width);
				dg.msgH = Std.int(style.msgBounds.height);
			#if stencyl
			}
			#end
			dg.defaultBounds = new Rectangle(dg.msgX, dg.msgY, dg.msgW, dg.msgH);
			dg.msgColor = -1;
			dg.msgFont = DialogFont.get(style.msgFont);
			dg.msgTypeSpeed = style.msgTypeSpeed;
			dg.charSpacing = style.charSpacing;
			dg.lineSpacing = style.lineSpacing;
		});

		addDrawCallback("Window Frame", function():Void
		{
			if(window != null)
				window.draw();
		});

		addDrawCallback("Message", function():Void
		{
			for(line in dg.lines)
			{
				G2.drawImage(line.img, line.pos.x, line.pos.y, false);
			}
		});
	}

	public function br():Void
	{
		dg.startNextLine();
	}

	public function end():Void
	{
		dg.clearMessage();

		var snd:Sound = style.endSound;
		if(snd != null)
			Script.playSound(snd);

		if(window != null)
		{
			window.tweenCompleteNotify.push(function():Void
			{
				dg.endMessage();
			});
			window.applyTween(style.msgWindow.destroyTween);
		}
		else
		{
			dg.endMessage();
		}
	}

	public function clear():Void
	{
		dg.clearMessage();
		var snd:Sound = style.clearSound;
		if(snd != null)
			Script.playSound(snd);
	}

	public function close():Void
	{
		dg.closeMessage();
		var snd:Sound = style.closeSound;
		if(snd != null)
			Script.playSound(snd);
	}

	public function dgGo(toCall:String):Void
	{
		dg.goToDialog(toCall);
	}

	// Member Access

	public function getWindow():DialogWindow
	{
		return window;
	}

	public function getStyle(): #if stencyl dialog.ds.ext.DialogBase #elseif unity DialogBase #end
	{
		return style;
	}
}
