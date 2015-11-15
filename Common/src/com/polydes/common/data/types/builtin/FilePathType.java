package com.polydes.common.data.types.builtin;

import java.io.File;

import javax.swing.JComponent;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.builtin.FileType.FileEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class FilePathType extends DataType<String>
{
	public FilePathType()
	{
		super(String.class, "com.polydes.common.FilePath");
	}

	public static final String ONLY_DIRECTORIES = "onlyDirectories";
	
	@Override
	public DataEditor<String> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new FilePathEditor(props, style);
	}

	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new FilePathEditorBuilder();
	}

	@Override
	public String decode(String s)
	{
		return s;
	}

	@Override
	public String encode(String t)
	{
		return t;
	}

	@Override
	public String copy(String t)
	{
		return t;
	}
	
	public class FilePathEditorBuilder extends DataEditorBuilder
	{
		public FilePathEditorBuilder()
		{
			super(FilePathType.this, new EditorProperties());
		}
		
		public FilePathEditorBuilder onlyDirectories()
		{
			props.put(ONLY_DIRECTORIES, true);
			return this;
		}
	}
	
	public static class FilePathEditor extends DataEditor<String>
	{
		private FileEditor editor;
		
		public FilePathEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			editor = new FileEditor(props, style);
		}
		
		@Override
		public String getValue()
		{
			File f = editor.getValue();
			return f == null ? "" : f.getAbsolutePath();
		}

		@Override
		protected void set(String s)
		{
			editor.setValue(new File(s));
		}

		@Override
		public JComponent[] getComponents()
		{
			return editor.getComponents();
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
		}
	}
}
