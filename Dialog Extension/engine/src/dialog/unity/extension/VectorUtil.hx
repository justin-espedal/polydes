package dialog.unity.extension;

import unityengine.*;

class VectorUtil
{
  public static function add(a:Vector2, b:Vector2)
  {
    return new Vector2(a.x + b.x, a.y + b.y);
  }

  public static function clone(v:Vector2)
  {
    return new Vector2(v.x, v.y);
  }
}
