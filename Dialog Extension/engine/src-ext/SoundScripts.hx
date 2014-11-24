/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;

class SoundScripts extends DialogExtension
{
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Sound Scripts";
		
		cmds =
		[
			"playsound"=>playsound,
			"loopsound"=>loopsound,
			"stopsound"=>stopsound,
			"playchan"=>playchan,
			"loopchan"=>loopchan,
			"stopchan"=>stopchan
		];
	}
	
	public function playsound(sound:String):Void
	{
		s.playSound(Util.sound(sound));
	}
	
	public function loopsound(sound:String):Void
	{
		s.loopSound(Util.sound(sound));
	}
	
	public function stopsound():Void
	{
		s.stopAllSounds();
	}
	
	public function playchan(sound:String, channel:Int):Void
	{
		s.playSoundOnChannel(Util.sound(sound), channel);
	}
	
	public function loopchan(sound:String, channel:Int):Void
	{
		s.loopSoundOnChannel(Util.sound(sound), channel);
	}
	
	public function stopchan(channel:Int):Void
	{
		s.stopSoundOnChannel(channel);
	}
}