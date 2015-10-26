package com.polydes.common.ext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.sw.SW;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.ExtensionWrapper;

public class ExtensionInterface
{
	public static Object sendMessage(String extensionID, String message, Object... args)
	{
		ExtensionWrapper ext = SW.get().getExtensionManager().getExtensions().get(extensionID);
		if(!ext.isActivated())
		{
			return null;
		}
		
		try
		{
			return MethodUtils.invokeMethod(ext.getExtension(), message, args);
		}
		catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2)
		{
			e2.printStackTrace();
		}
		
		return null;
	}
	
	public static void doUponLoad(String extensionID, final Runnable task)
	{
		if(SW.get().getExtensionManager().isEnabled(extensionID))
		{
			task.run();
			return;
		}
		
		GameExtensionWatcher monitor = new GameExtensionWatcher()
		{
			@Override
			public void extensionRemoved(BaseExtension e)
			{
				
			}
			
			@Override
			public void extensionAdded(BaseExtension e)
			{
				if(e.getManifest().id.equals(extensionID))
				{
					task.run();
					SwingUtilities.invokeLater(() -> stopWatching());
				}
			}
		};
		monitor.startWatching();
	}
	
	public static void doUponAllLoaded(String[] extensionIDs, final Runnable task)
	{
		HashSet<String> needToLoad = new HashSet<>();
		needToLoad.addAll(Arrays.asList(extensionIDs));
		
		for(String id : extensionIDs)
		{
			if(SW.get().getExtensionManager().isEnabled(id))
			{
				needToLoad.remove(id);
			}
		}
		
		if(needToLoad.isEmpty())
		{
			task.run();
			return;
		}
		
		GameExtensionWatcher monitor = new GameExtensionWatcher()
		{
			@Override
			public void extensionRemoved(BaseExtension e)
			{
				
			}
			
			@Override
			public void extensionAdded(BaseExtension e)
			{
				if(needToLoad.contains(e.getManifest().id))
				{
					needToLoad.remove(e.getManifest().id);
					if(needToLoad.isEmpty())
					{
						task.run();
						SwingUtilities.invokeLater(() -> stopWatching());
					}
				}
			}
		};
		monitor.startWatching();
	}
}
