package com.polydes.common.sw;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.snippet.ISnippet.SnippetType;
import stencyl.core.lib.Game;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Loader;
import stencyl.sw.util.Locations;

public class Snippets
{
	public static ISnippet createNew(String name, String packageName, String className, String description, String sourceCode)
	{
		try
		{
			Game game = Game.getGame();
			int ID = game.getNextSnippetID();
			
			ISnippet s = new ISnippet
			(
				ID, 
				name, 
				className, 
				-1, 
				description, 
				false,
				-1,
				0,
				SnippetType.ARBITRARY,
				-1,
				-1,
				false,
				true,
				packageName
			);
			
			game.getSnippetList().put(ID, s);
			
			String url = Locations.getPath("snippets") + ID + ".png";
			ImageIcon icon = Loader.loadIcon("res/snippet/icon.png");
			s.setIcon(icon.getImage());
			byte[] bytes = FileHelper.readImageToByteArray(icon.getImage(), "png");
			FileHelper.writeToGameFile(game, url, bytes);
			
			new File(Locations.getPath(Locations.getGameLocation(game), "code")).mkdirs();
			FileHelper.writeToGameFile(game, Locations.getPath("code") + className + ".hx", sourceCode.getBytes());
			
			return s;
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
