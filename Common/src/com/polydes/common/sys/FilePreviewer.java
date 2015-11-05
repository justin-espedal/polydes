package com.polydes.common.sys;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.FileHelper;

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
			JPanel previewPanel = new JPanel();
			previewPanel.setBackground(PropertiesSheetStyle.DARK.pageBg);
			previewPanel.add(toPreview);
			
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
			return new JLabel(new ImageIcon(ImageIO.read(f)));
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
		preview.setEditable(false);
		Dimension previewSize = new Dimension(380, 200);
		preview.setMinimumSize(previewSize);
		preview.setMaximumSize(previewSize);
		preview.setPreferredSize(previewSize);
		try
		{
			preview.setText(FileHelper.readFileToString(f));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		panel.add(preview, BorderLayout.CENTER);
		
		return panel;
	}
}
