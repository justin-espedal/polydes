package dialog.unity.extension;

import unityengine.*;

class RectUtil
{
  public static function clone(r:Rect)
  {
    return new Rect(r.x, r.y, r.width, r.height);
  }
}
