package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.Main;
import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.hidden.DataTypeType;
import stencyl.ext.polydes.datastruct.data.types.hidden.DataTypeType.DataTypeEditor;
import stencyl.ext.polydes.datastruct.ui.comp.DataListEditor;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.utils.Lang;
import stencyl.ext.polydes.datastruct.utils.ListElementArrays;
import stencyl.ext.polydes.datastruct.utils.StringData;

public class ArrayType extends BuiltinType<DataList>
{
	public ArrayType()
	{
		super(DataList.class, "Array<Dynamic>", "LIST", "Array");
	}

	@Override
	public DataEditor<DataList> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		final Extras e = (Extras) extras;
		
		if(e.editor.equals(Editor.Simple))
			return new SimpleArrayEditor(style, e.genType);
		else //if(editorType.equals("Standard"))
			return new StandardArrayEditor();
	}

	@Override
	public DataList decode(String s)
	{
		if(s.isEmpty())
			return new DataList(Types._Dynamic);
		
		//backwards compatibility
		if(!s.startsWith("["))
		{
			Main.forceUpdateData = true;
			
			String[] strings = StringUtils.split(s, ",");
			
			DataType<?> genType;
			DataList list;
			
			if(strings[0].indexOf(":") == -1)
			{
				genType = Types.fromXML("String");
				list = new DataList(genType);
				for(String s2 : strings)
					list.add(s2);
			}
			else
			{
				genType = Types.fromXML(strings[0].split(":")[1]);
				list = new DataList(genType);
				for(String s2 : strings)
					list.add(genType.decode(s2.split(":")[0]));
			}
			
			return list;
		}
		
		/*
		BranchNode node = StringData.readTree(s);
		
		String typename;
		if(((String) node.data).isEmpty())
			typename = "String";
		else
			typename = ((String) node.data);
		
		DataType<?> genType = Types.fromXML(typename);
		
		DataList list = new DataList(genType);
		
		for(Node n : node.children)
			list.add(genType.decode((String) n.data));
		*/
		
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
		String s = "[";
		
		for(int i = 0; i < array.size(); ++i)
			s += array.genType.checkEncode(array.get(i)) + (i < array.size() - 1 ? "," : "");
		s += "]:" + array.genType.xml;
		
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
		
		DataList editorChoices = Lang.datalist(Types._String, "Standard", "Simple"/*, "Grid", "Cycle"*/);
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
		
		//=== Type
		
		@SuppressWarnings("rawtypes")
		final DataEditor<DataType> typeField = new DataTypeEditor(DataTypeType.dynamicArraySubTypes);
		typeField.setValue(e.genType);
		typeField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.genType = typeField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Default Value
		/*
		final DataEditor<DataList> defaultField = new SimpleArrayEditor(style, e.genType);
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		*/
		panel.addGenericRow(expansion, "Editor", editorChooser);
		panel.addGenericRow(expansion, "Options", typeField);
		//panel.addGenericRow(expansion, "Default", defaultField);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Standard);
		e.genType = extras.get("genType", Types._DataType, Types._String);
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Array, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put("genType", e.genType.xml);
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public DataType<?> genType;
		public DataList defaultValue;
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
		
		public StandardArrayEditor()
		{
			editor = new DataListEditor(null);
			
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
				t = new DataList(Types._String);
			editor.setList(t);
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
	}
}
