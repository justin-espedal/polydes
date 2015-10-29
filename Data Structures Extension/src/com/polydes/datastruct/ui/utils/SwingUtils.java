package com.polydes.datastruct.ui.utils;

import java.awt.Component;

import javax.swing.JPopupMenu;

public class SwingUtils
{
	//http://stackoverflow.com/a/12109282
	/**
	 * Returns whether the component is part of the parent's
	 * container hierarchy. If a parent in the chain is of type 
	 * JPopupMenu, the parent chain of its invoker is walked.
	 * 
	 * @param focusOwner
	 * @param parent
	 * @return true if the component is contained under the parent's 
	 *    hierarchy, coping with JPopupMenus.
	 */
	public static boolean isDescendingFrom(Component focusOwner, Component parent)
	{
		while (focusOwner != null)
		{
			if (focusOwner == parent)
				return true;
			
			if (focusOwner instanceof JPopupMenu)
				focusOwner = ((JPopupMenu) focusOwner).getInvoker();
			else
				focusOwner = focusOwner.getParent();
		}
		return false;
	}
}
