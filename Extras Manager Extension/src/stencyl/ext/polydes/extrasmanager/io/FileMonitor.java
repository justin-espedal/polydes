package stencyl.ext.polydes.extrasmanager.io;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import stencyl.ext.polydes.extrasmanager.app.pages.MainPage;
import stencyl.ext.polydes.extrasmanager.data.ExtrasDirectory;

public class FileMonitor
{
	public static final int POLLING_INTERVAL = 5 * 1000;
	
	private static FileAlterationObserver observer;
	private static FileAlterationMonitor monitor;
	private static FileAlterationListener listener;
	
	public static void registerOnRoot(File folder)
	{
		if(!folder.exists())
		{
			System.out.println("Couldn't begin file watcher, directory does not exist:\n" + folder.getAbsolutePath());
			return;
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
				maybeRootChanged(file);
				
				
			}

			@Override
			public void onFileDelete(File file)
			{
				System.out.println("File deleted: " + file.getAbsolutePath());
				maybeRootChanged(file);
				
			}
			
			@Override
			public void onFileChange(File file)
			{
				System.out.println("File changed: " + file.getAbsolutePath());
				maybeRootChanged(file);
				
			}
			
			@Override
			public void onDirectoryCreate(File directory)
			{
				System.out.println("Folder created: " + directory.getAbsolutePath());
				maybeRootChanged(directory);
				
			}
			
			@Override
			public void onDirectoryDelete(File directory)
			{
				System.out.println("Folder deleted: " + directory.getAbsolutePath());
				maybeRootChanged(directory);
				
			}
			
			@Override
			public void onDirectoryChange(File directory)
			{
				System.out.println("Folder changed: " + directory.getAbsolutePath());
				MainPage.get().update(directory);
				addToChangeSet(directory);
			}
			
			private void maybeRootChanged(File file)
			{
				if(ExtrasDirectory.isRoot(file.getParentFile()))
					onDirectoryChange(ExtrasDirectory.extrasFolderF);
			}
			
			private HashSet<File> changedSet = new HashSet<File>();
			private Timer changeSetTimer;
			
			private void addToChangeSet(File file)
			{
				System.out.println("added to changest");
				
				if(changeSetTimer == null)
				{
					System.out.println("started changeset timer");
					
					changeSetTimer = new Timer();
					changeSetTimer.schedule(new TimerTask()
					{
						@Override
						public void run()
						{
							System.out.println("running changeset timer");
							
							HashMap<String, File> changeMap = new HashMap<String, File>();
							for(File file : changedSet)
								changeMap.put(file.getAbsolutePath(), file);
							for(File file : changeMap.values())
								if(!changeMap.containsKey(file.getParentFile().getAbsolutePath()))
									MainPage.get().updateTree(file);
							changedSet.clear();
							changeSetTimer = null;
						}
					}, 100);
				}
				changedSet.add(file);
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
	}
	
	public static void refresh()
	{
		observer.checkAndNotify();
	}
	
	public static void unregister()
	{
		try
		{
			monitor.stop();
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
	}
}