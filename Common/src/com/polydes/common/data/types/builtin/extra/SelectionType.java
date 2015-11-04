package com.polydes.common.data.types.builtin.extra;

import static com.polydes.common.util.Lang.array;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class SelectionType extends DataType<String>
{
	public SelectionType()
	{
		super(String.class, "com.polydes.common.Selection");
	}
	
	@Override
	public DataEditor<String> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		DataList options = e.options;
		
		if(options == null || options.isEmpty())
			return new InvalidEditor<String>("The selected source has no items", style);
		
		if(e.editor.equals(Editor.RadioButtons))
		{
			return new RadioButtonsSelectionEditor(options, style);
		}
		else// if(e.editor.equals(Editor.Dropdown))
		{
			return new DropdownSelectionEditor(options);
		}
		/*else if(e.editor.equals(Editor.Grid))
			return comps(style.createSoftLabel("Grid is unimplemented"));
		else //if(editorType.equals("Cycle"))
			return comps(style.createSoftLabel("Cycle is unimplemented"));*/
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
		if(e.options != null)
			emap.put("options", Types._Array.encode(e.options));
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Editor editor;
		public DataList options;//editor Simple, genType String
		public String defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static enum Editor
	{
		Dropdown,
		RadioButtons/*,
		Grid,
		Cycle;*/
	}
	
	public static class DropdownSelectionEditor extends DataEditor<String>
	{
		JComboBox<String> editor;
		
		public DropdownSelectionEditor(DataList options)
		{
			String[] optionStrings = DataList.toStrings(options);
			
			editor = new JComboBox<String>(optionStrings);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(String t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public String getValue()
		{
			return "" + editor.getSelectedItem();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(editor);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor = null;
		}
	}
	
	public static class RadioButtonsSelectionEditor extends DataEditor<String>
	{
		ButtonGroup group;
		HashMap<String, JRadioButton> bmap;
		JPanel buttonPanel;
		
		String current;
		
		public RadioButtonsSelectionEditor(DataList options, PropertiesSheetStyle style)
		{
			group = new ButtonGroup();
			ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
			
			String[] optionStrings = DataList.toStrings(options);
			
			bmap = new HashMap<String, JRadioButton>();
			
			JRadioButton b;
			for(final String s : optionStrings)
			{
				buttons.add(b = new JRadioButton(s));
				group.add(b);
				bmap.put(s, b);
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						current = s;
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			buttonPanel = Layout.verticalBox(0, buttons.toArray(new JRadioButton[0]));
		}
		
		@Override
		public void set(String t)
		{
			current = t;
			if(bmap.containsKey(t))
				group.setSelected(bmap.get(t).getModel(), true);
		}
		
		@Override
		public String getValue()
		{
			return current;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(buttonPanel);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			bmap.clear();
			bmap = null;
			buttonPanel = null;
			current = null;
			group = null;
		}
	}
}
