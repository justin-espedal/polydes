package dialog.ext;

#if stencyl

import nme.display.BitmapData;
import nme.geom.Point;

#elseif unity

import dialog.unity.compat.G2;
import dialog.unity.compat.Typedefs;
import unityengine.*;

using dialog.unity.extension.VectorUtil;

#end

import dialog.core.*;

using dialog.util.BitmapDataUtil;

class TextEffects extends dialog.core.DialogExtension implements DrawHandler
{
	private static inline var SHAKE_FLAG = 1;
	private static inline var SINE_FLAG = 2;
	private static inline var REVOLVE_FLAG = 4;
	private static inline var GROW_FLAG = 8;

	private var imgs:Map<Int, DrawnImage>;
	private var unusedIDs:Array<Int>;
	private var curRange:Int;

	private var shakeOn:Bool;
	private var sineOn:Bool;
	private var revolveOn:Bool;
	private var growOn:Bool;

	#if unity
	private var style:TextEffects;

	public var v_maxShakeOffsetX:Int;
	public var v_maxShakeOffsetY:Int;
	public var v_shakeFrequency:Int;
	public var s_magnitude:Int;
	public var s_frequency:Int;
	public var s_pattern:Int;
	public var r_diameter:Int;
	public var r_frequency:Int;
	public var r_pattern:Int;
	public var g_start:Float;
	public var g_stop:Float;
	public var g_duration:Int;
	#elseif stencyl
	private var style:dialog.ds.ext.TextEffects;
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

		//Set up as a DrawHandler
		imgs = new Map<Int, DrawnImage>();
		unusedIDs = new Array<Int>();
		curRange = 0;

		name = "Text Effects";

		shakeOn = false;
		sineOn = false;
		revolveOn = false;
		growOn = false;

		cmds =
		[
			"shake"=>shake,
			"/shake"=>endshake,
			"sine"=>sine,
			"/sine"=>endsine,
			"revolve"=>revolve,
			"/revolve"=>endrevolve,
			"grow"=>grow,
			"/grow"=>endgrow
		];

		addCallback(Dialog.ALWAYS, function():Void
		{
			var draw:DrawnImage;

			for(key in imgs.keys())
			{
				draw = imgs.get(key);

				if((draw.flags & SHAKE_FLAG) > 0)
				{
					draw.shakeElapsed += 10;
					if(draw.shakeElapsed >= style.v_shakeFrequency)
					{
						draw.shakeElapsed = 0;
						draw.shakeOffset.x = Std.random(style.v_maxShakeOffsetX + 1);
						draw.shakeOffset.y = Std.random(style.v_maxShakeOffsetY + 1);
					}
				}
				if((draw.flags & SINE_FLAG) > 0)
				{
					draw.sineElapsed += 10;
					if(draw.sineElapsed >= style.s_frequency)
					{
						draw.sineElapsed = 0;
					}

					draw.sineOffset.y = sin(draw.sineElapsed / style.s_frequency * 2 * PI) * style.s_magnitude;
				}
				if((draw.flags & REVOLVE_FLAG) > 0)
				{
					draw.revolveElapsed += 10;
					if(draw.revolveElapsed >= style.r_frequency)
					{
						draw.revolveElapsed = 0;
					}

					draw.revolveOffset.x = cos(draw.revolveElapsed / style.r_frequency * 2 * PI) * style.r_diameter;
					draw.revolveOffset.y = sin(draw.revolveElapsed / style.r_frequency * 2 * PI) * style.r_diameter;
				}
				if((draw.flags & GROW_FLAG) > 0)
				{
					draw.growElapsed += 10;

					if(draw.growElapsed >= style.g_duration)
					{
						draw.growElapsed = 0;
						draw.flags -= GROW_FLAG;
					}
				}
			}
		});

