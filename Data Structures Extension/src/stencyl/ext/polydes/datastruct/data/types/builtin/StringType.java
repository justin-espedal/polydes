package stencyl.ext.polydes.datastruct.data.types.builtin;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;

public class StringType extends BuiltinType<String>
{
	public StringType()
	{
		super(String.class, "String", "TEXT", "String");
	}
	
	@Override
	public JComponent[] getEditor(final DataUpdater<String> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		if(e.editor.equals(Editor.Expanding))
		{
			final JTextArea editor = new JTextArea();
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
			
			editor.setText(updater.get());
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				@Override
				protected void update()
				{
					updater.set(editor.getText());
				}
			});
			
			return comps(editor);
		}
		else //if(editorType.equals("Single Line"))
		{
			final JTextField editor = style.createTextField();
			editor.setText(updater.get());
		
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				@Override
				protected void update()
				{
					updater.set(editor.getText());
				}
			});
			
			return comps(editor);
		}
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
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public String defaultValue;
	}
	
	enum Editor
	{
		SingleLine,
		Expanding
	}
}
