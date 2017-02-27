package com.polydes.scenelink.ui;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.apache.commons.io.FileUtils;

import com.polydes.scenelink.SceneLinkExtension;
import com.polydes.scenelink.res.Resources;
import com.polydes.scenelink.util.PngFilter;

import misc.comp.ImagePreview;
import stencyl.sw.SW;
import stencyl.sw.ext.FileHandler;
import stencyl.sw.util.Util;
import stencyl.sw.util.filechooser.ImageFileView;

public class ImageImportButton extends JButton implements ActionListener
{
	public static String lastImported = "";
	
	private final ArrayList<ActionListener> listeners;
	
	public ImageImportButton()
	{
		setIcon(Resources.loadIcon("plus.png"));
		super.addActionListener(this);
		listeners = new ArrayList<ActionListener>();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File file = null;
		
		if(Util.isMacOSX()) 
		{
			FileDialog fc = new FileDialog(SW.get(), "Import Image");
			fc.setPreferredSize(new Dimension(800, 600));
			fc.setVisible(true);
			fc.setFilenameFilter(new PngFilter());
			
			if(fc.getFile() != null)
			{
				file = new File(fc.getDirectory(), fc.getFile());
			}
		}
		
		else
		{
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new PngFilter());
			fc.setFileView(new ImageFileView());
			fc.setAccessory(new ImagePreview(fc));
			
			int returnVal = fc.showDialog(SW.get(), "Import Image");
		    
			if(returnVal == JFileChooser.APPROVE_OPTION)
		    {
		        file = fc.getSelectedFile();
		    }
		}

		if(file != null) 
		{
			imageHandler.handleFile(file);
			for(ActionListener l : listeners)
			{
				l.actionPerformed(new ActionEvent(this, 0, ""));
			}
		}
	}
	
	public static FileHandler imageHandler = new FileHandler()
	{
		@Override
		public void handleFile(File f)
		{
			try
			{
				String name = f.getName();
				String noext = name.substring(0, name.length() - 4);
				String forcedlowercase = noext + ".png";
				FileUtils.copyFile(f, new File(SceneLinkExtension.resourcesFolder, forcedlowercase));
				Resources.loadResourceNames();
				lastImported = noext;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		};
	};
	
	@Override
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	@Override
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
}
