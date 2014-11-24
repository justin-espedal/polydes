/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;
import com.stencyl.graphics.fonts.BitmapFont;
import com.stencyl.graphics.G;
import com.stencyl.models.Font;
import com.stencyl.models.Resource;
import com.stencyl.Data;
import com.stencyl.Engine;

import scripts.DataStructures;
import scripts.ds.dialog.Style;

class Dialog
{
	private static var _instance:Dialog = null;
	
	public static inline var ALWAYS:Int = 0;
	public static inline var WHEN_CREATED:Int = 1;
	public static inline var WHEN_DRAWING:Int = 2;
	public static inline var WHEN_MESSAGE_BEGINS:Int = 3;
	public static inline var WHEN_MESSAGE_ENDS:Int = 4;
	public static inline var WHEN_TYPING_BEGINS:Int = 5;
	public static inline var WHEN_TYPING_ENDS:Int = 6;
	public static inline var WHEN_CHAR_TYPED:Int = 7;
	public static inline var WHEN_MESSAGE_SHOWN:Int = 8;
	public static inline var WHEN_MESSAGE_HIDDEN:Int = 9;
	public static inline var WHEN_TEXT_OVERFLOWS:Int = 10;
	public static inline var WHEN_MESSAGE_CLEARED:Int = 11;
	public static inline var RESTORE_DEFAULTS:Int = 12;
	
	//used to loop through all the constants
	public static var callbackConstants:Array<Int> =
	[
		ALWAYS,
		WHEN_CREATED,
		WHEN_DRAWING,
		WHEN_MESSAGE_BEGINS,
		WHEN_MESSAGE_ENDS,
		WHEN_TYPING_BEGINS,
		WHEN_TYPING_ENDS,
		WHEN_CHAR_TYPED,
		WHEN_MESSAGE_SHOWN,
		WHEN_MESSAGE_HIDDEN,
		WHEN_TEXT_OVERFLOWS,
		WHEN_MESSAGE_CLEARED,
		RESTORE_DEFAULTS
	];
	
	public static var activeDialogBox:DialogBox = null;
	public static var scriptReference:Script;
	public static var graphicsReference:G;
	
	public static var dialogBoxes:Array<DialogBox>;
	public static var animations:Array<AnimatedImage>;

	public static var defaultStyle:Style;
	
	public static var dialogCache:Map<String, String> = null;

	public static var macros:Map<String, String> = null;
	public static var specialMacros:Map<String, Array<String>> = null;
	//ID1 -> Text1, ID2, Text2, ID3, Text3...
	
	private static var continuousStarted:Bool = false;
	
	private function new()
	{
		defaultStyle = cast DataStructures.get("Default Style");
		dialogBoxes = new Array<DialogBox>();
		animations = new Array<AnimatedImage>();
		loadDialogCache();
		loadScene(Engine.engine);
	}
	
	public static function get():Dialog
	{
		if(_instance == null)
			_instance = new Dialog();
		return _instance;
	}
	
	//==================================================================
	//==================================================================
	//==================================================================
	
	private var o:Dynamic;
	private var call:String;
	
	public static function cbCall(dgAddress:String, style:String, o:Dynamic, call:String):Void
	{
		if(_instance == null)
			_instance = new Dialog();
		
		var style:Dynamic = DataStructures.get(style);
		if(style == null || !Std.is(style, Style))
			style = defaultStyle;
		
		var dg:DialogBox = new DialogBox(getDg(dgAddress), cast(style, Style));
		_instance.addDialogBox(dg);
		
		Engine.engine.whenDrawingListeners.push(_instance.dialogDrawer);
		Engine.engine.whenUpdatedListeners.push(_instance.dialogUpdater);
		
		_instance.o = o;
		_instance.call = call;
		
		dg.beginDialog();
	}
	
	public static function globalCall(dgText:String, style:String, o:Dynamic, call:String):Void
	{
		if(_instance == null)
			_instance = new Dialog();
		
		var style:Dynamic = DataStructures.get(style);
		if(style == null || !Std.is(style, Style))
			style = defaultStyle;
		
		var dg:DialogBox = new DialogBox(dgText, cast(style, Style));
		_instance.addDialogBox(dg);
		
		Engine.engine.whenDrawingListeners.push(_instance.dialogDrawer);
		Engine.engine.whenUpdatedListeners.push(_instance.dialogUpdater);
		
		_instance.o = o;
		_instance.call = call;
		
		dg.beginDialog();
	}

	public static function dgEnded():Void
	{
		Engine.engine.whenDrawingListeners.remove(_instance.dialogDrawer);
		Engine.engine.whenUpdatedListeners.remove(_instance.dialogUpdater);
		
		if(_instance.o != null && _instance.call != "")
			Reflect.callMethod(_instance.o, Reflect.field(_instance.o, "_customEvent_"+_instance.call), []);
	}
	
