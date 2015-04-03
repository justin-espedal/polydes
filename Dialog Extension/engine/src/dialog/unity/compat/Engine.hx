package dialog.unity.compat;

import dialog.core.*;
import unityengine.*;

class Engine
{
  public static inline var SCALE:Float = 1.0;

  public static var STEP_SIZE(get, never):Int;

  public static var screenWidth(get, never):Int;
  public static var screenHeight(get, never):Int;

  static inline function get_STEP_SIZE():Int
  {
    return cast (1000 * Time.deltaTime);
  }

  static inline function get_screenWidth()
  {
    return Screen.width;
  }

  static inline function get_screenHeight()
  {
    return Screen.height;
  }
}
