package dialog.core;

import dialog.ds.Typedefs;
import dialog.ext.*;

#if unity

import hugs.HUGSWrapper.NativeArrayIterator;

#end

class DialogStyle
{
	public var style:Style;

	#if stencyl
	public var extensionClasses:Array<Class<Dynamic>>;
	#end
	public var extensions:Array<DialogExtension>;
	public var extensionMap:Map<String, DialogExtension>;

	public var callbacks:Map<Int, Array<Void->Void>>; //callback const, index -> function():Void
	public var graphicsCallbacks:Map<String, Void->Void>; //layer name -> function():Void
	public var cmds:Map<String, Dynamic>; //cmdName -> function(Dynamic):Dynamic

	public static function fromStyle(style:Style):DialogStyle
	{
		var toReturn:DialogStyle = new DialogStyle();
		toReturn.style = style;

		#if stencyl

		toReturn.extensionClasses =
			[DialogBase, CharacterScripts, FlowScripts, MessagingScripts, SkipScripts,
			SoundScripts, TypingScripts, ExtraGlyphs, TextEffects, DialogOptions, Logic];

		#end

		return toReturn;
	}

	public function new()
	{

	}

	public function tieExtensionsToDialogBox(dg:DialogBox):Void
	{
		extensions = new Array<DialogExtension>();
		extensionMap = new Map<String, DialogExtension>();

		var curExt:DialogExtension;

		#if stencyl

		for(curClass in extensionClasses)
		{
			curExt = Type.createInstance(curClass, []);
			extensions.push(curExt);
		}

		#elseif unity

		extensions = style.extensions;

		#end
		
		for(ext in extensions)
		{
			ext.setup(dg);
			extensionMap.set(ext.name, ext);
		}

		cmds = new Map<String, Dynamic>();
		for(curExtension in this.extensions)
		{
			for(curCmdName in curExtension.cmds.keys())
			{
				cmds.set(curCmdName, curExtension.cmds.get(curCmdName));
			}
		}

		callbacks = new Map<Int, Array<Void->Void>>();
		for(curConst in Dialog.callbackConstants)
		{
			inheritCallbacks(curConst);
		}
		inheritGraphicsCallbacks();
	}

	private function inheritCallbacks(constID:Int):Void
	{
		if(constID == Dialog.WHEN_DRAWING)
			return;

		if(!callbacks.exists(constID))
			callbacks.set(constID, new Array<Void->Void>());

		for(curExtension in extensions)
		{
			if(curExtension.callbacks.exists(constID))
			{
				for(curFunction in curExtension.callbacks.get(constID))
				{
					callbacks.get(constID).push(curFunction);
				}
			}
		}
	}

	private function inheritGraphicsCallbacks():Void
	{
		graphicsCallbacks = new Map<String, Void->Void>();

		for(curExtension in extensions)
		{
			if(curExtension.graphicsCallbacks != null)
			{
				for(key in curExtension.graphicsCallbacks.keys())
				{
					graphicsCallbacks.set(key, curExtension.graphicsCallbacks.get(key));
				}
			}
		}
	}
}