	public function dialogDrawer(g:G, x:Float, y:Float, list:Array<Dynamic>):Void
	{
		_instance.drawDialogBoxes();
	}
	
	public function dialogUpdater(elapsedTime:Float, list:Array<Dynamic>):Void
	{
		_instance.updateDialogBoxes();
	}
	
	//==================================================================
	//==================================================================
	//==================================================================
	
	public function loadDialogCache():Void
	{
		if(dialogCache != null) return;
		
		dialogCache = new Map<String, String>();

		var dgLines:Array<String> = Util.getFileLines("dialog.txt");
		
		var curAddress:String = null;
		var curDgString:String = "";

		//==MACROS
		macros = new Map<String, String>();
		specialMacros = new Map<String, Array<String>>();
		
		var m_data:Array<String> = Util.getFileLines("macros.txt");
		var cur_m_data:Array<String> = null;
		var left:String = "";
		var right:String = "";
		for(curLine in m_data)
		{
			if(curLine.length == 0) continue;
			if(curLine.charAt(0) == "#") continue;
			
			cur_m_data = curLine.split("->");
			left = cur_m_data[0];
			right = cur_m_data[1];
			
			if(curLine.charAt(0) == "!")
			{
				var keys:Array<String> = left.substring(1, left.length - 1).split("...");
				var values:Array<String> = right.substr(1).split("...");
				var combined:Array<String> = new Array<String>();
				
				for(i in 0...keys.length)
				{
					if(i > 0)
						combined.push(keys[i]);
					combined.push(values[i]);
				}
				
				specialMacros.set(keys[0], combined);
			}
			else
			{
				macros.set(left.substring(left.indexOf("{") + 1, left.indexOf("}")), right.substr(1));
			}
		}
		
		for(i in 0...dgLines.length)
		{
			dgLines[i] = replaceSpecialMacros(replaceMacros(dgLines[i]));
		}
		
		for(curLine in dgLines)
		{
			if(curLine.length == 0) continue;
			if(curLine.substr(0, 2) == ">>" || curLine.substr(0, 2) == "<<") continue;
			if(curLine.charAt(0) == "#")
			{
				if(curAddress != null)
					dialogCache.set(curAddress, curDgString);
				
				curAddress = curLine.substr(1);
				curDgString = "";
				
				continue;
			}
			
			curDgString += curLine + "\n";
		}
		
		//one final time, to get the currently stored dialog.
		if(curAddress != null && curDgString.length > 0)
			dialogCache.set(curAddress, curDgString);
	}

	private static inline var leftDelimiter:String = "{";
	private static inline var rightDelimiter:String = "}";
	private var delimiterFound:Bool = false;
	
	public function replaceMacros(s:String):String
	{
		if(s.indexOf(leftDelimiter) == -1)
			return s;

		var ns:String = "";
		
		var i:Int = 0;
		while(i < s.length)
		{
			if(s.charAt(i) == "\\")
			{
				i += 2;
				continue;
			}
			
			if(s.charAt(i) == leftDelimiter)
			{
				ns += s.substr(0, i);
				s = s.substr(i + 1);
				i = -1;

				var j:Int = s.indexOf(rightDelimiter);
				if(j == -1)
				{
					break;
				}
				
				if(macros.exists(s.substr(0, j)))
				{
					ns += macros.get(s.substr(0, j));
				}
				s = s.substr(j + 1);
			}
			
			++i;
		}

		ns += s;

		return ns;
	}
	
	public function replaceSpecialMacros(s:String):String
	{
		delimiterFound = false;
		
		for(key in specialMacros.keys())
		{
			if(s.indexOf(key) != -1)
				delimiterFound = true;
		}
		
		if(!delimiterFound)
			return s;
		
		var ns:String = "";
		
		var i:Int = 0;
		while(i < s.length)
		{
			if(s.charAt(i) == "\\")
			{
				i += 2;
				continue;
			}
			
			for(key in specialMacros.keys())
			{
				if(s.charAt(i) == key)
				{
					var a:Array<String> = specialMacros.get(key);
					
					ns += s.substr(0, i) + a[0];
					s = s.substr(i + 1);
					i = -1;
					
					var j:Int = 1;
					var k:Int = 0;
					
					while(j < a.length)
					{
						k = s.indexOf(a[j]);
						if(k == -1)
						{
							break;
						}
						
						s = s.substring(0, k) + a[j + 1] + s.substr(k + 1);
						
						j += 2;
					}
				}
			}
			
			++i;
		}
		
		ns += s;
		
		return ns;
	}
	
	public function loadScene(state:Engine):Void
	{
		scriptReference = new Script(state);
		graphicsReference = state.g;
	}
	
	public function addDialogBox(dialogBox:DialogBox):Void
	{
		dialogBoxes.push(dialogBox);
	}
	
