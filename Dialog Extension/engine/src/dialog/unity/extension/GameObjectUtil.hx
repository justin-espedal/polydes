package dialog.unity.extension;

import unityengine.*;

using hugs.HUGSWrapper.GameObjectMethods;

class GameObjectUtil
{
  public static function getValue(go:GameObject, componentName:String, varName:String):Dynamic
  {
    return Reflect.field(go.getTypedComponent(Type.resolveClass(componentName)), varName);
  }

  public static function setValue(go:GameObject, componentName:String, varName:String, val:Dynamic):Void
  {
    return Reflect.setField(go.getTypedComponent(Type.resolveClass(componentName)), varName, val);
  }
}
