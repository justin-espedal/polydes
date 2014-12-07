package stencyl.ext.polydes.paint.app;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import stencyl.sw.util.comp.GradientPanel;

public class StatusBar
{
	public static final Color START_COLOR = new Color(56, 56, 56);
	public static final Color END_COLOR = new Color(45, 45, 45);
	public static final Color TEXT_COLOR = new Color(161, 161, 161);
	public static final int HEIGHT = 21;

	private StatusBar()
	{
	}

	public static JPanel createStatusBar()
	{
		JPanel bar = new GradientPanel("", START_COLOR, END_COLOR, TEXT_COLOR, HEIGHT);
		
		bar.removeAll();
		bar.setBorder(BorderFactory.createMatteBorder(1, 0,	0, 0, new Color(0x2d2d2d)));
		
//		bar.setBackground(MainEditor.SIDEBAR_COLOR);
//		((FlowLayout) bar.getLayout()).setHgap(0);
//		((FlowLayout) bar.getLayout()).setVgap(0);
//		((FlowLayout) bar.getLayout()).setAlignment(FlowLayout.LEFT);
//		
		
		return bar;
	}
}
