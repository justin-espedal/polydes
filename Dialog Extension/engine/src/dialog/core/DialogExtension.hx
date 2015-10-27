package dialog.core;

import dialog.ds.*;

#if unity
@:build(dialog.unity.EditorBuilder.prepareForUnityInspector())
@:autoBuild(dialog.unity.EditorBuilder.prepareForUnityInspector())
@:autoBuild(dialog.unity.EditorBuilder.markForMenuGeneration())
#end
@:keepSub
class DialogExtension #if unity extends unityengine.ScriptableObject #end
{
	private var dg:DialogBox;

	#if !unity
	public var name:String;
	#end

	public var cmds:Map<String, Dynamic>; //cmdName, <Function>
	public var callbacks:Map<Int, Array<Void->Void>>; //callbackConstant, <Array> //id, <Function>
	public var graphicsCallbacks:Map<String, Void->Void>;

	public function new()
	{
		#if unity
		super();
		#end
	}

	public function setup(dg:DialogBox, style:Dynamic)
	{
		this.dg = dg;

		cmds = new Map<String, Dynamic>();
		callbacks = new Map<Int, Array<Void->Void>>();
		graphicsCallbacks = new Map<String, Void->Void>();
	}

	private function addCallback(callbackConst:Int, f:Void->Void):Void
	{
		if(!(callbacks.exists(callbackConst)))
		{
			callbacks.set(callbackConst, new Array<Void->Void>());
		}
		callbacks.get(callbackConst).push(f);
	}

	private function addDrawCallback(callbackName:String, f:Void->Void):Void
	{
		graphicsCallbacks.set(callbackName, f);
	}
}