	public function removeDialogBox(dialogBox:DialogBox):Void
	{
		for(i in 0...dialogBoxes.length)
		{
			if(dialogBoxes[i] == dialogBox)
			{
				dialogBoxes.splice(i, 1);
				break;
			}
		}
		
		if(dialogBoxes.length == 0)
			dgEnded();
	}

	public function addAnimation(anim:AnimatedImage):Void
	{
		animations.push(anim);
	}
	
	public function removeAnimation(anim:AnimatedImage):Void
	{
		for(i in 0...animations.length)
		{
			if(animations[i] == anim)
			{
				animations.splice(i, 1);
				break;
			}
		}
	}	
	
	public function updateDialogBoxes():Void
	{
		for(curAnimation in animations)
		{
			curAnimation.update();
		}

		for(curDialogBox in dialogBoxes)
		{
			curDialogBox.update();
		}
	}
	
	public function drawDialogBoxes():Void
	{
		for(curDialogBox in dialogBoxes)
		{
			curDialogBox.draw(graphicsReference);
		}
	}
	
	public static function parseMessage(msg:String):Array<Dynamic>
	{
		//convert message from string to array
		var msgArray:Array<Dynamic> = new Array<Dynamic>();
		for(i in 0...msg.length)
		{
			msgArray.push(msg.charAt(i));
		}
		
		//go through message, find all tokens, convert contents to Tag and splice into array.
		msgArray = Dialog.constructTags(msgArray);
		
		//return resulting array
		return msgArray;
	}
	
	private static function constructTags(a:Array<Dynamic>):Array<Dynamic>
	{
		var openTags:Array<Int> = new Array<Int>();

		var i:Int = 0;
		while(i < a.length)
		{
			if(a[i] == "\\" && (a[i + 1] == "<" || a[i + 1] == ">"))
			{
				a.splice(i, 1);
			}
			else if(a[i] == "<")
			{
				openTags.push(i);
			}
			else if(a[i] == ">")
			{
				var j:Int = openTags.pop();
				var newTagData:Array<Dynamic> = a.splice(j, i - j + 1);
				//create a tag with the contents, insert into array, move i
				var newTag:Tag = Dialog.createTag(newTagData);
				i = j;
				a.insert(i, newTag);
			}
			//check for comments. Markup is> //begin comment, end comment//
			else if(i < a.length - 2)
			{
				if(a[i] == "/" && a[i + 1] == "/")
				{
					var j:Int = i + 2;
					while(j < a.length - 2)
					{
						if(a[j] == "/" && a[j + 1] == "/")
						{
							j += 2;
							break;
						}
						++j;
					}

					a.splice(i, j - i);
				}
			}
			
			++i;
		}

		return a;
	}

	private static function createTag(tag:Array<Dynamic>):Tag
	{
		var name:String = "";
		var argArray:Array<Array<Dynamic>> = new Array<Array<Dynamic>>();
		var curArgLevel:Int = 0;
		argArray[0] = new Array<Dynamic>();
		
		var endofarg:String = "";
		
		//remove the first and last elements (< and >) from the tag
		tag.shift();
		tag.pop();

		var i:Int = 0;
		var isString:Bool = true;
		
		while(tag.length > 0)
		{
			while(tag[0] == " " || tag[0] == "\r" || tag[0] == "\n" || tag[0] == "\t")
			{
				tag.shift();
			}

			if(tag[0] == "[")
			{
				++curArgLevel;
				argArray[curArgLevel] = new Array<Dynamic>();
				
				tag.shift();
				continue;
			}

			if(tag[0] == "]")
			{
				argArray[curArgLevel - 1].push(argArray[curArgLevel]);
				--curArgLevel;
				
				tag.shift();
				continue;
			}

			if(tag[0] == "\"")
			{
				endofarg = "\"";
				tag.shift();
			}
			else
			{
				endofarg = " ";
			}
			
			i = 0;
			isString = true;
			while(tag[i] != endofarg)
			{
				if(!(Std.is(tag[i], String))) isString = false;
				++i;
				if(i >= tag.length) break;
				if(endofarg == " " && tag[i] == "]") break;
			}

			if(tag.length > 0)
			{
				var newTag:Dynamic;
				
				if(isString || endofarg == "\"")
				{
					var newTagString:String = tag.splice(0, i).join("");
					
					newTag = Util.valueOfString(newTagString);
				}
				else
				{
					newTag = tag.splice(0, i)[0];
				}

				argArray[curArgLevel].push(newTag);
			}

			if(endofarg == "\"")
			{
				tag.shift();
			}
		}
		
		name = argArray[0].shift();
		return new Tag(name, argArray[0]);
	}

	public static function getDg(address:String):String
	{
		return Dialog.dialogCache.get(address);
	}
}