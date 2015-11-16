package com.polydes.common.data.types.builtin.basic;

import static com.polydes.common.util.Lang.or;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.polydes.common.comp.utils.DocumentAdapter;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class StringType extends DataType<String>
{
	public StringType()
	{
		super(String.class);
	}
	
	public static final String REGEX = "regex";
	
	@Override
	public DataEditor<String> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		switch(or(props.<Editor>get(EDITOR), Editor.SingleLine))
		{
			case Expanding:
				return new ExpandingStringEditor(props, style);
			default:
				return new SingleLineStringEditor(props, style);	
		}
	}

	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new StringEditorBuilder();
	}
	
	@Override
	public String decode(String s)
	{
		return s;
	}

	@Override
	public String encode(String s)
	{
		return s;
	}

	@Override
	public String copy(String t)
	{
		return t;
	}
	
	public class StringEditorBuilder extends DataEditorBuilder
	{
		public StringEditorBuilder()
		{
			super(StringType.this, new EditorProperties(){{
				put(EDITOR, Editor.SingleLine);
			}});
		}

		public StringEditorBuilder expandingEditor()
		{
			props.put(EDITOR, Editor.Expanding);
			return this;
		}
		
		public StringEditorBuilder regex(String pattern)
		{
			props.put(REGEX, pattern);
			return this;
		}
	}
	
	public static enum Editor
	{
		SingleLine,
		Expanding
	}
	
	public static class SingleLineStringEditor extends DataEditor<String>
	{
		final JTextField editor;
		String regex;
		
		public SingleLineStringEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			editor = style.createTextField();
			regex = props.get(REGEX);
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				Color fg = editor.getForeground();
				
				@Override
				protected void update()
				{
					boolean valid = (regex == null || editor.getText().matches(regex));
					if(valid)
						updated();
					editor.setForeground(valid ? fg : Color.RED);
				}
			});
		}
		
		@Override
		public void set(String t)
		{
			if(t == null)
				t = "";
			editor.setText(t);
		}
		
		@Override
		public String getValue()
		{
			return editor.getText();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
	}
	
	public static class ExpandingStringEditor extends DataEditor<String>
	{
		final JTextArea editor;
		String regex;
		
		public ExpandingStringEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			editor = new JTextArea();
			editor.setBackground(style.fieldBg);
			editor.setForeground(style.fieldtextColor);
			editor.setCaretColor(style.fieldtextColor);
			editor.setLineWrap(true);
			editor.setWrapStyleWord(true);
			if(style.fieldBorder != null)
				editor.setBorder
				(
					BorderFactory.createCompoundBorder
					(
						BorderFactory.createLineBorder(style.fieldBorder, 1),
						BorderFactory.createEmptyBorder(0, 3, 0, 3)
					)
				);
			
			regex = props.get(REGEX);
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				Color fg = editor.getForeground();
				
				@Override
				protected void update()
				{
					boolean valid = (regex == null || editor.getText().matches(regex));
					if(valid)
						updated();
					editor.setForeground(valid ? fg : Color.RED);
				}
			});
		}
		
		@Override
		public void set(String t)
		{
			if(t == null)
				t = "";
			editor.setText(t);
		}
		
		@Override
		public String getValue()
		{
			return editor.getText();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
	}
}
