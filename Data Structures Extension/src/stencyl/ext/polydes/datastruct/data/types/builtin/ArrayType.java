package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.Main;
import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.DataListEditor;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.utils.ListElementArrays;
import stencyl.ext.polydes.datastruct.utils.StringData;

public class ArrayType extends BuiltinType<DataList>
{
	private DynamicType type = new DynamicType();
	
	public ArrayType()
	{
		super(DataList.class, "Array<Dynamic>", "LIST", "Array");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<DataList> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final Extras e = (Extras) extras;
		
		if(updater.get() == null || !updater.get().genType.xml.equals(e.genType.xml))
			updater.set(new DataList(e.genType));
		
		if(e.editor.equals(Editor.Simple))
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
			
			editor.setText(StringUtils.join(ListElementArrays.toStrings(updater.get()), "\n"));
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(false)
			{
				@Override
				protected void update()
				{
					updater.set(ListElementArrays.fromStrings(StringUtils.split(editor.getText(), "\n"), e.genType));
				}
			});
			
			return comps(editor);
		}
		else //if(editorType.equals("Standard"))
		{
			final DataListEditor editor = new DataListEditor(updater.get());
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updater.updated();
				}
			});
			
			return comps(editor);
		}
	}

	@Override
	public DataList decode(String s)
	{
		if(s.isEmpty())
			return new DataList(type);
		
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
}
