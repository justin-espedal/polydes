package dialog.unity.compat;

import dialog.core.*;
import unityengine.*;

class Script
{
  public static inline function playSound(snd:AudioClip):Void
  {
    AudioSource.PlayClipAtPoint(snd, Camera.main.transform.position);
  }

  public static inline function setGameAttribute(name:String, value:Dynamic):Void
  {
    GlobalMap.set(name, value);
  }

  public static inline function getGameAttribute(name:String):Dynamic
  {
    return GlobalMap.get(name);
  }
}
