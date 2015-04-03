package dialog.unity.ds;

import unityengine.*;
import dialog.core.*;

@:build(dialog.unity.EditorBuilder.prepareForUnityInspector())
@:build(dialog.unity.EditorBuilder.markForMenuGeneration())
class Style extends ScriptableObject
{
  public var extensions:Array<DialogExtension>;

  public var drawOrder:Array<String>;
}
