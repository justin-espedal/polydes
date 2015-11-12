package com.polydes.common.data.types.builtin.basic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.polydes.common.collections.CollectionPredicate;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.core.Dynamic;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.dg.DialogPanel;

public class DynamicType extends DataType<Dynamic>
{
	private static final ExtrasMap noExtras = new ExtrasMap();
	
	public DynamicType()
	{
		super(Dynamic.class);
	}
	
	@Override
	public DataEditor<Dynamic> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DynamicEditor(style);
	}
	
	@Override
	public Dynamic decode(String s)
	{
		int i = s.lastIndexOf(":");
		if(i == -1)
			return new Dynamic(s, Types._String);
		
		String value = s.substring(0, i);
		String type = s.substring(i + 1);
		DataType<?> dtype = Types.get().getItem(type);
		return new Dynamic(dtype.decode(value), dtype);
	}

	@Override
	public String encode(Dynamic e)
	{
		return e.type.checkEncode(e.value) + ":" + e.type.getId();
	}

	@Override
	public String toDisplayString(Dynamic data)
	{
		return data.type.checkToDisplayString(data.value);
	}
	
	@Override
	public Dynamic copy(Dynamic t)
	{
		return new Dynamic(t.type.checkCopy(t.value), t.type);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Dynamic, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Dynamic defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class DynamicEditor extends DataEditor<Dynamic>
	{
		private UpdatingCombo<DataType<?>> typeChooser;
		private JPanel valueEditorWrapper;
		private DataEditor valueEditor;
		private PropertiesSheetStyle style;
		
		private Dynamic data;
		
		public DynamicEditor(PropertiesSheetStyle style)
		{
			this.style = style;
			
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), null);
			valueEditorWrapper = new JPanel();
			valueEditorWrapper.setBackground(null);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setType(typeChooser.getSelected());
					updated();
				}
			});
		}
		
		public void excludeTypes(final HashSet<DataType<?>> types)
		{
			typeChooser.setFilter(new CollectionPredicate<DataType<?>>()
			{
				@Override
				public boolean test(DataType<?> t)
				{
					return !types.contains(t);
				}
			});
		}
		
		@Override
		public Dynamic getValue()
		{
			return data;
		}

		@Override
		public void set(Dynamic t)
		{
			if(t == null)
				t = new Dynamic("", Types._String);
			data = t;
			typeChooser.setSelectedItem(t.type);
		}
		
		@SuppressWarnings("unchecked")
		private void setType(DataType newType)
		{
			if(valueEditor == null || !newType.equals(data.type))
			{
				data.type = newType;
				if(!newType.javaType.isInstance(data.value))
					data.value = newType.decode("");
				
				valueEditorWrapper.removeAll();
				
				if(valueEditor != null)
					valueEditor.dispose();
				
				JComponent editor = null;
				
				valueEditor = newType.createEditor(noExtras, PropertiesSheetStyle.DARK);
				valueEditor.setValue(data.value);
				valueEditor.addListener(new UpdateListener()
				{
					@Override
					public void updated()
					{
						data.value = valueEditor.getValue();
						DynamicEditor.this.updated();
					}
				});
				
				editor = Layout.horizontalBox(style.fieldDimension, valueEditor.getComponents());
				
				valueEditorWrapper.add(editor, BorderLayout.CENTER);
				valueEditorWrapper.revalidate();
			}
		}

		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {typeChooser, valueEditorWrapper};
		}
		
		public DialogPanel createMiniPage()
		{
			DialogPanel page = new DialogPanel(style.pageBg.darker());
			page.addGenericRow("Type", typeChooser);
			page.addGenericRow("Value", valueEditorWrapper);
			page.finishBlock();
			
			return page;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			typeChooser.dispose();
			valueEditorWrapper.removeAll();
			if(valueEditor != null)
				valueEditor.dispose();
			
			data = null;
			typeChooser = null;
			valueEditorWrapper = null;
			valueEditor = null;
			style = null;
		}
	}
}