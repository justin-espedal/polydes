package stencyl.ext.polydes.extrasmanager.io;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.extrasmanager.Main;
import stencyl.ext.polydes.extrasmanager.app.list.FileListRenderer;
import stencyl.ext.polydes.extrasmanager.data.FilePreviewer;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFile;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFolder;

public class FileMonitor
{
	public static final int POLLING_INTERVAL = 5 * 1000;
	
	private static FileAlterationObserver observer;
	private static FileAlterationMonitor monitor;
	private static FileAlterationListener listener;
	
	public static HashMap<String, Leaf<SysFile>> fileCache = new HashMap<String, Leaf<SysFile>>();
	
	public static SysFolder registerOnRoot(File folder)
	{
		if(!folder.exists())
		{
			System.out.println("Couldn't begin file watcher, directory does not exist:\n" + folder.getAbsolutePath());
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
				System.out.println("File created: " + file.getAbsolutePath());
				
				SysFile sysFile = getSysFile(file);
				SysFolder parent = getParentSysFolder(file);
				
				parent.addItem(sysFile, parent.findInsertionIndex(sysFile.getName(), false));
			}

			@Override
			public void onFileDelete(File file)
			{
				System.out.println("File deleted: " + file.getAbsolutePath());
				
				SysFile toRemove = getSysFile(file);
				toRemove.getParent().removeItem(toRemove);
				
				FileListRenderer.clearThumbnail(file);
				dispose(file);
			}
			
			@Override
			public void onFileChange(File file)
			{
				System.out.println("File changed: " + file.getAbsolutePath());
				
				if(FilePreviewer.getPreviewFile() == getSysFile(file))
					FilePreviewer.preview(getSysFile(file));
				FileListRenderer.clearThumbnail(file);
			}
			
			@Override
			public void onDirectoryCreate(File directory)
			{
				System.out.println("Folder created: " + directory.getAbsolutePath());
				
				SysFile sysFile = getSysFile(directory);
				SysFolder parent = getParentSysFolder(directory);
				
				//Ignore owned directories.
				if(parent == Main.getModel().getRootBranch() && Main.ownedFolderNames.contains(directory.getName()))
					return;
				
				parent.addItem(sysFile, parent.findInsertionIndex(sysFile.getName(), true));
			}
			
			@Override
			public void onDirectoryDelete(File directory)
			{
				System.out.println("Folder deleted: " + directory.getAbsolutePath());
				
				SysFile toRemove = getSysFile(directory);
				SysFolder parent = getParentSysFolder(directory);
				
				//Ignore owned directories.
				if(parent == Main.getModel().getRootBranch() && Main.ownedFolderNames.contains(directory.getName()))
					return;
				
				toRemove.getParent().removeItem(toRemove);
				dispose(directory);
			}
			
			@Override
			public void onDirectoryChange(File directory)
			{
				System.out.println("Folder changed: " + directory.getAbsolutePath());
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
	
	private static Leaf<SysFile> getSys(File file)
	{
		String key = file.getAbsolutePath();
		if(fileCache.containsKey(key))
			return fileCache.get(key);
		
		Leaf<SysFile> newFile;
		if(file.isDirectory())
			newFile = new SysFolder(file);
		else
			newFile = new SysFile(file);
		
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
			if(isRoot && Main.ownedFolderNames.contains(file.getName()))
				continue;
			
			Leaf<SysFile> sysFile = getSys(file);
			folder.addItem(sysFile, folder.findInsertionIndex(sysFile.getName(), sysFile instanceof SysFolder));
			if(file.isDirectory())
				readFolder((SysFolder) sysFile, false);
		}
	}
	
	private static SysFile getSysFile(File file)
	{
		return (SysFile) getSys(file);
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
		FileListRenderer.clearThumbnails();
	}
}