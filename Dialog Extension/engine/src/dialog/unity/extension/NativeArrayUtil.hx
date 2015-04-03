package dialog.unity.extension;

import cs.NativeArray;

class NativeArrayUtil
{
  public static function iterator(na:NativeArray<Dynamic>):NativeArrayIterator<Dynamic>
  {
    return new NativeArrayIterator<Dynamic>(na);
  }
}

class NativeArrayIterator<T>
{
  var na:NativeArray<T>;
  var i:Int;

  public function new(na:NativeArray<T>)
  {
    this.na = na;
    i = 0;
  }

  public function hasNext():Bool
  {
    return i < na.Length;
  }

  public function next():T
  {
    return na[i++];
  }
}
