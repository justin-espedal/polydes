/**
 * @author Justin Espedal
 */
class Logic extends DialogExtension
{
	public var conditionOpen:Bool;
	public var lastIfWasTrue:Bool;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Logic";
		
		conditionOpen = false;
		lastIfWasTrue = false;
		
		cmds = 
		[
			"if"=>ifCheck,
			"elseif"=>elseifCheck,
			"else"=>elseCheck,
			"endif"=>endif
		];
	}
	
	public function ifCheck(condition:Bool):Void
	{
		conditionOpen = true;
		
		if(condition)
		{
			lastIfWasTrue = true;
		}
		else
		{
			var nextIndex:Int;
			
			nextIndex = findNextLogicTag();
			
			if(nextIndex == -1)
				dg.typeIndex = dg.msg.length;
			else
			{
				dg.typeIndex = nextIndex - 1;
			}
			lastIfWasTrue = false;
		}
	}
	
	public function elseifCheck(condition:Bool):Void
	{
		if(!conditionOpen)
			return;
		
		if(!lastIfWasTrue)
		{
			if(condition)
			{
				lastIfWasTrue = true;
			}
			else
			{
				var nextIndex:Int;
			
				nextIndex = findNextLogicTag();
				
				if(nextIndex == -1)
					dg.typeIndex = dg.msg.length;
				else
				{
					dg.typeIndex = nextIndex - 1;
				}
			}
		}
		else
		{
			var nextIndex:Int;
			
			nextIndex = findNextLogicTag();
			
			if(nextIndex == -1)
				dg.typeIndex = dg.msg.length;
			else
			{
				dg.typeIndex = nextIndex - 1;
			}
		}
	}
	
	public function elseCheck():Void
	{
		if(!conditionOpen)
			return;
		
		if(!lastIfWasTrue)
		{
			lastIfWasTrue = true;
		}
		else
		{
			var nextIndex:Int;
			
			nextIndex = findNextLogicTag();
			
			if(nextIndex == -1)
				dg.typeIndex = dg.msg.length;
			else
			{
				dg.typeIndex = nextIndex - 1;
			}
		}
	}
	
	public function endif():Void
	{
		if(!conditionOpen)
			return;
		
		conditionOpen = false;
		lastIfWasTrue = false;
	}
	
	private function findNextLogicTag():Int
	{
		var dg:DialogBox = dg;
		
		for(i in dg.typeIndex + 1...dg.msg.length - 1)
		{
			if(Std.is(dg.msg[i], Tag))
			{
				var tagName:String = cast(dg.msg[i], Tag).name;
				
				if(tagName == "if" || tagName == "elseif" || tagName == "else" || tagName == "endif")
					return i;
			}
		}
		
		return -1;
	}
}