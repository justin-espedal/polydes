package com.polydes.datastruct.ui.utils;

import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

public abstract class TemporaryAWTListener implements AWTEventListener
{
	private boolean running;
	private final long eventMask;
	
	public TemporaryAWTListener(long eventMask)
	{
		this.eventMask = eventMask;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void start()
	{
		if(running)
			return;
		
		running = true;
		Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
	}
	
	public void stop()
	{
		if(!running)
			return;
		
		running = false;
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}
	
	public void dispose()
	{
		stop();
	}
}
