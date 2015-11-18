package com.polydes.common.data.types.builtin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.SW;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.filechooser.ImageFileView;
import stencyl.thirdparty.misc.comp.ImagePreview;

public class FileType extends DataType<File>
{
	public FileType()
	{
		super(File.class);
	}

	public static final String ROOT_DIRECTORY = "rootDirectory";
	public static final String ONLY_DIRECTORIES = "onlyDirectories";
	public static final String FILTER = "filter";
	
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
		
		public FileEditorBuilder rootDirectory(String path)
		{
			props.put(ROOT_DIRECTORY, path);
			return this;
		}
		
		public FileEditorBuilder onlyDirectories()
		{
			props.put(ONLY_DIRECTORIES, true);
			return this;
		}
		
		public FileEditorBuilder filter(DualFileFilter filter)
		{
			props.put(FILTER, filter);
			return this;
		}
	}
	
	public static class FileEditor extends DataEditor<File>
	{
		private File file;
		final GroupButton button;
		final JTextField pathField;
		String rootDirectory;
		
		public FileEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			boolean onlyDirectories = props.get(ONLY_DIRECTORIES) == Boolean.TRUE;
			rootDirectory = props.get(ROOT_DIRECTORY);
			DualFileFilter filter = props.get(FILTER);
			
			button = new GroupButton(4);
			button.setText("Choose");
			
			button.addActionListener(e -> {
//				if(Util.isMacOSX()) 
//				{
//					if(onlyDirectories)
//						System.setProperty("apple.awt.fileDialogForDirectories", "true");
//					
//					FileDialog fc = new FileDialog(SW.get(), "Choose");
//					fc.setDirectory(rootDirectory);
//					fc.setFilenameFilter(filter);
//					fc.setPreferredSize(new Dimension(800, 600));
//					fc.setVisible(true);
//					
//					if(fc.getFile() != null)
//						file = new File(fc.getDirectory(), fc.getFile());
//					
//					if(onlyDirectories)
//						System.setProperty("apple.awt.fileDialogForDirectories", "false");
//				}
//				
//				else
//				{
					File root = rootDirectory != null ? new File(rootDirectory) : null;
					FileSystemView fsv = root != null ? new SingleRootFileSystemView(root) : null;
					
					JFileChooser fc = new JFileChooser(fsv);
					fc.setAcceptAllFileFilterUsed(filter == null);
					fc.setFileFilter(filter);
					if(root != null)
						fc.setCurrentDirectory(root);
					
					if(onlyDirectories)
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					if(filter == ExtensionFilter.PNG_FILTER)
					{
						fc.setFileView(new ImageFileView());
						fc.setAccessory(new ImagePreview(fc));
					}
					
					int returnVal = fc.showDialog(SW.get(), "Choose");
				    
					if(returnVal == JFileChooser.APPROVE_OPTION) 
						file = fc.getSelectedFile();
//				}
				
				updatePathField();
				updated();
			});
			
			pathField = style.createTextField();
		}
		
		@Override
		public File getValue()
		{
			return file;
		}
		
		private void updatePathField()
		{
			String fullPath = file == null ? "" : file.getAbsolutePath();
			String shortPath = fullPath.substring(StringUtils.getCommonPrefix(fullPath, rootDirectory).length());
			pathField.setText(shortPath);
		}

		@Override
		protected void set(File t)
		{
			file = t;
			updatePathField();
		}

		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {button, pathField};
		}
	}
	
	// http://www.camick.com/java/source/SingleRootFileSystemView.java
		
	/**
	 *  A FileSystemView class that limits the file selections to a single root.
	 *
	 *  When used with the JFileChooser component the user will only be able to
	 *  traverse the directories contained within the specified root fill.
	 *
	 *  The "Look In" combo box will only display the specified root.
	 *
	 *  The "Up One Level" button will be disable when at the root.
	 *
	 */
	public static class SingleRootFileSystemView extends FileSystemView
	{
		File root;
		File[] roots = new File[1];

		public SingleRootFileSystemView(File root)
		{
			super();
			this.root = root;
			roots[0] = root;
		}

		@Override
		public File createNewFolder(File containingDir)
		{
			File folder = new File(containingDir, "New Folder");
			folder.mkdir();
			return folder;
		}

		@Override
		public File getDefaultDirectory()
		{
			return root;
		}

		@Override
		public File getHomeDirectory()
		{
			return root;
		}

		@Override
		public File[] getRoots()
		{
			return roots;
		}
	}
	
	public static abstract class DualFileFilter extends FileFilter implements FilenameFilter
	{
	}
	
	public static final class ExtensionFilter extends DualFileFilter
	{
		public static final ExtensionFilter PNG_FILTER = new ExtensionFilter("png");
		
		private final String description;
		private final Set<String> extensions;
		
		public ExtensionFilter(String extension)
		{
			extensions = new HashSet<String>();
			extensions.add(extension);
			description = "." + extension.toUpperCase(Locale.ENGLISH);
		}
		
		public ExtensionFilter(String description, Set<String> extension)
		{
			this.extensions = extension;
			this.description = description;
		}
		
		public ExtensionFilter(String description, String[] extension)
		{
			this.extensions = new HashSet<String>(Arrays.asList(extension));
			this.description = description;
		}
		
		@Override
		public boolean accept(File f)
		{
			String extension = FilenameUtils.getExtension(f.getName()).toLowerCase(Locale.ENGLISH);
			return f.isDirectory() || extensions.contains(extension);
		}
		
		@Override
		public String getDescription()
		{
			return description;
		}

		@Override
		public boolean accept(File dir, String name)
		{
			return accept(new File(dir, name));
		}
	}
}
