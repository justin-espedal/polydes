package com.polydes.datastruct.updates;

import java.io.File;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.lib.Game;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class V3_GameExtensionUpdate implements Runnable
{
	@Override
	public void run()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		
		File oldExtrasFolder = new File(Locations.getGameLocation(Game.getGame()) + "extras/[ext] data structures");
		File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
		File oldExtrasDataFolder = new File(oldExtrasFolder, "data");
		
		File newExtrasDataFolder = new File(Locations.getExtensionExtrasDataLocation(Game.getGame(), dse.getManifest().id), "data");
		File newExtrasDefsFolder = new File(Locations.getExtensionGameDataLocation(Game.getGame(), dse.getManifest().id), "defs");
		
		FileHelper.copyDirectory(oldExtrasDataFolder, newExtrasDataFolder);
		FileHelper.copyDirectory(oldExtrasDefsFolder, newExtrasDefsFolder);
		FileHelper.delete(oldExtrasFolder);
	}
}