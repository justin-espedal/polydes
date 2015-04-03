#if unity

package dialog.unity.extension;

import unityengine.*;

import dialog.unity.compat.ColorTransform;

class TextureUtil
{
  public static function getSubTextureFromUV(tex:Texture2D, uv0:Vec2, uv1:Vec2, uv2:Vec2, uv3:Vec2):Texture2D
  {
    var size:Vec2 = new Vector2(tex.width, tex.height);

    var xChange = ((uv1 - uv0) * size);
    var yChange = ((uv2 - uv0) * size);

    var colAdvance = xChange.normalized;
    var rowAdvance = yChange.normalized;

    var x:Int = cast (uv0 * size).x;
    var y:Int = cast (uv0 * size).y;

    if(colAdvance.y == -1 || rowAdvance.y == -1)
      --y;
    if(colAdvance.x == -1 || rowAdvance.x == -1)
      --x;

    var w = xChange.larger();
    var h = yChange.larger();

    var newTex = new Texture2D(w, h);

    var col = x;
    var row = y;

    for(i in 0...w)
    {
      for(j in 0...h)
      {
        col = x + i * cast(colAdvance.x, Int) + j * cast(rowAdvance.x, Int);
        row = y + i * cast(colAdvance.y, Int) + j * cast(rowAdvance.y, Int);

        newTex.SetPixel(i, h - (j + 1), tex.GetPixel(col, row));
      }

    }
    newTex.Apply();

    return newTex;
  }

  public static function getSubTexture(tex:Texture2D, rect:Rect):Texture2D
  {
    var newTex = new Texture2D(cast rect.width, cast rect.height);

    var col:Int = cast rect.x;
    var row:Int = cast rect.y;
    var w:Int = cast rect.width;
    var h:Int = cast rect.height;

    for(i in 0...w)
    {
      for(j in 0...h)
      {
        newTex.SetPixel(i, j, tex.GetPixel(col + i, row + j));
      }
    }
    newTex.Apply();

    return newTex;
  }

  public static function getScaled(tex:Texture2D, sx:Float, sy:Float):Texture2D
  {
    var newTex = Object.Instantiate(tex);
	  TextureScale.Point(newTex, Std.int(tex.width * sx), Std.int(tex.height * sy));
    return newTex;
  }

  public static function getTiled(brush:Texture2D, sx:Float, sy:Float):Texture2D
  {
    var tilesX:Int = Math.ceil(sx);
    var tilesY:Int = Math.ceil(sy);

    var newTex = new Texture2D(Std.int(brush.width * sx), Std.int(brush.height * sy));

    var pixels = brush.GetPixels();

    for(y in 0...tilesY)
    {
      for(x in 0...tilesX)
      {
        newTex.SetPixels(x * brush.width, newTex.height - ((y + 1) * brush.height), brush.width, brush.height, pixels);
      }
    }

    newTex.Apply();
    return newTex;
  }

  public static function drawTexture(tex:Texture2D, brush:Texture2D, x:Int, y:Int):Void
  {
    tex.SetPixels(x, tex.height - (brush.height + y), brush.width, brush.height, brush.GetPixels());
    tex.Apply();
  }

  public static function copyPixels(tex:Texture2D, brush:Texture2D, src:Rect, target:Vector2):Void
  {
    var colors = brush.GetPixels(cast src.x, cast brush.height - (src.height + src.y), cast src.width, cast src.height);
    tex.SetPixels(cast target.x, cast tex.height - (src.height + target.y), cast src.width, cast src.height, colors);
    tex.Apply();
  }

  public static function drawChar(tex:Texture2D, c:String, font:Font, x:Int, y:Int):Void
	{
    var src = FontUtil.getCharTexture(font, c);
    drawTexture(tex, src, x, y);
	}

  // Lime code

  public static function colorTransform(tex:Texture2D, rect:Rect, ct:ColorTransform)
  {
    var rowStart:Int = cast rect.y;
		var rowEnd:Int = cast (rect.y + rect.height);
		var columnStart:Int = cast rect.x;
		var columnEnd:Int = cast (rect.x + rect.width);

		var r, g, b, a, ex = 0;
    var color:Color = null;

		for (row in rowStart...rowEnd) {

			for (column in columnStart...columnEnd) {

        color = tex.GetPixel(column, row);

				a = Std.int ((color.a * 0xFF * ct.alphaMultiplier) + ct.alphaOffset);
				ex = a > 0xFF ? a - 0xFF : 0;
				b = Std.int ((color.b * 0xFF * ct.blueMultiplier) + ct.blueOffset + ex);
				ex = b > 0xFF ? b - 0xFF : 0;
				g = Std.int ((color.g * 0xFF * ct.greenMultiplier) + ct.greenOffset + ex);
				ex = g > 0xFF ? g - 0xFF : 0;
				r = Std.int ((color.r * 0xFF * ct.redMultiplier) + ct.redOffset + ex);

        color.r = r / 0xFF;
        color.g = g / 0xFF;
        color.b = b / 0xFF;
        color.a = a / 0xFF;

        tex.SetPixel(column, row, color);
			}

		}

		tex.Apply();

	}
}

@:forward
abstract Vec2(Vector2) from Vector2 to Vector2
{
  public inline function new(v:Vector2)
  {
    this = v;
  }

  @:op(A + B)
  public function add(rhs:Vector2):Vec2
  {
    return new Vec2(new Vector2(this.x + rhs.x, this.y + rhs.y));
  }

  @:op(A - B)
  public function sub(rhs:Vector2):Vec2
  {
    return new Vec2(new Vector2(this.x - rhs.x, this.y - rhs.y));
  }

  @:op(A * B)
  public function mul(rhs:Vector2):Vec2
  {
    return new Vec2(new Vector2(this.x * rhs.x, this.y * rhs.y));
  }

  @:op(A * B)
  public function muli(rhs:Int):Vec2
  {
    return new Vec2(new Vector2(this.x * rhs, this.y * rhs));
  }

  @:op(A * B)
  public function mulf(rhs:Float):Vec2
  {
    return new Vec2(new Vector2(this.x * rhs, this.y * rhs));
  }

  public function larger():Int
  {
    if(Math.abs(this.x) > Math.abs(this.y))
      return cast Math.abs(this.x);
    return cast Math.abs(this.y);
  }
}

#end
