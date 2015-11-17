package com.polydes.common.util;

import static com.polydes.common.util.Lang.mapCA;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

public class PopupUtil
{
	public static JPopupMenu buildPopup(String[] itemNames, ActionListener listener)
	{
		JPopupMenu menu = new JPopupMenu();
		for(String itemName : itemNames)
		{
			JMenuItem item = new JMenuItem(itemName);
			item.addActionListener(listener);
			menu.add(item);
		}
		
		return menu;
	}
	
	public static JMenu menu(String name, JMenuItem... items)
	{
		JMenu menu = new JMenu(name);
		for(JMenuItem item : items)
			menu.add(item);
		
		return menu;
	}
	
	public static JPopupMenu buildPopup(JMenuItem... items)
	{
		JPopupMenu menu = new JPopupMenu();
		for(JMenuItem item : items)
			menu.add(item);
		
		return menu;
	}
	
	public static <T> void installListener(MenuElement menu, PopupSelectionListener<T> listener)
	{
		for(MenuElement element : menu.getSubElements())
		{
			if(element instanceof JMenuItem)
			{
				final JMenuItem item = (JMenuItem) element;
				
				for(ActionListener l : item.getActionListeners())
					item.removeActionListener(l);
				
				item.addActionListener((event) -> {
					T userData = getUserData(item);
					if(userData != null)
						listener.itemSelected(userData);
				});
			}
			
			installListener(element, listener);
		}
	}
	
	public static final String USER_DATA_PROPERTY = "PopupUtil.userData";
	
	@SuppressWarnings("unchecked")
	public static <T> T getUserData(JMenuItem item)
	{
		return (T) item.getClientProperty(USER_DATA_PROPERTY);
	}
	
	public static JMenuItem menuItem(String name, Object o, ImageIcon icon)
	{
		JMenuItem item = new JMenuItem(name);
		item.setIcon(IconUtil.getIcon(icon, 16));
		item.putClientProperty(USER_DATA_PROPERTY, o);
		return item;
	}
	
	public static JMenuItem menuItem(String name, ImageIcon icon, ActionListener l)
	{
		JMenuItem item = new JMenuItem(name);
		item.setIcon(IconUtil.getIcon(icon, 16));
		item.addActionListener(l);
		return item;
	}
	
	public static JMenuItem menuItem(String name, ActionListener l)
	{
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(l);
		return item;
	}
	
	public static interface PopupSelectionListener<T>
	{
		public void itemSelected(T item);
	}
	
	public static interface MenuItemAccess
	{
		JMenuItem asMenuItem();
	}
	
	public static JMenuItem[] asMenuItems(Collection<? extends MenuItemAccess> c)
	{
		if(c == null)
			return new JMenuItem[0];
		
		return mapCA(c, JMenuItem.class, info -> info.asMenuItem());
	}
	
	public static Point getPositionWithinWindow(Component component, Component parent, Point p)
	{
		Point[] pointCheck = new Point[]{(Point)p.clone(), (Point)p.clone(), (Point)p.clone(), (Point)p.clone()};
		int w = component.getWidth();
		int h = component.getHeight();
		pointCheck[0].translate(w, h);
		pointCheck[1].translate(0, h);
		pointCheck[2].translate(w, 0);
		pointCheck[3].translate(0, 0);
		for(Point p2 : pointCheck)
		{
			if(parent.getBounds().contains(p2))
			{
				p2.translate(-w, -h);
				return p2;
			}
		}
		return p;
	}
}