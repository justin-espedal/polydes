package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.builtin.ArrayType.SimpleArrayEditor;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType.SingleLineStringEditor;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;
import stencyl.ext.polydes.datastruct.utils.DLang;
import stencyl.ext.polydes.datastruct.utils.ListElementArrays;

public class SelectionType extends BuiltinType<String>
{
	public SelectionType()
	{
		super(String.class, "String", "TEXT", "Selection");
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
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "Dropdown", "RadioButtons"/*, "Grid", "Cycle"*/);
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
		
		//=== Options
		
		final DataEditor<DataList> optionsField = new SimpleArrayEditor(style, Types._String);
		optionsField.setValue(e.options);
		optionsField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.options = optionsField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Default Value
		
		final DataEditor<String> defaultField = new SingleLineStringEditor(style);
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addGenericRow(expansion, "Editor", editorChooser);
		panel.addGenericRow(expansion, "Options", optionsField, StructureObjectPanel.RESIZE_FLAG);
		panel.addGenericRow(expansion, "Default", defaultField);
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
	
	class Extras extends ExtraProperties
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
	
	enum Editor
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
			String[] optionStrings = ListElementArrays.toStrings(options);
			
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
			return comps(editor);
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
			
			String[] optionStrings = ListElementArrays.toStrings(options);
			
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
			return comps(buttonPanel);
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
