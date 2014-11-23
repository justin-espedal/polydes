package stencyl.ext.polydes.datastruct.ui.utils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
	
	public static JMenuItem item(String name, Icon icon)
	{
		JMenuItem item = new JMenuItem(name);
		item.setIcon(icon);
		
		return item;
	}
	
	public static JPopupMenu buildPopup(ArrayList<PopupItem> items, final PopupSelectionListener listener)
	{
		JPopupMenu menu = new JPopupMenu();
		for(final PopupItem item : items)
		{
			for(ActionListener l : item.getActionListeners())
				item.removeActionListener(l);
			item.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					listener.itemSelected(item);
				}
			});
			menu.add(item);
		}
		
		return menu;
	}
	
	public static class PopupItem extends JMenuItem
	{
		public String text;
		public Object data;
		
		public PopupItem(Object o, Icon icon)
		{
			super("" + o);
			text = "" + o;
			setIcon(icon);
			data = o;
		}
		
		public PopupItem(String name, Object o, Icon icon)
		{
			super(name);
			text = name;
			setIcon(icon);
			data = o;
		}
	}
	
	public static interface PopupSelectionListener
	{
		public void itemSelected(PopupItem item);
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