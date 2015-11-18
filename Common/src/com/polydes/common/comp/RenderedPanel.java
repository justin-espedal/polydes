package com.polydes.common.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import stencyl.sw.lnf.Theme;
import stencyl.sw.util.comp.RoundedPanel;
import stencyl.sw.util.gfx.ImageUtil;

public class RenderedPanel extends RoundedPanel
{
	final JLabel imageLabel;
	int width;
	int height;
	int padding;
	
	public RenderedPanel(int width, int height, int padding)
	{
		super(0);
		
		this.width = width;
		this.height = height;
		this.padding = padding;
		
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setVerticalAlignment(SwingConstants.CENTER);
//		imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
//		imageLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
//		imageLabel.setForeground(Theme.TEXT_COLOR);		
		
		setLayout(new BorderLayout());
		setBackground(Theme.LIGHT_BG_COLOR);
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		add(imageLabel, BorderLayout.CENTER);
	}
	
	public void setLabel(Image img)
	{
		if(img == null)
			imageLabel.setIcon(null);
		else
		{
			img = ImageUtil.downsize(img, width - padding - 10, height - padding - 20);
			imageLabel.setIcon(new ImageIcon(img));
		}
	}
}