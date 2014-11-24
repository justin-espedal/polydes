/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;
import com.stencyl.graphics.G;

import scripts.ds.dialog.Style;

class DialogExtension
{
	public var dg:DialogBox;
	public var s:Script;
	public var g:G;
	
	public var name:String;
	
	public var cmds:Map<String, Dynamic>; //cmdName, <Function>
	
	public var callbacks:Map<Int, Array<Void->Void>>; //callbackConstant, <Array> //id, <Function>
	public var graphicsCallbacks:Map<String, Void->Void>;
	
	public var style:Style;
	
	private function new(dg:DialogBox)
	{
		this.dg = dg;
		s = Dialog.scriptReference;
		g = Dialog.graphicsReference;
		style = dg.style;
		
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