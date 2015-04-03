package dialog.core;

class Tag
{
	public var name:String;
	public var argArray:Array<Dynamic>;
	
	public function new(name:String, argArray:Array<Dynamic>)
	{
		this.name = name;
		this.argArray = argArray;
	}
	
	public function toString():String
	{
		return "<" + name + " " + argArray.join(" ") + ">";
	}
}