package dialog.unity;

#if macro

import haxe.macro.Context;
import haxe.macro.Expr;
import haxe.macro.Type;
import haxe.macro.Expr.FieldType;

using haxe.macro.MacroStringTools;
using haxe.macro.TypeTools;
using Type;
using Lambda;

typedef MenuItem =
{
  className:String,
  menuPath:String,
  category:String,
  scriptableObject:String,
  priority:Int
};

class EditorBuilder
{
  public static var menuItems:Array<MenuItem> = [];

  macro public static function prepareForUnityInspector():Array<Field>
  {
    var fields = Context.getBuildFields();
    var modified = false;

    modified = hidePrivateFields(fields) || modified;
    modified = createArrayProxies(fields) || modified;

    if(modified)
      return fields;
    else
      return null;
  }

  public static function hidePrivateFields(fields:Array<Field>):Bool
  {
    var modified = false;
    var pos = Context.currentPos();

    for(field in fields)
    {
      switch(field.kind)
      {
        case FVar(_):
          if(field.access.exists(function(acc) return acc == APrivate))
            field.meta.push({
              name: ":meta",
              params: [macro UnityEngine.HideInInspector],
              pos: pos});
            modified = true;
        case _:
          continue;
      }
    }

    return modified;
  }

  public static function createArrayProxies(fields:Array<Field>):Bool
  {
    var modified = false;
    var pos = Context.currentPos();

    var newFields = new Array<Field>();
    var oldFields = new Array<Field>();

    for(field in fields)
    {
      var thisModified = false;

      if(field.access.exists(function(acc) return acc == APublic))
      {
        switch(field.kind)
        {
          case FVar(t, exp):
            switch(t)
            {
              case TPath(p):
                if(p.name == "Array")
                {
                  switch(p.params[0])
                  {
                    case TPType(TPath(p2)):
                      oldFields.push(field);
                      newFields = newFields.concat(createProxyFields(field.name, p2.name));
                      modified = true;
                    default:
                  }
                }
              default:
            }
          default:
        }
      }
    }

    for(oldField in oldFields)
      fields.remove(oldField);
    for(newField in newFields)
      fields.push(newField);

    return modified;
  }

  public static function createProxyFields(name:String, pTypeName:String):Array<Field>
  {
    var pName = '_$name';

    var pGetter = 'get_$pName';
    var pSetter = 'set_$pName';

    var rGetter = 'get_$name';

    var genType = pTypeName.toComplex();

    var rType = macro : Array<$genType>;
    var pType = macro : cs.NativeArray<$genType>;

    var fieldStore = macro class NewFields
    {
      public var $name(get, never):$rType;
      public var $pName:$pType;

      public function $rGetter():$rType
      {
        return new Array($i{pName});
      }
    }

    return fieldStore.fields;
  }

  macro public static function markForMenuGeneration():Array<Field>
  {
    var ct = Context.getLocalClass().get();

    var menuCategory = "";

    if(ct.superClass.t != null)
    {
      switch(ct.superClass.t.get().name)
      {
        case "DialogExtension":
          menuCategory = "Extension";
        case "ScriptableObject":
          menuCategory = "Settings";
      }
    }

    if(menuCategory != "")
    {
      menuItems.push(
        {
          className: ct.name,
          menuPath: '"Assets/Create/Dialog/$menuCategory/${ct.name}"',
          category: menuCategory,
          scriptableObject: '${ct.pack.join(".")}.${ct.name}',
          priority: 0
        });
    }

    return null;
  }

  macro public static function generateMenus():Array<Field>
  {
    var fields = Context.getBuildFields();

    var pos = Context.currentPos();

    var catIndices = new Map<String, Int>();
    catIndices["Extension"] = 10;
    catIndices["Settings"] = 10;

    for(menuItem in menuItems)
    {
      if(menuItem.className != "DialogBase" && menuItem.className != "Style")
      {
        catIndices[menuItem.category] = catIndices[menuItem.category] + 1;
        menuItem.priority = catIndices[menuItem.category];
      }
      var csCode = 'ScriptableObjectUtility.CreateAsset<${menuItem.scriptableObject}>()';

      var fun =
      {
        kind: FFun
        ({
          args: [],
          expr: macro $b{[macro untyped __cs__( $v{csCode} )]},
          params: [],
          ret: macro : Void
        }),
        meta: [{
          name: ":meta",
          params: [macro UnityEditor.MenuItem( $i{menuItem.menuPath}, false, $v{menuItem.priority} )],
          pos: pos
        }],
        name: "create" + menuItem.className,
        doc: null,
        pos: pos,
        access: [AStatic, APublic]
      };

      fields.push(fun);
    }

    return fields;
  }
}

#end

/*

C# template for editor class

using UnityEngine;
using UnityEditor;

public class YourClassAsset
{
	[MenuItem("Assets/Create/Dialog/Extension/DialogBase")]
	public static void CreateDialogBase()
	{
		ScriptableObjectUtility.CreateAsset<dialog.ext.DialogBase>();
	}
}

*/
