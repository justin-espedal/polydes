package com.polydes.common.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.comp.DisabledPanel;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class FilePreviewer
{
	public static JPanel getPreview(SysFile f)
	{
		String type = Mime.get(f.getFile());
		JComponent toPreview = null;
		
		if(type.startsWith("image"))
			toPreview = buildImagePreview(f.getFile());
		else if(type.startsWith("text") || type.equals("application/octet-stream"))
			toPreview = buildTextPreview(f.getFile());
		
		if(toPreview != null)
		{
			DisabledPanel previewPanel = new DisabledPanel(toPreview);
			previewPanel.setBackground(PropertiesSheetStyle.DARK.pageBg);
			previewPanel.setEnabled(false);
			previewPanel.setDisabledColor(new Color(0, 0, 0, 0));
			return previewPanel;
		}
		
		JPanel filePanel = new JPanel();
		filePanel.add(new JLabel(FileRenderer.fileThumb));
		filePanel.setBackground(PropertiesSheetStyle.DARK.pageBg);
		return filePanel;
	}
	
	private static JComponent buildImagePreview(File f)
	{
		try
		{
			BufferedImage previewImage = ImageIO.read(f);
			if(previewImage.getWidth() > 500 || previewImage.getHeight() > 500)
				previewImage = GraphicsUtilities.createThumbnail(previewImage, 500);
			return new JLabel(new ImageIcon(previewImage));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new JLabel();
		}
	}
	
	private static JComponent buildTextPreview(File f)
	{
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea preview = new JTextArea();
		
		Dimension previewSize = new Dimension(380, 200);
		preview.setMinimumSize(previewSize);
		preview.setMaximumSize(previewSize);
		preview.setPreferredSize(previewSize);
		
		String[] previewLines = FileRenderer.getLines(f, 20);
		preview.setText(StringUtils.join(previewLines,'\n'));
		
		panel.add(preview, BorderLayout.CENTER);
		
		return panel;
	}
}
