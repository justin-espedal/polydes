package com.polydes.datastruct.ui.utils;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Layout
{
	
	public static JPanel horizontalBox(Dimension constrict, Component... comps)
	{
		JPanel constricted = new JPanel();
		constricted.setLayout(new BoxLayout(constricted, BoxLayout.X_AXIS));
		constricted.setBackground(null);
		if(constrict != null)
		{
			//constricted.setMinimumSize(constrict);
			//constricted.setPreferredSize(constrict);
			//constricted.setMaximumSize(constrict);
		}
		for(int i = 0; i < comps.length; ++i)
		{
			constricted.add(comps[i]);
			if(i != comps.length - 1)
				constricted.add(Box.createHorizontalStrut(10));
		}

		return constricted;
	}
	
	public static JPanel horizontalBox(Component... comps)
	{
		JPanel constricted = new JPanel();
		constricted.setLayout(new BoxLayout(constricted, BoxLayout.X_AXIS));
		constricted.setBackground(null);
		for(int i = 0; i < comps.length; ++i)
		{
			constricted.add(comps[i]);
			if(i != comps.length - 1)
				constricted.add(Box.createHorizontalStrut(10));
		}

		return constricted;
	}
	
	public static JPanel aligned(Component comp, int xAlign, int yAlign)
	{
		JPanel wrapper1 = new JPanel();
		wrapper1.setBackground(comp.getBackground());
		wrapper1.setLayout(new BoxLayout(wrapper1, BoxLayout.X_AXIS));
		if(xAlign == SwingConstants.RIGHT || xAlign == SwingConstants.CENTER)
			wrapper1.add(Box.createHorizontalGlue());
		wrapper1.add(comp);
		if(xAlign == SwingConstants.LEFT || xAlign == SwingConstants.CENTER)
			wrapper1.add(Box.createHorizontalGlue());
		
		JPanel wrapper2 = new JPanel();
		wrapper2.setBackground(comp.getBackground());
		wrapper2.setLayout(new BoxLayout(wrapper2, BoxLayout.Y_AXIS));
		if(yAlign == SwingConstants.BOTTOM || yAlign == SwingConstants.CENTER)
			wrapper2.add(Box.createVerticalGlue());
		wrapper2.add(wrapper1);
		if(yAlign == SwingConstants.TOP || yAlign == SwingConstants.CENTER)
			wrapper2.add(Box.createVerticalGlue());
		
		return wrapper2;
	}

	public static void horizontalBoxExisting(JComponent comp, JComponent... comps)
	{
		comp.removeAll();
		for(int i = 0; i < comps.length; ++i)
		{
			comp.add(comps[i]);
			if(i != comps.length - 1)
				comp.add(Box.createHorizontalStrut(10));
		}
	}

	public static JPanel verticalBox(int gap, Component... comps)
	{
		JPanel constricted = new JPanel();
		constricted.setLayout(new BoxLayout(constricted, BoxLayout.Y_AXIS));
		constricted.setBackground(null);
		for(int i = 0; i < comps.length; ++i)
		{
			constricted.add(comps[i]);
			if(i != comps.length - 1 && gap > 0)
				constricted.add(Box.createVerticalStrut(gap));
		}

		return constricted;
	}
	
	public static JPanel verticalBox(Component... comps)
	{
		return verticalBox(10, comps);
	}
}
