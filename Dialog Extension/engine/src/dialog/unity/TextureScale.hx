/*
 * Author: Eric Haines (Eric5h5)
 *
 * Ported to Haxe by Justin Espedal
*/

package dialog.unity;

import cs.NativeArray;
import cs.system.threading.*;
import unityengine.*;

class ThreadData
{
  public var start:Int;
  public var end:Int;

  public function new(s:Int, e:Int)
  {
    start = s;
    end = e;
  }
}

class TextureScale
{
  private static var texColors:NativeArray<Color>;
  private static var newColors:NativeArray<Color>;
  private static var w:Int;
  private static var ratioX:Float;
  private static var ratioY:Float;
  private static var w2:Int;
  private static var finishCount:Int;
  private static var mutex:Mutex;

	public static function Point(tex:Texture2D, newWidth:Int, newHeight:Int):Void
  {
		ThreadedScale(tex, newWidth, newHeight, false);
	}

	public static function Bilinear(tex:Texture2D, newWidth:Int, newHeight:Int):Void
  {
		ThreadedScale(tex, newWidth, newHeight, true);
	}

	private static function ThreadedScale(tex:Texture2D, newWidth:Int, newHeight:Int, useBilinear:Bool):Void
  {
		texColors = tex.GetPixels();
		newColors = new NativeArray<Color>(newWidth * newHeight);
		if(useBilinear)
    {
			ratioX = 1.0 / (newWidth / (tex.width - 1));
			ratioY = 1.0 / (newHeight / (tex.height - 1));
		}
		else
    {
			ratioX = tex.width / newWidth;
			ratioY = tex.height / newHeight;
		}
		w = tex.width;
		w2 = newWidth;
		var cores = Std.int(Math.min(SystemInfo.processorCount, newHeight));
		var slice = Std.int(newHeight / cores);

		finishCount = 0;
		if (mutex == null)
    {
			mutex = new Mutex(false);
		}
		if (cores > 1)
		{
		    var i:Int = 0;
		    var threadData:ThreadData = null;
			  for (i in 0...(cores - 1))
        {
          threadData = new ThreadData(slice * i, slice * (i + 1));
          var ts:ParameterizedThreadStart = useBilinear ? new ParameterizedThreadStart(BilinearScale) : new ParameterizedThreadStart(PointScale);
          var thread:Thread = new Thread(ts);
          thread.Start(threadData);
        }
        threadData = new ThreadData(slice*i, newHeight);
        if (useBilinear)
        {
          BilinearScale(cast threadData);
        }
        else
        {
          PointScale(cast threadData);
        }
        while (finishCount < cores)
        {
          Thread.Sleep(1);
        }
      }
      else
      {
        var threadData:ThreadData = new ThreadData(0, newHeight);
        if (useBilinear)
        {
          BilinearScale(cast threadData);
        }
        else
        {
          PointScale(cast threadData);
        }
      }

      tex.Resize(newWidth, newHeight);
      tex.SetPixels(newColors);
      tex.Apply();
    }

    public static function BilinearScale(obj:cs.system.Object):Void
    {
      var threadData:ThreadData = cast obj;
      for(y in threadData.start...threadData.end)
      {
        var yFloor:Int = Std.int(Math.floor(y * ratioY));
        var y1 = yFloor * w;
        var y2 = (yFloor + 1) * w;
        var yw = y * w2;

        for(x in 0...w2)
        {
          var xFloor:Int = Std.int(Math.floor(x * ratioX));
          var xLerp = x * ratioX - xFloor;

          newColors[yw + x] = ColorLerpUnclamped(ColorLerpUnclamped(texColors[y1 + xFloor], texColors[y1 + xFloor+1], xLerp),
													   ColorLerpUnclamped(texColors[y2 + xFloor], texColors[y2 + xFloor+1], xLerp),
													   y * ratioY - yFloor);
        }
      }

      mutex.WaitOne();
      finishCount++;
      mutex.ReleaseMutex();
    }

    public static function PointScale(obj:cs.system.Object):Void
    {
      var threadData:ThreadData = cast obj;

      for(y in threadData.start...threadData.end)
      {
        var thisY:Int = Std.int(ratioY * y) * w;
        var yw:Int = y * w2;

        for(x in 0...w2)
        {
          newColors[yw + x] = texColors[Std.int(thisY + ratioX * x)];
        }
      }

      mutex.WaitOne();
      finishCount++;
      mutex.ReleaseMutex();
    }

    private static function ColorLerpUnclamped(c1:Color, c2:Color, value:Float):Color
    {
        return new Color(c1.r + (c2.r - c1.r) * value,
						  c1.g + (c2.g - c1.g) * value,
						  c1.b + (c2.b - c1.b) * value,
						  c1.a + (c2.a - c1.a) * value);
    }
}
