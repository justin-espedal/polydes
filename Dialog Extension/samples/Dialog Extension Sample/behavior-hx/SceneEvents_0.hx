package scripts;

import com.stencyl.graphics.G;
import com.stencyl.graphics.BitmapWrapper;

import com.stencyl.behavior.Script;
import com.stencyl.behavior.Script.*;
import com.stencyl.behavior.ActorScript;
import com.stencyl.behavior.SceneScript;
import com.stencyl.behavior.TimedTask;

import com.stencyl.models.Actor;
import com.stencyl.models.GameModel;
import com.stencyl.models.actor.Animation;
import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Collision;
import com.stencyl.models.actor.Group;
import com.stencyl.models.Scene;
import com.stencyl.models.Sound;
import com.stencyl.models.Region;
import com.stencyl.models.Font;

import com.stencyl.Engine;
import com.stencyl.Input;
import com.stencyl.Key;
import com.stencyl.utils.Utils;

import openfl.ui.Mouse;
import openfl.display.Graphics;
import openfl.display.BlendMode;
import openfl.display.BitmapData;
import openfl.display.Bitmap;
import openfl.events.Event;
import openfl.events.KeyboardEvent;
import openfl.events.TouchEvent;
import openfl.net.URLLoader;

import box2D.common.math.B2Vec2;
import box2D.dynamics.B2Body;
import box2D.dynamics.B2Fixture;
import box2D.dynamics.joints.B2Joint;
import box2D.collision.shapes.B2Shape;

import motion.Actuate;
import motion.easing.Back;
import motion.easing.Cubic;
import motion.easing.Elastic;
import motion.easing.Expo;
import motion.easing.Linear;
import motion.easing.Quad;
import motion.easing.Quart;
import motion.easing.Quint;
import motion.easing.Sine;

import com.stencyl.graphics.shaders.BasicShader;
import com.stencyl.graphics.shaders.GrayscaleShader;
import com.stencyl.graphics.shaders.SepiaShader;
import com.stencyl.graphics.shaders.InvertShader;
import com.stencyl.graphics.shaders.GrainShader;
import com.stencyl.graphics.shaders.ExternalShader;
import com.stencyl.graphics.shaders.InlineShader;
import com.stencyl.graphics.shaders.BlurShader;
import com.stencyl.graphics.shaders.SharpenShader;
import com.stencyl.graphics.shaders.ScanlineShader;
import com.stencyl.graphics.shaders.CSBShader;
import com.stencyl.graphics.shaders.HueShader;
import com.stencyl.graphics.shaders.TintShader;
import com.stencyl.graphics.shaders.BloomShader;



class SceneEvents_0 extends SceneScript
{
	
public var _AlreadyStarted:Bool;

public var _controls:Array<Dynamic>;

 
 	public function new(dummy:Int, dummy2:Engine)
	{
		super();
		nameMap.set("Already Started", "_AlreadyStarted");
_AlreadyStarted = false;
nameMap.set("controls", "_controls");
_controls = [];

	}
	
	override public function init()
	{
		    
/* ======================== When Creating ========================= */
        _AlreadyStarted = false;
propertyChanged("_AlreadyStarted", _AlreadyStarted);
    
/* ======================== When Creating ========================= */
        #if (windows || linux || mac)
            Input.enableJoystick();
            Input.mapJoystickButton("0, 0", "z");
            Input.mapJoystickButton("0, 1", "x");
            Input.mapJoystickButton("0, 2", "c");
            Input.mapJoystickButton("0, 3", "v");
            Input.mapJoystickButton("0, up hat", "up");
            Input.mapJoystickButton("0, down hat", "down");
            Input.mapJoystickButton("0, left hat", "left");
            Input.mapJoystickButton("0, right hat", "right");
            Input.mapJoystickButton("0, 4", "enter");
#end
    
/* ======================== When Creating ========================= */
        #if mobile
            _controls = new Array<Dynamic>();
propertyChanged("_controls", _controls);
            for(actorOfType in getActorsOfType(getActorType(34)))
{
if(actorOfType != null && !actorOfType.dead && !actorOfType.recycled){
                _controls.push(actorOfType);
}
}

#end
    
/* ======================== When Updating ========================= */
addWhenUpdatedListener(null, function(elapsedTime:Float, list:Array<Dynamic>):Void
{
if(wrapper.enabled)
{
        #if mobile
            for(item in cast(_controls, Array<Dynamic>))
{
                item.say("Touch Control", "_customEvent_" + "checkInput");
}

#end
}
});
    
/* =========================== Keyboard =========================== */
addKeyStateListener("enter", function(pressed:Bool, released:Bool, list:Array<Dynamic>):Void
{
if(wrapper.enabled && pressed)
{
        if(!(_AlreadyStarted))
{
            _AlreadyStarted = true;
propertyChanged("_AlreadyStarted", _AlreadyStarted);
            Dialog.cbCall("Start", "base", this, "");
}

        else if(!(Engine.engine.getGameAttribute("Dialog Open")))
{
            Dialog.cbCall("Welcome Back", "base", this, "");
}

}
});

	}	      	
	
	override public function forwardMessage(msg:String)
	{
		
	}
}