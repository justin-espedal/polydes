/**
 * @author Justin Espedal
 */
import nme.display.BitmapData;

interface DrawHandler
{
	function addImg(img:BitmapData, x:Int, y:Int, scale:Bool = true):Int;
	function moveImgTo(id:Int, x:Int, y:Int):Void;
	function moveImgBy(id:Int, x:Int, y:Int):Void;
	function removeImg(id:Int):Void;
	function clearImgs():Void;
}