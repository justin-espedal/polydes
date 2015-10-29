package com.polydes.datastruct.ui.utils;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

public class HierarchyLeaveListener extends TemporaryAWTListener implements FocusListener
{
	private boolean inHierarchy; //was the last click in the component's hierarchy
	
	private final Component component;
	private final Runnable callback;
	
	public HierarchyLeaveListener(Component component, final Runnable callback)
	{
		super(AWTEvent.MOUSE_EVENT_MASK);
		
		this.component = component;
		this.callback = callback;
		component.addFocusListener(this);
	}
	
	@Override
	public void eventDispatched(AWTEvent e) 
    {
    	if(e instanceof MouseEvent)
    	{
    		MouseEvent me = (MouseEvent) e;
    		
    		if(me.getClickCount() > 0 && e.toString().contains("PRESSED"))
    		{
    			Component c = (Component) me.getSource();
    			
    			if(inHierarchy != SwingUtils.isDescendingFrom(c, component))
    			{
    				inHierarchy = !inHierarchy;
    				
    				if(!inHierarchy)
    					callback.run();
    			}
    		}
    	}
    }
	
	@Override
	public void dispose()
	{
		super.dispose();
		component.removeFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		inHierarchy = true;
		start();
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		if(!inHierarchy)
			stop();
	}
}
