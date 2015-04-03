package dialog.ext;

#if stencyl

import com.stencyl.models.Actor;
import com.stencyl.models.Sound;
import com.stencyl.behavior.Script;
import com.stencyl.Engine;
import com.stencyl.Input;

#elseif unity

import cs.NativeArray;

import dialog.unity.compat.Typedefs;
import dialog.unity.compat.*;
import unityengine.Object;

using dialog.unity.extension.NativeArrayUtil;
using dialog.unity.extension.GameObjectUtil;

#end

import dialog.core.*;
import dialog.ds.Typedefs;

class FlowScripts extends DialogExtension
{
	//Communicates with SkipScripts

	private var waitingForAttribute:Bool;
	private var waitElapsed:Int;
	private var source:Array<Dynamic>;
	private var clearAfterInput:Bool;
	private var pointer:AnimatedImage;

	public var noInputSoundWithTags:Array<String>;

	private var skipScripts:SkipScripts;

	#if unity
	private var style:FlowScripts;

	public var animForPointer:String;
	public var pointerPos:RatioPoint;
	public var advanceDialogButton:String;
	public var waitingSound:Null<Sound>;
	public var waitingSoundInterval:Int;
	public var inputSound:Null<Sound>;
	//public var noInputSoundWithTags:Array<String>;
	#end

	public function new()
	{
		super();

		#if unity
		style = this;
		#end
	}

	override public function setup(dg:DialogBox)
	{
		super.setup(dg);

		name = "Flow Scripts";

		waitingForAttribute = false;
		waitElapsed = 0;
		source = new Array<Dynamic>();
		clearAfterInput = false;
		pointer = null;

		cmds =
		[
			"waitattr"=>waitattr,
			"waitvar"=>waitattr,
			"wait"=>wait,
			"but"=>but,
			"bc"=>bc
		];

		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			skipScripts = cast(dg.getExt("Skip Scripts"), SkipScripts);
			#if stencyl
			noInputSoundWithTags = [];
			for(tagname in style.noInputSoundWithTags)
				noInputSoundWithTags.push("" + tagname);
			#end
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			if(waitingForAttribute)
			{
				var done:Bool = analyzeAttr(source) == source[source.length - 1];
				if(done)
				{
					waitingForAttribute = false;
					dg.paused = false;
				}
			}
			if(pointer != null)
			{
				if(style.waitingSoundInterval > 0)
				{
					waitElapsed += Engine.STEP_SIZE;

					if(waitElapsed >= style.waitingSoundInterval)
					{
						waitElapsed = 0;
						var snd:Sound = style.waitingSound;
						if(snd != null)
							Script.playSound(snd);
					}
				}

				if(Input.pressed(style.advanceDialogButton) || skipLevel(2))
				{
					dg.paused = false;
					pointer.end(#if unity dg #end);
					pointer = null;
					if(clearAfterInput)
					{
						dg.clearMessage();
						clearAfterInput = false;
					}

					waitElapsed = 0;

					if(!skipLevel(2) && !nearCancelingTag())
					{
						var snd:Sound = style.inputSound;
						if(snd != null)
							Script.playSound(snd);
					}
				}
			}
		});
		addDrawCallback("Wait Pointer", function():Void
		{
			if(pointer == null)
				return;

			pointer.draw(Std.int(style.pointerPos.xp * Engine.screenWidth + style.pointerPos.xv), Std.int(style.pointerPos.yp * Engine.screenHeight + style.pointerPos.yv));
		});
	}

	//copied from MessagingScripts
	public function analyzeAttr(source:Array<Dynamic>):Dynamic
	{
		switch("" + source[0])
		{
			case "game", "global":
				return Script.getGameAttribute("" + source[1]);
			case "actorbhv", "go":
				return GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			#if stencyl
			case "scenebhv":
				return Script.getValueForScene("" + source[1], "" + source[2]);
			case "actor":
				return GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
			#end
		}
		return null;
	}

	public function waitattr(source:Array<Dynamic>):Void
	{
		dg.paused = true;
		waitingForAttribute = true;
		this.source = source;
	}

	public function wait(duration:Float):Void
	{
		dg.typeDelay = Std.int(duration * 1000);
	}

	public function but():Void
	{
		dg.paused = true;
		pointer = new AnimatedImage(style.animForPointer);
		pointer.start(#if unity dg #end);

		if(!skipLevel(2))
		{
			var snd:Sound = style.waitingSound;
			if(snd != null)
				Script.playSound(snd);
		}
	}

	public function bc():Void
	{
		but();
		clearAfterInput = true;
	}

	private function nearCancelingTag():Bool
	{
		var cancelTagFound:Bool = false;

		var i:Int = dg.typeIndex + 1;
		while(i < dg.msg.length)
		{
			if(Std.is(dg.msg[i], String))
			{
				var s:String = dg.msg[i];
				if(!(s == " " || s == "\r" || s == "\n" || s == "\t"))
					return false;
			}
			else
			{
				var cmdName = cast(dg.msg[i], Tag).name;

				if(cmdName == "wait")
					return false;

				for(cancelTag in noInputSoundWithTags)
				{
					if(cmdName == cancelTag)
					{
						return true;
					}
				}
			}
			++i;
		}

		return false;
	}

	private function skipLevel(level:Int):Bool
	{
		if(skipScripts == null)
			return false;

		return skipScripts.getCurrentSkipLevel() == level;
	}
}
