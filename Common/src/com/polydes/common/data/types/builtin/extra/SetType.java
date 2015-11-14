package com.polydes.common.data.types.builtin.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.core.DataSet;
import com.polydes.common.data.core.DataSetSource;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class SetType extends DataType<DataSet>
{
	public SetType()
	{
		super(DataSet.class);
	}
	
	public static final String SOURCE = "source";
	
	@Override
	public DataEditor<DataSet> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		DataSetSource source = props.get(SOURCE);
		
		if(source == null)
			return new InvalidEditor<DataSet>("Select a valid data source", style);
		
		if(source.collectionSupplier.get().isEmpty())
			return new InvalidEditor<DataSet>("The selected source has no items", style);
		
		return new ChecklistDataSetEditor(source, style);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new SetEditorBuilder();
	}

	@Override
	public DataSet decode(String s)
	{
		int typeMark = s.lastIndexOf(":");
		if(typeMark == -1)
			return new DataSet(Types._Dynamic);
		
		DataType<?> dtype = Types.get().getItem(s.substring(typeMark + 1));
		if(dtype == null)
			return new DataSet(Types._Dynamic);
		
		DataSet toReturn = new DataSet(dtype);
		
		for(String s2 : StringUtils.split(s.substring(1, typeMark - 1), ","))
			toReturn.add(dtype.decode(s2));
		
		return toReturn;
	}

	@Override
	public String encode(DataSet t)
	{
		Object[] a = t.toArray(new Object[0]);
		String s = "[";
		DataType<?> type = t.genType;
		
		for(int i = 0; i < a.length; ++i)
		{
			s += type.checkEncode(a[i]);
			
			if(i < a.length - 1)
				s += ",";
		}
		
		s += "]:" + type.getId();
		
		return s;
	}
	
	//For set, because none of the data should ever actually be modified, a shallow copy is fine.
	@Override
	public DataSet copy(DataSet t)
	{
		DataSet copySet = new DataSet(t.genType);
		Iterator<Object> it = t.iterator();
		while(it.hasNext())
			copySet.add(it.next());
		return copySet;
	}
	
	public static enum Editor
	{
		Checklist/*,
		Grid*/
	}
	
	public class SetEditorBuilder extends DataEditorBuilder
	{
		public SetEditorBuilder()
		{
			super(SetType.this, new EditorProperties());
		}

		public SetEditorBuilder source(DataSetSource source)
		{
			props.put(SOURCE, source);
			return this;
		}
	}
	
	public static class ChecklistDataSetEditor extends DataEditor<DataSet>
	{
		DataSetSource source;
		
		DataSet set;
		JPanel buttonPanel;
		IdentityHashMap<Object, JCheckBox> map;
		
		public ChecklistDataSetEditor(DataSetSource source, PropertiesSheetStyle style)
		{
			this.source = source;
			
			ArrayList<JCheckBox> buttons = new ArrayList<JCheckBox>();
			
			map = new IdentityHashMap<Object, JCheckBox>();
			
			for(final Object o : source.collectionSupplier.get())
			{
				final JCheckBox b = new JCheckBox("" + o);
				buttons.add(b);
				map.put(o, b);
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if(b.isSelected())
							set.add(o);
						else
							set.remove(o);
						updated();
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			buttonPanel = Layout.verticalBox(0, buttons.toArray(new JCheckBox[0]));
		}
		
		@Override
		public void set(DataSet t)
		{
			if(t == null)
				t = new DataSet(source.type);
			Iterator<Object> it = t.iterator();
			while(it.hasNext())
				map.get(it.next()).setSelected(true);
			set = t;
		}
		
		@Override
		public DataSet getValue()
		{
			return set;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {buttonPanel};
		}
	}
}
