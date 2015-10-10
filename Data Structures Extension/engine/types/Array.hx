	public static function rArray(s:String):Array<Dynamic>
	{
		var a:Array<Dynamic> = [];
		
		var i:Int = s.lastIndexOf(":");
		var type:String = s.substring(i + 1);
		
		var char:String;
		var k:Int = 0;
		var commas:Array<Int> = [];
		for(j in 1...i)
		{
			char = s.charAt(j);
			if(char == "[")
				++k;
			else if(char == "]")
				--k;
			else if(char == "," && k == 0)
				commas.push(j);
		}
		
		var lastComma:Int = 0;
		for(comma in commas)
		{
			a.push(StringData.read(s.substring(lastComma + 1, comma), type));
			lastComma = comma;
		}
		a.push(StringData.read(s.substring(lastComma + 1, i - 1), type));
		
		return a;
	}