/**
 * @author Justin Espedal
 */
import com.stencyl.models.Sound;

class SkipScripts extends DialogExtension
{
	//Communicates with TypingScripts

	public var msgSkippable:Bool;
	public var curSkipLevel:Int;
	public var soundTimer:Int;
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
				if(s.isKeyPressed(style.instantButton))
				{
					var snd:Sound = style.instantSound;
					if(snd != null)
						s.playSound(snd);
				}
				if(s.isKeyDown(style.instantButton))
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
				else if(s.isKeyDown(style.zoomButton))
				{
					if(style.zoomSoundInterval > 0)
					{
						setTypingScriptSounds(null);
						
						soundTimer += 10;
						if(soundTimer >= style.zoomSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = style.zoomSound;
							if(snd != null)
								s.playSound(snd);
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
				else if(s.isKeyDown(style.fastButton))
				{
					if(style.fastSoundInterval > 0)
					{
						setTypingScriptSounds(null);
						
						soundTimer += 10;
						if(soundTimer >= style.fastSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = style.fastSound;
							if(snd != null)
								s.playSound(snd);
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
					
					setTypingScriptSounds(style.defaultTypeSound);
					
					curSkipLevel = 0;
				}
			}
			else if(curSkipLevel != 0)
			{
				curSkipLevel = 0;
				
				dg.msgTypeSpeed = restoreSpeedTo;
				setTypingScriptSounds(style.defaultTypeSound);
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

	private function setTypingScriptSounds(sound:Sound):Void
	{
		if(TypingScripts != null)
			typingScript.typeSound = sound;
	}
}