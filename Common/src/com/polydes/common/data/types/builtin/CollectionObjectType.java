package com.polydes.common.data.types.builtin;

import java.util.Collection;
import java.util.function.Predicate;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class CollectionObjectType extends DataType<Object>
{
	public CollectionObjectType()
	{
		super(Object.class, "com.polydes.common.CollectionObject");
	}
	
	public static final String SOURCE = "source";
	public static final String FILTER = "filter";

	@Override
	public DataEditor<Object> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new CollectionObjectEditor(props, style);
	}

	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new CollectionObjectEditorBuilder();
	}

	@Override
	public Object decode(String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encode(Object t)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object copy(Object t)
	{
		return t;
	}
	
	public class CollectionObjectEditorBuilder extends DataEditorBuilder
	{
		public CollectionObjectEditorBuilder()
		{
			super(CollectionObjectType.this, new EditorProperties());
		}
		
		public CollectionObjectEditorBuilder source(Collection<? extends Object> list)
		{
			props.put(SOURCE, list);
			return this;
		}
		
		public CollectionObjectEditorBuilder filter(Predicate<? extends Object> filter)
		{
			props.put(FILTER, filter);
			return this;
		}
	}
	
	public class CollectionObjectEditor extends DataEditor<Object>
	{
		UpdatingCombo<Object> combo;
		
		public CollectionObjectEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			Collection<Object> list = props.get(SOURCE);
			Predicate<Object> filter = props.get(FILTER);
			combo = new UpdatingCombo<Object>(list, filter == null ? null : o -> filter.test(o));
			combo.addActionListener(event -> updated());
		}
		
		@Override
		public Object getValue()
		{
			return combo.getSelected();
		}

		@Override
		protected void set(Object t)
		{
			combo.setSelectedItem(t);
		}

		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {combo};
		}
		
		@Override
		public void dispose()
		{
			combo.dispose();
		}
	}
}
