/**
 * @author Justin Espedal
 */
import com.stencyl.models.Actor;
import com.stencyl.models.Sound;
import com.stencyl.behavior.Script;
import com.stencyl.Engine;
import com.stencyl.Input;

class FlowScripts extends DialogExtension
{
	//Communicates with SkipScripts
	
	public var waitingForAttribute:Bool;
	public var waitElapsed:Int;
	public var source:Array<Dynamic>;
	public var clearAfterInput:Bool;
	public var pointer:AnimatedImage;
	
	public var noInputSoundWithTags:Array<String>;
	
	private var skipScripts:SkipScripts;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Flow Scripts";
		
		waitingForAttribute = false;
		waitElapsed = 0;
		source = new Array<Dynamic>();
		clearAfterInput = false;
		pointer = null;
		
		cmds =
		[
			"waitattr"=>waitattr,
			"wait"=>wait,
			"but"=>but,
			"bc"=>bc
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			skipScripts = cast(dg.getExt("Skip Scripts"), SkipScripts);
			noInputSoundWithTags = [];
			for(tagname in style.noInputSoundWithTags)
				noInputSoundWithTags.push("" + tagname);
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			if(waitingForAttribute)
			{
				var done:Bool = false;
				switch("" + source[0])
				{
					case "game":
						done = (Script.getGameAttribute("" + source[1]) == source[2]);
					case "scenebhv":
						done = (Script.getValueForScene("" + source[1], "" + source[2]) ==  source[3]);
					case "actorbhv":
						done = (GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]) ==  source[4]);
					case "actor":
						done = (GlobalActorID.get("" + source[1]).getActorValue("" + source[2]) ==  source[3]);
				}
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
					waitElapsed += 10;
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
					pointer.end();
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
		pointer.start();
		
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

		return skipScripts.curSkipLevel == level;
	}
}