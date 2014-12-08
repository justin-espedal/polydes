package stencyl.ext.polydes.datastruct.ui.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.UI;
import stencyl.sw.util.comp.RoundedLabel;

public abstract class PropertiesSheetStyle
{
	public static Dark DARK = new Dark();
	public static Light LIGHT = new Light();
	
	public Color pageBg;
	public Color fieldBg;
	public Color fieldBorder;
	public Color fieldtextColor;
	public Color labelColor;
	public Color softLabelColor;
	
	public Font labelFont;
	public Font softLabelFont;
	
	public Dimension fieldDimension;
	public Border border;
	
	public int rowgap;
	public int tabsetgap;
	public int hintgap;
	
	public JTextField createTextField()
	{
		JTextField field = new JTextField();
		field.setBackground(fieldBg);
		field.setForeground(fieldtextColor);
		field.setCaretColor(fieldtextColor);
		if(fieldBorder != null)
			field.setBorder
			(
				BorderFactory.createCompoundBorder
				(
					BorderFactory.createLineBorder(fieldBorder, 1),
					BorderFactory.createEmptyBorder(0, 3, 0, 3)
				)
			);
		return field;
	}
	
	public JLabel createLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(labelColor);
		label.setFont(labelFont);
		return label;
	}

	public JLabel createSoftLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(softLabelColor);
		label.setFont(softLabelFont);
		return label;
	}
	
	public abstract RoundedLabel createRoundedLabel(String text);
	public abstract JLabel createDescriptionRow(String desc);
	public abstract void setDescription(JLabel label, String desc);
	
	public JLabel createEditorHint(String text)
	{
		JLabel label = new JLabel(Resources.loadIcon("question-small-white.png"));
		label.setToolTipText("<html>" + text + "</html>");
		
		return label;
	}

	public JScrollPane createScroller(JComponent editor)
	{
		JScrollPane scroller = UI.createScrollPane(editor);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		return scroller;
	}
	
	public static class Dark extends PropertiesSheetStyle
	{
		public Dark()
		{
			pageBg = new Color(64, 64, 64);
			fieldBg = new Color(192, 192, 192);
			fieldBorder = null;
			fieldtextColor = Color.BLACK;
			labelColor = Color.WHITE;
			softLabelColor = labelColor.darker();
			
			labelFont = Fonts.getBoldFont();
			softLabelFont = Fonts.getNormalFont().deriveFont(Font.BOLD|Font.ITALIC);
			
			fieldDimension = new Dimension(300, 20);
			border = BorderFactory.createEmptyBorder(20, 20, 20, 20);
			
			rowgap = 14;
			tabsetgap = 10;
			hintgap = 7;
		}
		
		@Override
		public RoundedLabel createRoundedLabel(String text)
		{
			return new RoundedLabel(text);
		}
		
		@Override
		public JLabel createDescriptionRow(String desc)
		{
			JLabel l = new JLabel();
			l.setForeground(labelColor.darker());
			setDescription(l, desc);
			
			return l;
		}
		
		@Override
		public void setDescription(JLabel label, String desc)
		{
			label.setText(String.format("<html>%s<html>", desc));
			label.setPreferredSize(null);
			label.setPreferredSize(new Dimension(fieldDimension.width, label.getPreferredSize().height * (label.getPreferredSize().width / fieldDimension.width + 1)));
		}
	}
	
	public static class Light extends PropertiesSheetStyle
	{
		Color headerBackground = new Color(185, 185, 185);
		
		public Light()
		{
			pageBg = null;
			fieldBg = Color.WHITE;
			fieldBorder = new Color(160, 160, 160);
			fieldtextColor = Color.BLACK;
			labelColor = Color.BLACK;
			softLabelColor = new Color(65, 65, 65);
			
			labelFont = Fonts.getNormalFont();
			softLabelFont = Fonts.getNormalFont().deriveFont(Font.BOLD|Font.ITALIC);
			
			fieldDimension = new Dimension(150, 20);
			border = BorderFactory.createEmptyBorder(7, 7, 7, 7);
			
			rowgap = 5;
			tabsetgap = 5;
			hintgap = 5;
		}
		
		@Override
		public RoundedLabel createRoundedLabel(String text)
		{
			RoundedLabel label = new RoundedLabel(text);
			label.setColor(headerBackground);
			label.setForeground(fieldtextColor);
			return label;
		}
		
		@Override
		public JLabel createDescriptionRow(String desc)
		{
			JLabel l = new JLabel();
			l.setForeground(labelColor.brighter());
			setDescription(l, desc);
			
			return l;
		}
		
		@Override
		public void setDescription(JLabel label, String desc)
		{
			label.setText(String.format("<html>%s<html>", desc));
			label.setPreferredSize(null);
			label.setPreferredSize(new Dimension(fieldDimension.width, label.getPreferredSize().height * (label.getPreferredSize().width / fieldDimension.width + 1)));
		}
	}
}