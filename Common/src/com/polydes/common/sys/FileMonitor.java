package com.polydes.common.sys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import com.polydes.common.nodes.HierarchyModel;

import stencyl.core.lib.Game;
import stencyl.sw.SW;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class FileMonitor
{
	private static final Logger log = Logger.getLogger(FileMonitor.class);
	
	public static final int POLLING_INTERVAL = 5 * 1000;
	
	private static FileAlterationObserver observer;
	private static FileAlterationMonitor monitor;
	private static FileAlterationListener listener;
	
	public static HashMap<String, SysFile> fileCache = new HashMap<String, SysFile>();
	
	private static HierarchyModel<SysFile,SysFolder> model;
	
	public static HierarchyModel<SysFile,SysFolder> getExtrasModel()
	{
		if(model == null)
		{
			File extrasDir = new File(Locations.getGameLocation(Game.getGame()), "extras");
			if(!extrasDir.exists())
				extrasDir.mkdir();
			
			model = new HierarchyModel<SysFile, SysFolder>(registerOnRoot(extrasDir), SysFile.class, SysFolder.class)
			{
				@Override
				public void massMove(List<SysFile> transferItems, SysFolder target, int position)
				{
					List<File> toMove = new ArrayList<File>();
					
					for(SysFile item : transferItems)
						if(item.getParent() != target)
							toMove.add(item.getFile());
					if(toMove.isEmpty())
						return;
					
					SysFileOperations.moveFiles(toMove, target.getFile());
				};
				
				@Override
				public void removeItem(SysFile item, SysFolder target)
				{
					FileHelper.delete(item.getFile());
					FileMonitor.refresh();
				}
			};
		}
		
		return model;
	}
	
	public static SysFolder registerOnRoot(File folder)
	{
		if(!folder.exists())
		{
			log.error("Couldn't begin file watcher, directory does not exist:\n" + folder.getAbsolutePath());
			return null;
		}
		
		if(observer != null)
		{
			unregister();
		}
		
		observer = new FileAlterationObserver(folder);
		monitor = new FileAlterationMonitor(POLLING_INTERVAL);
		listener = new FileAlterationListenerAdaptor()
		{
			@Override
			public void onFileCreate(File file)
			{
				log.info("File created: " + file.getAbsolutePath());
				
				SysFile sysFile = getSysFile(file);
				SysFolder parent = getParentSysFolder(file);
				
				parent.addItem(sysFile, parent.findInsertionIndex(sysFile.getName(), false));
			}

			@Override
			public void onFileDelete(File file)
			{
				log.info("File deleted: " + file.getAbsolutePath());
				
				SysFile toRemove = getSysFile(file);
				if(toRemove != null && toRemove.getParent() != null)
				{
					model.getSelection().remove(toRemove);
					toRemove.getParent().removeItem(toRemove);
				}
				
				dispose(file);
			}
			
			@Override
			public void onFileChange(File file)
			{
				log.info("File changed: " + file.getAbsolutePath());
				
				getSysFile(file).notifyChanged();
			}
			
			@Override
			public void onDirectoryCreate(File directory)
			{
				log.info("Folder created: " + directory.getAbsolutePath());
				
				SysFile sysFile = getSysFile(directory);
				SysFolder parent = getParentSysFolder(directory);
				
				//Ignore owned directories.
				if(parent == model.getRootBranch() && extensionExists(directory.getName()))
					return;
				
				parent.addItem(sysFile, parent.findInsertionIndex(sysFile.getName(), true));
			}
			
			@Override
			public void onDirectoryDelete(File directory)
			{
				log.info("Folder deleted: " + directory.getAbsolutePath());
				
				SysFile toRemove = getSysFile(directory);
				SysFolder parent = getParentSysFolder(directory);
				
				//Ignore owned directories.
				if(parent == model.getRootBranch() && extensionExists(directory.getName()))
					return;
				
				model.getSelection().remove(toRemove);
				toRemove.getParent().removeItem(toRemove);
				dispose(directory);
			}
			
			@Override
			public void onDirectoryChange(File directory)
			{
				log.info("Folder changed: " + directory.getAbsolutePath());
			}
		};

		observer.addListener(listener);
		monitor.addObserver(observer);
		
		try
		{
			monitor.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		SysFolder toReturn = (SysFolder) getSys(folder);
		readFolder(toReturn, true);
		return toReturn;
	}
	
	private static SysFile getSys(File file)
	{
		String key = file.getAbsolutePath();
		if(fileCache.containsKey(key))
			return fileCache.get(key);
		if(!file.exists())
			return null;
		
		SysFile newFile;
		if(file.isDirectory())
			newFile = new SysFolder(file);
		else
			newFile = new SysFile(file);
		
		System.out.println(key + " is " + ((newFile instanceof SysFolder) ? "Folder" : "File"));
		
		fileCache.put(key, newFile);
		return newFile;
	}
	
	private static void dispose(File file)
	{
		fileCache.remove(file.getAbsolutePath());
	}
	
	//TODO: This could run unnecessarily slow for large filesets.
	private static void readFolder(SysFolder folder, boolean isRoot)
	{
		for(File file : folder.getFile().listFiles())
		{
			if(isRoot && extensionExists(file.getName()))
				continue;
			
			SysFile sysFile = getSys(file);
			folder.addItem(sysFile, folder.findInsertionIndex(sysFile.getName(), sysFile instanceof SysFolder));
			if(file.isDirectory())
				readFolder((SysFolder) sysFile, false);
		}
	}
	
	private static boolean extensionExists(String name)
	{
		return SW.get().getExtensionManager().getExtensions().containsKey(name);
	}
	
	private static SysFile getSysFile(File file)
	{
		return getSys(file);
	}
	
	private static SysFolder getParentSysFolder(File file)
	{
		return (SysFolder) getSys(file.getParentFile());
	}
	
	public static void refresh()
	{
		observer.checkAndNotify();
	}
	
	public static void unregister()
	{
		try
		{
			monitor.stop(1);
			monitor.removeObserver(observer);
			observer.removeListener(listener);
			monitor = null;
			observer = null;
			listener = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		fileCache.clear();
	}
}