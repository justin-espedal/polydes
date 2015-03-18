/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;

class MessagingScripts extends DialogExtension
{
	public var attributes:Map<String, Dynamic>;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Messaging Scripts";
		
		attributes = new Map<String, Dynamic>();
		
		cmds =
		[
			"setattr"=>setattr,
			"getattr"=>getattr,
			"showattr"=>showattr,
			"messagescene"=>messagescene,
			"messageactor"=>messageactor,
			"addtolist"=>addtolist,
			"removefromlist"=>removefromlist,
			"show"=>show,

			"say"=>say,
			"set"=>set,
			"get"=>get,
			"listset"=>listset,
			"listget"=>listget
		];
	}

	public function analyzeAttr(source:Array<Dynamic>):Dynamic
	{
		switch("" + source[0])
		{
			case "game":
				return Script.getGameAttribute("" + source[1]);
			case "dialog":
				return attributes.get("" + source[1]);
			case "scenebhv":
				return Script.getValueForScene("" + source[1], "" + source[2]);
			case "actorbhv":
				return GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			case "actor":
				return GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
		}
		return null;
	}

	public function setattr(args:Array<Dynamic>):Void
	{
		switch("" + args[0])
		{
			case "game":
				Script.setGameAttribute("" + args[1], args[2]);
			case "dialog":
				attributes.set("" + args[1], args[2]);
			case "scenebhv":
				Script.setValueForScene("" + args[1], "" + args[2], args[3]);
			case "actorbhv":
				GlobalActorID.get("" + args[1]).setValue("" + args[2], "" + args[3], args[4]);
			case "actor":
				GlobalActorID.get("" + args[1]).setActorValue("" + args[2], args[3]);
		}
	}
	
	public function getattr(source:Array<Dynamic>):Dynamic
	{
		return analyzeAttr(source);
	}
	
	public function showattr(source:Array<Dynamic>):Void
	{
		show(analyzeAttr(source));
	}

	public function show(object:Dynamic):Void
	{
		dg.insertMessage("" + object);
	}

	public function addtolist(source:Array<Dynamic>):Void
	{
		analyzeAttr(source).push(source.pop());
	}

	public function removefromlist(source:Array<Dynamic>):Void
	{
		analyzeAttr(source).remove(source.pop());
	}

	public function listset(source:Array<Dynamic>, index:Int, value:Dynamic):Void
	{
		source[index] = value;
	}

	public function listget(source:Array<Dynamic>, index:Int):Dynamic
	{
		return source[index];
	}

	public function code(expr:String):Dynamic
	{
		//TODO
		return null;
	}

	public function say(object:Dynamic, message:String, args:Array<Dynamic>):Dynamic
	{
		return Reflect.callMethod(object, Reflect.field(object, message), args);
	}

	public function set(object:Dynamic, field:String, value:Dynamic):Void
	{
		Reflect.setField(object, field, value);
	}

	public function get(object:Dynamic, field:String):Dynamic
	{
		return Reflect.field(object, field);
	}
	
	public function messagescene(behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		Script.sayToScene(behaviorName, messageName, args);
	}
	
	public function messageactor(actorname:String, behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		GlobalActorID.get(actorname).say(behaviorName, messageName, args);
	}
}