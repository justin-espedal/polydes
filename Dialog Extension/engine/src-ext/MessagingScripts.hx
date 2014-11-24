/**
 * @author Justin Espedal
 */
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
			"removefromlist"=>removefromlist
		];
	}

	public function setattr(args:Array<Dynamic>):Void
	{
		switch("" + args[0])
		{
			case "game":
				s.setGameAttribute("" + args[1], args[2]);
			case "dialog":
				attributes.set("" + args[1], args[2]);
			case "scenebhv":
				s.setValueForScene("" + args[1], "" + args[2], args[3]);
			case "actorbhv":
				GlobalActorID.get("" + args[1]).setValue("" + args[2], "" + args[3], args[4]);
			case "actor":
				GlobalActorID.get("" + args[1]).setActorValue("" + args[2], args[3]);
		}
	}
	
	public function getattr(source:Array<Dynamic>):Dynamic
	{
		switch("" + source[0])
		{
			case "game":
				return s.getGameAttribute("" + source[1]);
			case "dialog":
				return attributes.get("" + source[1]);
			case "scenebhv":
				return s.getValueForScene("" + source[1], "" + source[2]);
			case "actorbhv":
				return GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			case "actor":
				return GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
		}
		return null;
	}
	
	public function showattr(source:Array<Dynamic>):Void
	{
		var toShow:String = "";
		switch("" + source[0])
		{
			case "game":
				toShow = "" + s.getGameAttribute("" + source[1]);
			case "dialog":
				toShow = "" + attributes.get("" + source[1]);
			case "scenebhv":
				toShow = "" + s.getValueForScene("" + source[1], "" + source[2]);
			case "actorbhv":
				toShow = "" + GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			case "actor":
				toShow = "" + GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
		}
		dg.insertMessage(toShow);
	}

	public function addtolist(source:Array<Dynamic>):Void
	{
		switch("" + source[0])
		{
			case "game":
				s.getGameAttribute("" + source[1]).push(source[2]);
			case "dialog":
				attributes.get("" + source[1]).push(source[2]);
			case "scenebhv":
				s.getValueForScene("" + source[1], "" + source[2]).push(source[3]);
			case "actorbhv":
				GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]).push(source[4]);
			case "actor":
				GlobalActorID.get("" + source[1]).getActorValue("" + source[2]).push(source[3]);
		}
	}

	public function removefromlist(source:Array<Dynamic>):Void
	{
		switch("" + source[0])
		{
			case "game":
				s.getGameAttribute("" + source[1]).remove(source[2]);
			case "dialog":
				attributes.get("" + source[1]).remove(source[2]);
			case "scenebhv":
				s.getValueForScene("" + source[1], "" + source[2]).remove(source[3]);
			case "actorbhv":
				GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]).remove(source[4]);
			case "actor":
				GlobalActorID.get("" + source[1]).getActorValue("" + source[2]).remove(source[3]);
		}
	}
	
	public function messagescene(behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		s.sayToScene(behaviorName, messageName, args);
	}
	
	public function messageactor(actorname:String, behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		GlobalActorID.get(actorname).say(behaviorName, messageName, args);
	}
}