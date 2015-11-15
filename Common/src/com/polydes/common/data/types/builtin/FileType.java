package com.polydes.common.data.types.builtin;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.SW;
import stencyl.sw.util.Util;
import stencyl.sw.util.comp.GroupButton;

public class FileType extends DataType<File>
{
	public FileType()
	{
		super(File.class);
	}

	public static final String ONLY_DIRECTORIES = "onlyDirectories";
	
	@Override
	public DataEditor<File> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new FileEditor(props, style);
	}

	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new FileEditorBuilder();
	}

	@Override
	public File decode(String s)
	{
		return new File(s);
	}

	@Override
	public String encode(File t)
	{
		return t.getAbsolutePath();
	}

	@Override
	public File copy(File t)
	{
		return t;
	}
	
	public class FileEditorBuilder extends DataEditorBuilder
	{
		public FileEditorBuilder()
		{
			super(FileType.this, new EditorProperties());
		}
		
		public FileEditorBuilder onlyDirectories()
		{
			props.put(ONLY_DIRECTORIES, true);
			return this;
		}
	}
	
	public class FileEditor extends DataEditor<File>
	{
		private File file;
		GroupButton button;
		JTextField pathField;
		
		public FileEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			boolean onlyDirectories = props.get(ONLY_DIRECTORIES);
			
			button = new GroupButton(4);
			button.setText("Choose");
			
			button.addActionListener(e -> {
				if(Util.isMacOSX()) 
				{
					if(onlyDirectories)
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
					
					FileDialog fc = new FileDialog(SW.get(), "Choose");
					fc.setPreferredSize(new Dimension(800, 600));
					fc.setVisible(true);
					
					if(fc.getFile() != null)
						file = new File(fc.getDirectory(), fc.getFile());
					
					if(onlyDirectories)
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
				}
				
				else
				{
					JFileChooser fc = new JFileChooser();
					fc.setAcceptAllFileFilterUsed(true);
					
					if(onlyDirectories)
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					int returnVal = fc.showDialog(SW.get(), "Choose");
				    
					if(returnVal == JFileChooser.APPROVE_OPTION) 
						file = fc.getSelectedFile();
				}
				
				updated();
			});
			
			pathField = style.createTextField();
		}
		
		@Override
		public File getValue()
		{
			return file;
		}

		@Override
		protected void set(File t)
		{
			file = t;
			pathField.setText(file == null ? "" : file.getAbsolutePath());
		}

		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {button, pathField};
		}
	}
}
