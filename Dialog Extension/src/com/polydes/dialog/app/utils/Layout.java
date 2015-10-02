package com.polydes.dialog.app.utils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Layout
{
	public static JPanel horizontalBox(JComponent... comps)
	{
		JPanel constricted = new JPanel();
		constricted.setLayout(new BoxLayout(constricted, BoxLayout.X_AXIS));
		constricted.setBackground(null);
		//constricted.setMinimumSize(new Dimension(360, 20));
		//constricted.setPreferredSize(new Dimension(360, 20));
		//constricted.setMaximumSize(new Dimension(360, 20));
		for(int i = 0; i < comps.length; ++i)
		{
			constricted.add(comps[i]);
			if(i != comps.length - 1)
				constricted.add(Box.createHorizontalStrut(10));
		}

		return constricted;
	}
	
	public static JPanel aligned(JComponent comp, int xAlign, int yAlign)
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
}
