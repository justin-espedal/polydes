package stencyl.ext.polydes.datastruct.ui.list;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import stencyl.sw.lnf.Theme;

import com.jidesoft.swing.PaintPanel;

public class ListUtils
{
	public static JComponent addHeader(JComponent component, String text)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createHeader(text), BorderLayout.NORTH);
		panel.add(component, BorderLayout.CENTER);
		
		return panel;
	}
	
	public static JComponent createHeader(String text)
	{
		PaintPanel header = new PaintPanel();
		header.setVertical(true);
		header.setStartColor(Theme.BUTTON_BAR_START);
		header.setEndColor(Theme.BUTTON_BAR_END);
		
		header.setPreferredSize(new Dimension(1, 20));
		
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		header.add(label);
		
		return header;
	}
}
