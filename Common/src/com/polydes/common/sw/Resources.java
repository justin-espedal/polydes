package com.polydes.common.sw;

import java.awt.Image;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.sound.ISoundClip;
import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Folder;
import stencyl.core.lib.Game;
import stencyl.core.lib.Game.Scale;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.data.EditableAnimation;
import stencyl.sw.data.EditableBackground;
import stencyl.sw.data.EditableFont;
import stencyl.sw.data.EditableSprite;
import stencyl.sw.data.EditableTileset;
import stencyl.sw.editors.font.BitmapFontPage;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;
import stencyl.thirdparty.pulpcore.assettools.ConvertFontTask;

public class Resources
{
	public static boolean isUnderFolder(AbstractResource r, Folder f)
	{
		//since Stencyl doesn't have nested folders, null folder represents the root folder.
		return f == null || r.getParentFolder() == f;
	}
	
	public static Image getImage(AbstractResource r)
	{
		Image img = null;
		
		if(r instanceof IActorType)
		{
			IActorType actor = (IActorType) r;
			EditableSprite sprite = actor.getSprite();
			img = getImage(sprite.getAnimation(sprite.getDefaultAnimation()));
		}
		else if(r instanceof EditableBackground)
		{
			EditableBackground background = (EditableBackground) r;
			img = getImage(background.getAnimation());
		}
		else if(r instanceof EditableFont)
		{
			EditableFont font = (EditableFont) r;
			String src = Locations.getResourceLocation(Game.getGame(), font, ".fnt", false, false);
			if(font.isPrerendered())
			{
				Locations.getResourceLocation(Game.getGame(), font, ".png", false, false);
				img = BitmapFontPage.generateThumbnail(font);
			}
			else
				img = new ConvertFontTask(src).getFontPreviewImage();
		}
		else if(r instanceof SceneModel)
		{
			SceneModel scene = (SceneModel) r;
			img = scene.getThumbnail();
		}
		else if(r instanceof ISnippet)
		{
			ISnippet snippet = (ISnippet) r;
			img = snippet.getIcon();
		}
		else if(r instanceof ISoundClip)
		{
			img = Loader.loadIcon("res/libraries/sound.png").getImage();
		}
		else if(r instanceof EditableTileset)
		{
			img = Loader.loadIcon("res/libraries/tileset.png").getImage();	
		}
		
		if(r != null && img == null)
			img = Loader.loadIcon("res/global/warning.png").getImage();
		
		return img;
	}
	
	public static Image getImage(EditableAnimation anim)
	{
		if(anim == null || anim.getNumFrames() == 0)
			return null;
		return anim.getFrame(0, Scale.X1).getImage();
	}
}
