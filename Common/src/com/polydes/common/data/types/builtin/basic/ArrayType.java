package com.polydes.common.data.types.builtin.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.comp.datalist.DataListEditor;
import com.polydes.common.comp.utils.DocumentAdapter;
import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class ArrayType extends DataType<DataList>
{
	public ArrayType()
	{
		super(DataList.class);
	}
	
	public static final String GEN_TYPE = "genType";
	public static final String GEN_TYPE_PROPS = "genTypeProps";
	
	@Override
	public DataEditor<DataList> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		switch(props.<Editor>get(EDITOR))
		{
			case Simple:
				return new SimpleArrayEditor(style, props);
			default:
				return new StandardArrayEditor(style, props);	
		}	
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new ArrayEditorBuilder();
	}

	@Override
	public DataList decode(String s)
	{
		if(s.isEmpty())
			return null;
		
		int i = s.lastIndexOf(":");
		String typename = s.substring(i + 1);
		DataType<?> genType = Types.get().getItem(typename);
		DataList list = new DataList(genType);
		
		for(String s2 : getEmbeddedArrayStrings(s))
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
		s += "]:" + array.genType.getId();
		
		return s;
	}
	
	public static ArrayList<String> getEmbeddedArrayStrings(String s)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		int i = s.lastIndexOf(":");
		
		char ch;
		int k = 0;
		ArrayList<Integer> commas = new ArrayList<Integer>();
		for(int j = 1; j < i; ++j)
		{
			ch = s.charAt(j);
			if(ch == '[')
				++k;
			else if(ch == ']')
				--k;
			else if(ch == ',' && k == 0)
				commas.add(j);
		}
		
		int lastComma = 0;
		for(int comma : commas)
		{
			a.add(s.substring(lastComma + 1, comma));
			lastComma = comma;
		}
		a.add(s.substring(lastComma + 1, i - 1));
		
		return a;
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
	
	public static enum Editor
	{
		Standard,
		Simple;
	}
	
	public class ArrayEditorBuilder extends DataEditorBuilder
	{
		public ArrayEditorBuilder()
		{
			super(ArrayType.this, new EditorProperties(){{
				put(EDITOR, Editor.Standard);
				put(GEN_TYPE, Types._String);
			}});
		}

		public ArrayEditorBuilder simpleEditor()
		{
			props.put(EDITOR, Editor.Simple);
			return this;
		}
		
		public ArrayEditorBuilder genType(DataType<?> dtype)
		{
			props.put(GEN_TYPE, dtype);
			return this;
		}
		
		public ArrayEditorBuilder genTypeProps(EditorProperties genTypeProps)
		{
			props.put(GEN_TYPE_PROPS, genTypeProps);
			return this;
		}
	}
	
	public static class SimpleArrayEditor extends DataEditor<DataList>
	{
		JTextArea editor;
		DataType<?> genType;
		
		DataList list;
		
		public SimpleArrayEditor(PropertiesSheetStyle style, EditorProperties props)
		{
			genType = props.get(GEN_TYPE);
			
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
					list = DataList.fromStrings(StringUtils.split(editor.getText(), "\n"), list.genType);
					updated();
				}
			});
		}
		
		public void setType(DataType<?> type)
		{
			if(!genType.equals(type))
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
			editor.setText(StringUtils.join(DataList.toStrings(t), "\n"));
		}
		
		@Override
		public DataList getValue()
		{
			return list;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
	}
	
	public static class StandardArrayEditor extends DataEditor<DataList>
	{
		DataListEditor editor;
		DataType<?> genType;
		EditorProperties genTypeProps;
		
		public StandardArrayEditor(PropertiesSheetStyle style, EditorProperties props)
		{
			genType = props.get(GEN_TYPE);
			genTypeProps = props.get(GEN_TYPE_PROPS);
			
			editor = new DataListEditor(null, genTypeProps, style);
			
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
			editor.setData(t, genTypeProps);
		}
		
		@Override
		public DataList getValue()
		{
			return editor.getModel();
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
			editor.dispose();
			editor = null;
		}
	}
}
