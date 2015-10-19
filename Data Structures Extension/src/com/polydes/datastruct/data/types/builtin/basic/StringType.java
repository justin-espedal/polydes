package com.polydes.datastruct.data.types.builtin.basic;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.BuiltinType;
import com.polydes.datastruct.data.types.builtin.extra.SelectionType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.datastruct.utils.DLang;

public class StringType extends BuiltinType<String>
{
	public StringType()
	{
		super(String.class, "String", "TEXT");
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
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "SingleLine", "Expanding"/*, "Grid", "Cycle"*/);
		final DataEditor<String> editorChooser = new SelectionType.DropdownSelectionEditor(editorChoices);
		editorChooser.setValue(e.editor.name());
		editorChooser.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.editor = Editor.valueOf(editorChooser.getValue());
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Default Value
		
		final DataEditor<String> defaultField = new SingleLineStringEditor(null, style);
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		//=== Regex
		
		final DataEditor<String> regexField = new SingleLineStringEditor(null, style);
		regexField.setValue(Lang.or(e.regex, ""));
		regexField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				String val = regexField.getValue();
				if(val.isEmpty())
					e.regex = null;
				else
					e.regex = val;
			}
		});

		panel.addGenericRow(expansion, "Editor", editorChooser);
		panel.addGenericRow(expansion, "Default", defaultField);
		panel.addGenericRow(expansion, "Regex", regexField);
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
	
	class Extras extends ExtraProperties
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
	
	enum Editor
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
			return comps(editor);
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
			return comps(editor);
		}
	}
}
