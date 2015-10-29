package com.polydes.datastruct.data.types.builtin.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.BuiltinType;
import com.polydes.datastruct.data.types.builtin.extra.SelectionType;
import com.polydes.datastruct.data.types.hidden.DataTypeType;
import com.polydes.datastruct.data.types.hidden.DataTypeType.DataTypeEditor;
import com.polydes.datastruct.ui.comp.DataListEditor;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.datastruct.utils.DLang;
import com.polydes.datastruct.utils.ListElementArrays;
import com.polydes.datastruct.utils.StringData;

public class ArrayType extends BuiltinType<DataList>
{
	public ArrayType()
	{
		//TODO: How does genType work now?
		super(DataList.class, "Array", "LIST");
	}

	@Override
	public DataEditor<DataList> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		final Extras e = (Extras) extras;
		
		if(e.editor.equals(Editor.Simple))
			return new SimpleArrayEditor(style, e.genType);
		else //if(editorType.equals("Standard"))
			return new StandardArrayEditor(e.genType, e.genTypeExtras, style);
	}

	@Override
	public DataList decode(String s)
	{
		if(s.isEmpty())
			return null;
		
		int i = s.lastIndexOf(":");
		String typename = s.substring(i + 1);
		DataType<?> genType = Types.fromXML(typename);
		DataList list = new DataList(genType);
		
		for(String s2 : StringData.getEmbeddedArrayStrings(s))
			list.add(genType.decode(s2));
		
		return list;
	}
	
	@Override
	public String encode(DataList array)
	{
		if(array == null)
			return "";
		
		String s = "[";
		
		for(int i = 0; i < array.size(); ++i)
			s += array.genType.checkEncode(array.get(i)) + (i < array.size() - 1 ? "," : "");
		s += "]:" + array.genType.haxeType;
		
		return s;
	}
	
	@Override
	public String toDisplayString(DataList data)
	{
		return null;
	}

	@Override
	public DataList copy(DataList t)
	{
		DataList copyList = new DataList(t.genType);
		
		for(Object o : t)
			copyList.add(t.genType.checkCopy(o));
		
		return copyList;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		
		//=== Editor
		
		DataList editorChoices = DLang.datalist(Types._String, "Standard", "Simple"/*, "Grid", "Cycle"*/);
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
		
		//=== Type, Default
		
		@SuppressWarnings("rawtypes")
		final DataEditor<DataType> typeField = new DataTypeEditor(DataTypeType.dynamicArraySubTypes);
		final SimpleArrayEditor defaultField = new SimpleArrayEditor(panel.style, e.genType);
		
		typeField.setValue(e.genType);
		typeField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.genType = typeField.getValue();
				preview.refreshDataItem(previewKey);
				defaultField.setType(typeField.getValue());
			}
		});
		
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
		panel.addGenericRow(expansion, "Data Type", typeField);
		panel.addGenericRow(expansion, "Default", defaultField, StructureObjectPanel.RESIZE_FLAG);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Standard);
		e.genType = extras.get("genType", Types._DataType, Types._String);
		if(extras.containsKey("genTypeExtras"))
			e.genTypeExtras = e.genType.loadExtras(extras.getMap("genTypeExtras"));
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Array, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put("genType", e.genType.haxeType);
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public class Extras extends ExtraProperties
	{
		public Editor editor;
		public DataType<?> genType;
		public ExtraProperties genTypeExtras;
		public DataList defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	enum Editor
	{
		Standard,
		Simple;
	}
	
	public static class SimpleArrayEditor extends DataEditor<DataList>
	{
		JTextArea editor;
		DataType<?> genType;
		
		DataList list;
		
		public SimpleArrayEditor(PropertiesSheetStyle style, DataType<?> genType)
		{
			this.genType = genType;
			
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
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				@Override
				protected void update()
				{
					list = ListElementArrays.fromStrings(StringUtils.split(editor.getText(), "\n"), list.genType);
					updated();
				}
			});
		}
		
		public void setType(DataType<?> type)
		{
			if(!genType.haxeType.equals(type.haxeType))
			{
				genType = type;
				set(null);
			}
		}
		
		@Override
		public void set(DataList t)
		{
			if(t == null)
				t = new DataList(genType);
			list = t;
			editor.setText(StringUtils.join(ListElementArrays.toStrings(t), "\n"));
		}
		
		@Override
		public DataList getValue()
		{
			return list;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(editor);
		}
	}
	
	public static class StandardArrayEditor extends DataEditor<DataList>
	{
		DataListEditor editor;
		DataType<?> genType;
		ExtraProperties genTypeExtras;
		
		public StandardArrayEditor(DataType<?> genType, ExtraProperties genTypeExtras, PropertiesSheetStyle style)
		{
			this.genType = genType;
			this.genTypeExtras = genTypeExtras;
			
			editor = new DataListEditor(null, genTypeExtras, style);
			
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
		public void set(DataList t)
		{
			if(t == null)
				t = new DataList(genType);
			editor.setData(t, genTypeExtras);
		}
		
		@Override
		public DataList getValue()
		{
			return editor.getModel();
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
			editor.dispose();
			editor = null;
		}
	}
}
