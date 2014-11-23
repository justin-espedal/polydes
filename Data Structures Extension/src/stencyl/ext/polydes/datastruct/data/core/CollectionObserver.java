package stencyl.ext.polydes.datastruct.data.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class CollectionObserver
{
	private static ArrayList<CollectionObserver> observers = new ArrayList<CollectionObserver>();
	private static Timer observerPollTimer;
	private static TimerTask observerPollTask;
	
	private int cachedSize;
	private Collection<?> observed;
	private ArrayList<CollectionUpdateListener> listeners;
	
	public CollectionObserver(Collection<?> observed)
	{
		this.observed = observed;
		cachedSize = observed.size();
		if(observers.size() == 0)
			initTimer();
		observers.add(this);
		listeners = new ArrayList<CollectionUpdateListener>();
	}
	
	//TODO: This could fail if an equals number of additions and removals happened within one second.
	public void checkList()
	{
		if(observed.size() != cachedSize)
			listUpdated();
	}
	
	public void addListener(CollectionUpdateListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(CollectionUpdateListener l)
	{
		listeners.remove(l);
	}
	
	public void listUpdated()
	{
		for(CollectionUpdateListener listener : listeners)
			listener.listUpdated();
	}
	
	private static void initTimer()
	{
		observerPollTask = new TimerTask()
		{
			@Override
			public void run()
			{
				for(CollectionObserver observer : observers)
					observer.checkList();
			}
		};
		observerPollTimer = new Timer();
		observerPollTimer.schedule(observerPollTask, 1000, 1000);
	}
}
