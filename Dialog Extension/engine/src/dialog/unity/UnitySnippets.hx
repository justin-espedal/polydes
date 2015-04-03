#if unity

package dialog.unity;

import unityengine.*;

import cs.NativeArray;

class UnitySnippets
{
	// https://xinyustudio.wordpress.com/2013/11/05/unity3d-make-3d-objects-over-gui-surface/

	public static function createCamera(name:String):Void
	{
		//var cameraGameObject = new GameObject(name);
		//var cam = cameraGameObject.AddComponent<Camera>();
		//cam.near = Convert.ToSingle(testCamera[i].Near);
		//cam.far = Convert.ToSingle(testCamera[i].Far);
		//cam.aspect = Convert.ToSingle(testCamera[i].AspectRatio);
		//cam.fov = Convert.ToSingle(testCamera[i].FOV);
	}

	public static function prepareTexture(font:Font, characters:String):Void
	{
		if(font.get_dynamic())
		{
			font.RequestCharactersInTexture(characters, 0, FontStyle.Normal);
		}
	}

/*
	public static function textureFromSprite(sprite:Sprite):Texture2D
	{
		if(sprite.rect.width != sprite.texture.width)
		{
			var newTex:Texture2D = new Texture2D(Std.int(sprite.rect.width), Std.int(sprite.rect.height));
			var newColors:NativeArray<Color> =
					sprite.texture.GetPixels
					(
						Std.int(sprite.textureRect.x),
						Std.int(sprite.textureRect.y),
						Std.int(sprite.textureRect.width),
						Std.int(sprite.textureRect.height)
					);
			newTex.SetPixels(newColors);
			newTex.Apply();
			return newTex;
		}
		else
		{
			return sprite.texture;
		}
	}
*/



}

#end
