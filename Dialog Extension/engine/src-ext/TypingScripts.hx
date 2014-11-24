/**
 * @author Justin Espedal
 */
import com.stencyl.models.Sound;

class TypingScripts extends DialogExtension
{
	public var typeSound:Sound;
	public var stopTypeSound:Bool;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Typing Scripts";
		
		typeSound = null;
		stopTypeSound = false;
		
		cmds =
		[
			"font"=>typefont,
			"color"=>typecolor,
			"typespeed"=>typespeed,
			"typesound"=>setTypeSound
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			typeSound = style.defaultTypeSound;
		});
		addCallback(Dialog.WHEN_CHAR_TYPED, function():Void
		{
			if(!stopTypeSound && dg.msg[dg.typeIndex] != " ")
			{
				if(typeSound != null)
					s.playSound(typeSound);
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
			typeSound = null;
		else
			typeSound = Util.sound(sound);
	}
}