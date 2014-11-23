package stencyl.ext.polydes.datastruct.data.types.builtin;

import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.Main;
import stencyl.ext.polydes.datastruct.data.core.DataList2D;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.utils.StringData;

public class Array2DType extends BuiltinType<DataList2D>
{
	private DynamicType type = new DynamicType();
	
	public Array2DType()
	{
		super(DataList2D.class, "Array<Array<Dynamic>>", "LIST", "2DArray");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<DataList2D> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		return null;
		/*final DataType<?> genType = getOptional(field, "genType", type);
		
		if(updater.get() == null || !updater.get().genType.xml.equals(genType.xml))
			updater.set(new DataList2D(genType));
		
		final DataList2DEditor editor = new DataList2DEditor(updater.get());
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.updated();
			}
		});
		
		return comps(editor);*/
	}

	@Override
	public DataList2D decode(String s)
	{
		if(s.isEmpty())
			return new DataList2D(type);
		
		//backwards compatibility
		if(!s.startsWith("["))
		{
			Main.forceUpdateData = true;
			
			String[] strings = StringUtils.split(s, ",");
			
			DataType<?> genType;
			DataList2D list;
			
			if(strings[0].indexOf(":") == -1)
			{
				genType = Types.fromXML("String");
				list = new DataList2D(genType);
				for(String s2 : strings)
					;//list.add(s2);
			}
			else
			{
				genType = Types.fromXML(strings[0].split(":")[1]);
				list = new DataList2D(genType);
				for(String s2 : strings)
					;//list.add(genType.decode(s2.split(":")[0]));
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
		DataList2D list = new DataList2D(genType);
		
		for(String s2 : StringData.getEmbeddedArrayStrings(s))
			;//list.add(genType.decode(s2));
		
		return list;
	}
	
	@Override
	public String encode(DataList2D array)
	{
		String s = "[";
		
		for(int i = 0; i < array.size(); ++i)
			s += array.genType.checkEncode(array.get(i)) + (i < array.size() - 1 ? "," : "");
		s += "]:" + array.genType.xml;
		
		return s;
	}
	
	@Override
	public String toDisplayString(DataList2D data)
	{
		return null;
	}

	@Override
	public DataList2D copy(DataList2D t)
	{
		DataList2D copyList = new DataList2D(t.genType);
		
		for(Object o : t)
			;//copyList.add(t.genType.checkCopy(o));
		
		return copyList;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return null;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
}
