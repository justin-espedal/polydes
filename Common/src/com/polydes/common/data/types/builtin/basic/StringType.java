package com.polydes.common.data.types.builtin.basic;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.polydes.common.comp.utils.DocumentAdapter;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class StringType extends DataType<String>
{
	public StringType()
	{
		super(String.class);
	}
	
	@Override
	public DataEditor<String> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		if(e.editor.equals(Editor.Expanding))
			return new ExpandingStringEditor(e, style);
		else //if(editorType.equals("Single Line"))
			return new SingleLineStringEditor(e, style);
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
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.SingleLine);
		e.defaultValue = extras.get("defaultValue", Types._String, "");
		e.regex = extras.get("regex", Types._String, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		if(e.regex != null)
			emap.put("regex", e.regex);
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Editor editor;
		public String defaultValue;
		public String regex;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static enum Editor
	{
		SingleLine,
		Expanding
	}
	
	public static class SingleLineStringEditor extends DataEditor<String>
	{
		JTextField editor;
		String regex;
		
		public SingleLineStringEditor(Extras e, PropertiesSheetStyle style)
		{
			editor = style.createTextField();
			if(e != null)
				regex = e.regex;
			
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
		JTextArea editor;
		String regex;
		
		public ExpandingStringEditor(Extras e, PropertiesSheetStyle style)
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
			
			if(e != null)
				regex = e.regex;
			
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
