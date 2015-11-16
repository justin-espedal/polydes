package com.polydes.common.data.types.builtin.extra;

import static com.polydes.common.util.Lang.or;

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
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class SelectionType extends DataType<String>
{
	public SelectionType()
	{
		super(String.class, "com.polydes.common.Selection");
	}
	
	public static final String OPTIONS = "options";
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new SelectionEditorBuilder();
	}
	
	@Override
	public DataEditor<String> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		DataList options = props.get(OPTIONS);
		
		if(options == null || options.isEmpty())
			return new InvalidEditor<String>("The selected source has no items", style);
		
		switch(or(props.<Editor>get(EDITOR), Editor.Dropdown))
		{
			case RadioButtons:
				return new RadioButtonsSelectionEditor(options, style);
			default:
				return new DropdownSelectionEditor(options);	
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
	
	public DataList options;
	
	public class SelectionEditorBuilder extends DataEditorBuilder
	{
		public SelectionEditorBuilder()
		{
			super(SelectionType.this, new EditorProperties(){{
				put(EDITOR, Editor.Dropdown);
			}});
		}
		
		public SelectionEditorBuilder radioButtonsEditor()
		{
			props.put(EDITOR, Editor.RadioButtons);
			return this;
		}
		
		public SelectionEditorBuilder source(DataList list)
		{
			props.put(OPTIONS, list);
			return this;
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
		final JComboBox<String> editor;
		
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
			return new JComponent[] {editor};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
		}
	}
	
	public static class RadioButtonsSelectionEditor extends DataEditor<String>
	{
		final ButtonGroup group;
		final HashMap<String, JRadioButton> bmap;
		final JPanel buttonPanel;
		
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
			return new JComponent[] {buttonPanel};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			bmap.clear();
			current = null;
		}
	}
}
