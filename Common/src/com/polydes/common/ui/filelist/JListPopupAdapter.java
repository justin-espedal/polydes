package com.polydes.common.ui.filelist;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

public abstract class JListPopupAdapter extends MouseAdapter
{
	private final JList<?> list;
	
	public JListPopupAdapter(JList<?> list)
	{
		this.list = list;
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(list.locationToIndex(e.getPoint()) == -1 && !e.isShiftDown() && !isMenuShortcutKeyDown(e))
			list.clearSelection();
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}
	
	private boolean eventIsOverSelection(MouseEvent e)
	{
		return
			list.locationToIndex(e.getPoint()) != -1 &&
			list.isSelectedIndex(list.locationToIndex(e.getPoint()));
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			boolean selectionTargeted = eventIsOverSelection(e);
			
			if(!selectionTargeted)
			{
				int index = list.locationToIndex(e.getPoint());
				if(index != -1)
				{
					list.setSelectedIndex(index);
					selectionTargeted = true;
				}
			}
			
			showPopup(selectionTargeted, e);
		}
	}
	
	private boolean isMenuShortcutKeyDown(InputEvent event)
	{
		return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
	}
	
	public abstract void showPopup(boolean selectionTargeted, MouseEvent e);
}
