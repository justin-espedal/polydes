package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;
import stencyl.ext.polydes.datastruct.utils.ListElementArrays;

public class SelectionType extends BuiltinType<String>
{
	public SelectionType()
	{
		super(String.class, "String", "TEXT", "Selection");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<String> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		DataList options = e.options;
		
		if(options == null)
			options = new DataList(Types.fromXML("String"));
		
		if(e.editor.equals(Editor.RadioButtons))
		{
			ButtonGroup group = new ButtonGroup();
			ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
			
			String[] optionStrings = ListElementArrays.toStrings(options);
			String oldValue = updater.get();
			boolean valueSelected = false;
			
			JRadioButton b;
			for(final String s : optionStrings)
			{
				buttons.add(b = new JRadioButton(s));
				group.add(b);
				if(s.equals(oldValue))
				{
					valueSelected = true;
					group.setSelected(b.getModel(), true);
				}
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						updater.set(s);
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			if(!valueSelected)
				updater.set("");
			
			return comps(Layout.verticalBox(0, buttons.toArray(new JRadioButton[0])));
		}
		else if(e.editor.equals(Editor.Dropdown))
		{
			String[] optionStrings = ListElementArrays.toStrings(options);
			
			final JComboBox editor = new JComboBox(optionStrings);
			String oldValue = updater.get();
			editor.setSelectedItem(oldValue);
			
			if(oldValue == null || !oldValue.equals(editor.getSelectedItem()))
				updater.set("" + editor.getSelectedItem());
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updater.set("" + editor.getSelectedItem());
				}
			});
			
			return comps(editor);
		}
		else if(e.editor.equals(Editor.Grid))
			return comps(style.createSoftLabel("Grid is unimplemented"));
		else //if(editorType.equals("Cycle"))
			return comps(style.createSoftLabel("Cycle is unimplemented"));
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
		e.editor = extras.get(EDITOR, Editor.Dropdown);
		e.options = extras.get("options", Types._Array, null);
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._String, "");
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put("options", Types._Array.encode(e.options));
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public DataList options;//editor Simple, genType String
		public String defaultValue;
	}
	
	enum Editor
	{
		Dropdown,
		RadioButtons,
		Grid,
		Cycle;
	}
}
