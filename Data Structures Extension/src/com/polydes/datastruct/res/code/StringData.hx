/**
 * @author Justin Espedal
 */
package scripts.ds;

import com.stencyl.Data;

class StringData
{
	public static function read(s:String, type:String):Dynamic
	{
		return Reflect.callMethod(StringData, Reflect.field(StringData, "r" + type), [s]);
	}
	
	public static function getInts(s:String):Array<Int>
	{
		if(s.length == 0)
			return null;
		
		var splitString:Array<String> = s.substring(1,s.length - 1).split(",");
		var toReturn:Array<Int> = [];
		for(sub in splitString)
			toReturn.push(rInt(sub));
		return toReturn;
	}
	
[DATAREADERS]
}