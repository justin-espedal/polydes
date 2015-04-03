package dialog.unity.extension;

class StringUtil
{
  public static function iterator(s:String):StringIterator
  {
    return new StringIterator(s);
  }
}

class StringIterator
{
  var s:String;
  var i:Int;

  public function new(s:String)
  {
    this.s = s;
    i = 0;
  }

  public function hasNext():Bool
  {
    return i < s.length;
  }

  public function next():String
  {
    return s.charAt(i++);
  }
}
