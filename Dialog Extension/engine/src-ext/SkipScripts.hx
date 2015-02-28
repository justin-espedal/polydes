/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import com.stencyl.Input;
import com.stencyl.Data;

class SkipScripts extends DialogExtension
{
	//Communicates with TypingScripts

	public var msgSkippable:Bool;
	public var curSkipLevel:Int;
	public var soundTimer:Float;
	public var restoreSpeedTo:Float;
	
	public var typingScript:TypingScripts;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Skip Scripts";
		
		msgSkippable = true;
		curSkipLevel = 0;
		soundTimer = 0;
		restoreSpeedTo = 0;
		
		//skip levels: default, fast skip, zoom skip, instant skip
		
		cmds =
		[
			"skip"=>skip,
			"noskip"=>noskip
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			msgSkippable = style.skippableDefault;
			typingScript = cast(dg.getExt("Typing Scripts"), TypingScripts);
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			if(msgSkippable)
			{
				// Make the zoom/fast sound play immediately
				if(Input.pressed(style.fastButton) || Input.pressed(style.zoomButton))
				{
					soundTimer = Math.max(style.fastSoundInterval, style.zoomSoundInterval);
				}

				if(Input.pressed(style.instantButton))
				{
					var snd:Sound = style.instantSound;
					if(snd != null)
						Script.playSound(snd);
				}
				if(Input.check(style.instantButton))
				{
					setTypingScriptSounds(null);
					
					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;
						
					curSkipLevel = 3;
					
					dg.msgTypeSpeed = 0;
					
					dg.stepTimer = dg.typeDelay = 0;
				}
				else if(Input.check(style.zoomButton))
				{
					if(style.zoomSoundInterval > 0)
					{
						setTypingScriptSounds(null);
						
						soundTimer += 10;
						if(soundTimer >= style.zoomSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = cast Data.get().resourceMap.get(style.zoomSound[Std.random(style.zoomSound.length)]);
							if(snd != null)
								Script.playSound(snd);
						}
					}
					else 
						setTypingScriptSounds(style.zoomSound);
					
					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;
					
					curSkipLevel = 2;
					
					dg.stepTimer = dg.typeDelay = Std.int(style.zoomSpeed * 1000);
					
					dg.msgTypeSpeed = style.zoomSpeed;
				}
				else if(Input.check(style.fastButton))
				{
					if(style.fastSoundInterval > 0)

					{
						setTypingScriptSounds(null);
						
						soundTimer += 10;
						if(!(dg.typeDelay > style.fastSpeed*1000) && soundTimer >= style.fastSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = cast Data.get().resourceMap.get(style.fastSound[Std.random(style.fastSound.length)]);
							if(snd != null)
								Script.playSound(snd);
						}
					}
					else
						setTypingScriptSounds(style.fastSound);
					
					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;
					
					curSkipLevel = 1;
					
					dg.msgTypeSpeed = style.fastSpeed;
				}
				else if(curSkipLevel != 0)
				{
					soundTimer = 0;
					
					dg.msgTypeSpeed = restoreSpeedTo;
					
					setTypingScriptSounds(typingScript.storedTypeSound);
					
					curSkipLevel = 0;

				}
			}
			else if(curSkipLevel != 0)
			{
				curSkipLevel = 0;
				
				dg.msgTypeSpeed = restoreSpeedTo;
				setTypingScriptSounds(typingScript.storedTypeSound);
			}
		});
	}
	
	public function skip():Void
	{
		msgSkippable = true;
	}
	
	public function noskip():Void
	{
		msgSkippable = false;
		curSkipLevel = 0;
	}

	private function setTypingScriptSounds(sound:Dynamic):Void
	{
		if(TypingScripts != null)
		{
			if(sound != null)
			{
				typingScript.typeSoundArray = sound;
			}
			else
			{
				typingScript.typeSoundArray = new Array<Dynamic>();
			}
		}
	}
}