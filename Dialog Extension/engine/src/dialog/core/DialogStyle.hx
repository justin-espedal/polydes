package dialog.core;

import dialog.ds.*;
import dialog.ext.*;

#if unity

import hugs.HUGSWrapper.NativeArrayIterator;

#end

class DialogStyle
{
	public var style:Style;

	public var extensions:Array<dialog.core.DialogExtension>;
	public var extensionMap:Map<String, dialog.core.DialogExtension>;

	public var callbacks:Map<Int, Array<Void->Void>>; //callback const, index -> function():Void
	public var graphicsCallbacks:Map<String, Void->Void>; //layer name -> function():Void
	public var cmds:Map<String, Dynamic>; //cmdName -> function(Dynamic):Dynamic

	public static function fromStyle(style:Style):DialogStyle
	{
		var toReturn:DialogStyle = new DialogStyle();
		toReturn.style = style;

		return toReturn;
	}

	public function new()
	{

	}

	public function tieExtensionsToDialogBox(dg:DialogBox):Void
	{
		extensions = new Array<dialog.core.DialogExtension>();
		extensionMap = new Map<String, dialog.core.DialogExtension>();

		var curExt:dialog.core.DialogExtension;

		#if stencyl

		for(extTemplate in style.extensions)
		{
			curExt = Type.createInstance(extTemplate.implementation, []);
			curExt.setup(dg, extTemplate);
			extensions.push(curExt);
			extensionMap.set(curExt.name, curExt);
		}

		#elseif unity

		extensions = style.extensions;

		for(ext in extensions)
		{
			ext.setup(dg, null);
			extensionMap.set(ext.name, ext);
		}

		#end
		
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