		addDrawCallback("Text Effects", function():Void
		{
			var draw:DrawnImage;
			var p:Point;

			for(key in imgs.keys())
			{
				draw = imgs.get(key);

				p = draw.basePos.add(draw.shakeOffset).add(draw.sineOffset).add(draw.revolveOffset);
				if((draw.flags & GROW_FLAG) > 0)
				{
					var s:Float = style.g_start + (style.g_stop - style.g_start) * (draw.growElapsed / style.g_duration);
					var w:Int = draw.img.width;
					var h:Int = draw.img.height;

					p = p.add(new Point(-w / 2 * (s - 1), -h / 2 * (s - 1)));

					#if unity
					Graphics.DrawTexture(new Rect(p.x, p.y, w * s, h * s), draw.img);
					#else
					G2.drawImage(draw.img.getScaled(s, s), Std.int(p.x), Std.int(p.y), false, false);
					#end
				}
				else
					G2.drawImage(draw.img, Std.int(p.x), Std.int(p.y), false, false);
			}
		});
	}

	public static inline var PI:Float = 3.14;

	public static inline function sin(x:Float):Float
	{
		if(x > PI)
		{
			x -= (2 * PI);
			return (4 * x / PI) + (4 * x * x / PI / PI);
		}
		else
			return (4 * x / PI) - (4 * x * x / PI / PI);
	}

	public static inline function cos(x:Float):Float
	{
		x += PI / 2;
		if(x > 2 * PI)
			x -= (2 * PI);
		return sin(x);
	}

	private var lastAdded:Int = -1;

	public function addImg(img:BitmapData, x:Int, y:Int, scale:Bool = true):Int
	{
		var nextID:Int;

		if(unusedIDs.length == 0)
			nextID = curRange++;
		else
			nextID = unusedIDs.pop();

		var flags = (shakeOn ? SHAKE_FLAG : 0) + (sineOn ? SINE_FLAG : 0) + (revolveOn ? REVOLVE_FLAG : 0) + (growOn ? GROW_FLAG : 0);

		var sineElapsed:Int = 0;
		var revolveElapsed:Int = 0;

		if(lastAdded != -1 && imgs.get(lastAdded) != null)
		{
			sineElapsed = Std.int((imgs.get(lastAdded).sineElapsed + style.s_pattern) % style.s_frequency);
			revolveElapsed = Std.int((imgs.get(lastAdded).revolveElapsed + style.r_pattern) % style.r_frequency);
		}

		var draw:DrawnImage = new DrawnImage(img, flags, new Point(x, y), new Point(0, 0), new Point(0, 0), new Point(0, 0), 0, sineElapsed, revolveElapsed, 0);

		imgs.set(nextID, draw);

		lastAdded = nextID;

		return nextID;
	}

	public function moveImgTo(id:Int, x:Int, y:Int):Void
	{
		imgs.get(id).basePos = new Point(x, y);
	}

	public function moveImgBy(id:Int, x:Int, y:Int):Void
	{
		imgs.get(id).basePos = imgs.get(id).basePos.add(new Point(x, y));
	}

	public function removeImg(id:Int):Void
	{
		imgs.remove(id);
		unusedIDs.push(id);
	}

	public function clearImgs():Void
	{
		for(key in imgs.keys())
		{
			imgs.remove(key);
			unusedIDs.push(key);
		}
	}

	public function shake():Void
	{
		shakeOn = true;
		dg.drawHandler = this;
	}

	public function endshake():Void
	{
		shakeOn = false;

		if(!(sineOn || revolveOn || growOn))
		{
			dg.drawHandler = null;
		}
	}

	public function sine():Void
	{
		sineOn = true;
		dg.drawHandler = this;
	}

	public function endsine():Void
	{
		sineOn = false;

		if(!(shakeOn || revolveOn || growOn))
		{
			dg.drawHandler = null;
		}
	}

	public function revolve():Void
	{
		revolveOn = true;
		dg.drawHandler = this;
	}

	public function endrevolve():Void
	{
		revolveOn = false;

		if(!(shakeOn || sineOn || growOn))
		{
			dg.drawHandler = null;
		}
	}

	public function grow():Void
	{
		growOn = true;
		dg.drawHandler = this;
	}

	public function endgrow():Void
	{
		growOn = false;

		if(!(shakeOn || sineOn || revolveOn))
		{
			dg.drawHandler = null;
		}
	}
}

private class DrawnImage
{
	public var img:BitmapData;
	public var flags:Int;

	public var basePos:Point;
	public var shakeOffset:Point;
	public var sineOffset:Point;
	public var revolveOffset:Point;

	public var shakeElapsed:Int;
	public var sineElapsed:Int;
	public var revolveElapsed:Int;
	public var growElapsed:Int;

	public function new(img:BitmapData, flags:Int, basePos:Point, shakeOffset:Point, sineOffset:Point, revolveOffset:Point, shakeElapsed:Int, sineElapsed:Int, revolveElapsed:Int, growElapsed:Int)
	{
		this.img = img;
		this.flags = flags;

		this.basePos = basePos;
		this.shakeOffset = shakeOffset;
		this.sineOffset = sineOffset;
		this.revolveOffset = revolveOffset;

		this.shakeElapsed = shakeElapsed;
		this.sineElapsed = sineElapsed;
		this.revolveElapsed = revolveElapsed;
		this.growElapsed = growElapsed;
	}
}
