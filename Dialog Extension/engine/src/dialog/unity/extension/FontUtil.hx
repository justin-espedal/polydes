#if unity

package dialog.unity.extension;

import unityengine.*;
import cs.NativeArray;

using dialog.unity.extension.TextureUtil;
using dialog.unity.extension.StringUtil;

class FontUtil
{
  public static var info:CharacterInfo;

  public static function getCharInfo(font:Font, ch:String):CharacterInfo
  {
    var hasInfo = untyped __cs__("font.GetCharacterInfo((char) ch[0], out info)");

    if(hasInfo)
    {
      return info;
    }
    else
    {
      return null;
    }
  }

  public static function getTexture(font:Font):Texture2D
  {
    return cast font.material.mainTexture;
  }

  public static function getCharTexture(font:Font, ch:String):Texture2D
  {
    var tex:Texture2D = cast font.material.mainTexture;

    var hasInfo = untyped __cs__("font.GetCharacterInfo((char) ch[0], out info)");

    if(hasInfo)
    {
      return tex.getSubTextureFromUV(info.uvTopLeft, info.uvTopRight, info.uvBottomLeft, info.uvBottomRight);
    }
    else
    {
      return Texture2D.whiteTexture;
    }
  }

  public static function getStringWidth(font:Font, msg:String, charSpacing:Int):Int
  {
    var w:Int = 0;

    for(ch in msg.iterator())
		{
			var info = getCharInfo(font, ch);

      w += info.advance + charSpacing;
		}

    w -= charSpacing;

    return w;
  }
}

#end
