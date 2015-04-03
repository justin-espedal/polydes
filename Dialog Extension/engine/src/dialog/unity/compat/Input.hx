package dialog.unity.compat;

class Input
{
  public static inline function check(key:String):Bool
  {
    return unityengine.Input.GetKey(key);
  }

  public static inline function pressed(key:String):Bool
  {
    return unityengine.Input.GetKeyDown(key);
  }

  public static inline function released(key:String):Bool
  {
    return unityengine.Input.GetKeyUp(key);
  }
}
