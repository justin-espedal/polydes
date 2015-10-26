package com.polydes.common.ext;

import java.util.HashSet;
import java.util.Iterator;

import stencyl.sw.SW;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.util.NotifierHashMap.Listener;

public abstract class GameExtensionWatcher
{
	private HashSet<BaseExtension> allExtensions = new HashSet<BaseExtension>();
	
	Listener<BaseExtension> extListener = (ext) -> updateExtensions();
	Listener<GameExtension> gameExtListener = (ext) -> updateExtensions();
	
	public final void startWatching()
	{
		SW.get().getExtensionManager().getActiveExtensions().addListener(extListener);
		SW.get().getExtensionManager().getActiveGameExtensions().addListener(gameExtListener);
		updateExtensions();
	}
	
	public final void stopWatching()
	{
		SW.get().getExtensionManager().getActiveExtensions().removeListener(extListener);
		SW.get().getExtensionManager().getActiveGameExtensions().removeListener(gameExtListener);
	}
	
	private void updateExtensions()
	{
		HashSet<BaseExtension> active = new HashSet<>();
		
		SW.get().getExtensionManager().getActiveExtensions().values().forEach(
			(ext) -> {if (!(ext instanceof GameExtension)) active.add(ext);}
		);
		SW.get().getExtensionManager().getActiveGameExtensions().values().forEach(
			(ext) -> active.add(ext)
		);
		
		for (Iterator<BaseExtension> i = allExtensions.iterator(); i.hasNext();) {
			BaseExtension ext = i.next();
			if(!active.contains(ext))
			{
				i.remove();
				extensionRemoved(ext);
			}
		}			
		for(BaseExtension ext : active)
			if(!allExtensions.contains(ext))
			{
				allExtensions.add(ext);
				extensionAdded(ext);
			}
	}
	
	public abstract void extensionAdded(BaseExtension e);
	public abstract void extensionRemoved(BaseExtension e);
}
