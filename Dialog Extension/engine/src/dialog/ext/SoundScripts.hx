package dialog.ext;

#if stencyl

import com.stencyl.behavior.Script;

#elseif unity

import dialog.unity.compat.*;
import unityengine.*;

#end

import dialog.core.*;

class SoundScripts extends DialogExtension
{
	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox)
	{
		super.setup(dg);

		name = "Sound Scripts";

		cmds =
		[
			"playsound"=>playsound #if stencyl,
			"loopsound"=>loopsound,
			"stopsound"=>stopsound,
			"playchan"=>playchan,
			"loopchan"=>loopchan,
			"stopchan"=>stopchan #end
		];
	}

	public function playsound(sound:String):Void
	{
		Script.playSound(Util.sound(sound));
	}

	#if stencyl

	public function loopsound(sound:String):Void
	{
		Script.loopSound(Util.sound(sound));
	}

	public function stopsound():Void
	{
		Script.stopAllSounds();
	}

	public function playchan(sound:String, channel:Int):Void
	{
		Script.playSoundOnChannel(Util.sound(sound), channel);
	}

	public function loopchan(sound:String, channel:Int):Void
	{
		Script.loopSoundOnChannel(Util.sound(sound), channel);
	}

	public function stopchan(channel:Int):Void
	{
		Script.stopSoundOnChannel(channel);
	}

	#end
}
