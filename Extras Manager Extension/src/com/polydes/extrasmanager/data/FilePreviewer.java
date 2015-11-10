package com.polydes.extrasmanager.data;

import java.awt.Color;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.polydes.common.sys.Mime;
import com.polydes.common.sys.SysFile;

import stencyl.sw.util.FileHelper;

public class FilePreviewer
{
	private static Color BACKGROUND_COLOR = new Color(62, 62, 62);
	private static SysFile previewFile = null;
	
	public static void preview(SysFile f)
	{
		String type = Mime.get(f.getFile());
		JComponent toPreview = null;
		
		if(type.startsWith("image"))
			toPreview = buildImagePreview(f.getFile());
		else if(type.startsWith("text"))
			toPreview = buildTextPreview(f.getFile());
		
		if(toPreview != null)
		{
			JPanel previewPanel = new JPanel();
			previewPanel.setBackground(BACKGROUND_COLOR);
			previewPanel.add(toPreview);
			previewFile = f;
		}
	}
	
	public static void endPreview()
	{
		previewFile = null;
	}
	
	public static SysFile getPreviewFile()
	{
		return previewFile;
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
		JPanel panel = new JPanel();
		TextArea preview = new TextArea();
		try
		{
			preview.setText(FileHelper.readFileToString(f));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		panel.add(preview);
		
		return panel;
	}
}
