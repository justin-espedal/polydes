/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import com.stencyl.Data;

class TypingScripts extends DialogExtension
{
	public var typeSound:Sound;
	public var stopTypeSound:Bool;
	public var typeSoundDelay = 0;
	public var curSoundDelay = 0;
	public var typeSoundArray:Array<Dynamic>;
	public var storedTypeSound:Array<Dynamic>;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Typing Scripts";
		
		typeSound = null;
		stopTypeSound = false;
		typeSoundDelay = style.characterSkipSFX;
		
		cmds =
		[
			"font"=>typefont,
			"color"=>typecolor,
			"typespeed"=>typespeed,
			"typesound"=>setTypeSound,
			"soundskip"=>setTypeSoundSkip
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			//typeSound = style.defaultTypeSound;
			typeSoundArray = style.defaultRandomTypeSounds;
			storedTypeSound = typeSoundArray;
		});
		addCallback(Dialog.WHEN_CHAR_TYPED, function():Void
		{
			if(!stopTypeSound && (style.playTypeSoundOnSpaces || dg.msg[dg.typeIndex] != " "))
			{
				if(typeSoundArray.length > 0)
					typeSound = cast Data.get().resourceMap.get(typeSoundArray[Std.random(typeSoundArray.length)]);
				if(typeSound != null && curSoundDelay-- == 0)
				{
					Script.playSound(typeSound);
					curSoundDelay = typeSoundDelay;
				}
			}
		});
	}
	
	public function typefont(fontName:String):Void
	{
		dg.msgFont = DialogFont.get(Util.font(fontName));
	}
	
	public function typecolor(fontColor:Int):Void
	{
		dg.msgColor = fontColor;
	}
	
	public function typespeed(speed:Float):Void
	{
		dg.msgTypeSpeed = speed;
	}
	
	public function setTypeSound(sound:Dynamic):Void
	{
		if(sound == "none")
			typeSoundArray = new Array<Dynamic>();
		else
		{
			if(Std.is(sound, String))
			{
				typeSoundArray = new Array<Dynamic>();
				typeSoundArray.push(sound);
				storedTypeSound = typeSoundArray;
			}
			else if(Std.is(sound, Array))
			{
				typeSoundArray = sound;
				storedTypeSound = typeSoundArray;
			}
		}
	}

	public function setTypeSoundSkip(numToSkip:Int):Void
	{
		typeSoundDelay = numToSkip;
	}
}