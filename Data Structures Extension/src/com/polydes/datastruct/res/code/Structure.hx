package [PACKAGE];

class [CLASSNAME] extends scripts.ds.DataStructure
{
	public static var fieldMap = {
		var m = new Map<String, String>();
[FIELDMAP]		m;
	}
	
	public static var typeInfo = {
		var m = new Map<String, String>();
[TYPEINFO]		m;
	}
	
	public function getTypeInfo(field:String):String
	{
		return [CLASSNAME].typeInfo.get(field);
	}
	
[VARIABLES]
	public function new()
	{
	}

[CUSTOM]
}