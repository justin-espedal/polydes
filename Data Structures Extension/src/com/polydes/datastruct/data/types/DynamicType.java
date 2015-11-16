package com.polydes.datastruct.data.types;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.core.Dynamic;
import com.polydes.datastruct.data.types.HaxeDataTypeType.HaxeDataTypeEditor;

import stencyl.sw.util.dg.DialogPanel;

public class DynamicType extends DataType<Dynamic>
{
	private static final EditorProperties noProps = new EditorProperties()
	{
		@Override
		public Object put(String key, Object value)
		{
			throw new RuntimeException();
		};
	};
	
	public DynamicType()
	{
		super(Dynamic.class);
	}
	
	@Override
	public DataEditor<Dynamic> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new DynamicEditor(style);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DynamicEditorBuilder();
	}
	
	@Override
	public Dynamic decode(String s)
	{
		int i = s.lastIndexOf(":");
		if(i == -1)
			return new Dynamic(s, HaxeTypes._String);
		
		String value = s.substring(0, i);
		String type = s.substring(i + 1);
		HaxeDataType htype = DataStructuresExtension.get().getHaxeTypes().getItem(type);
		return new Dynamic(HaxeTypeConverter.decode(htype.dataType, value), htype);
	}

	@Override
	public String encode(Dynamic e)
	{
		return HaxeTypeConverter.encode(e.type.dataType, e.value) + ":" + e.type.getHaxeType();
	}

	@Override
	public String toDisplayString(Dynamic data)
	{
		return data.type.dataType.checkToDisplayString(data.value);
	}
	
	@Override
	public Dynamic copy(Dynamic t)
	{
		return new Dynamic(t.type.dataType.checkCopy(t.value), t.type);
	}
	
	public class DynamicEditorBuilder extends DataEditorBuilder
	{
		public DynamicEditorBuilder()
		{
			super(DynamicType.this, new EditorProperties());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class DynamicEditor extends DataEditor<Dynamic>
	{
		private final HaxeDataTypeEditor typeChooser;
		private final JPanel valueEditorWrapper;
		private final JComponent[] comps;
		
		private DataEditor valueEditor;
		private PropertiesSheetStyle style;
		
		private Dynamic data;
		
		public DynamicEditor(PropertiesSheetStyle style)
		{
			this.style = style;
			
			typeChooser = new HaxeDataTypeEditor();
			valueEditorWrapper = new JPanel();
			valueEditorWrapper.setBackground(null);
			
			typeChooser.addListener(() -> {
				setType(typeChooser.getValue());
				updated();
			});
			
			JComponent[] typeChooserComps = typeChooser.getComponents();
			comps = ArrayUtils.add(typeChooserComps, valueEditorWrapper);
		}
		
//		public void excludeTypes(final HashSet<DataType<?>> types)
//		{
//			typeChooser.setFilter(new Predicate<DataType<?>>()
//			{
//				@Override
//				public boolean test(DataType<?> t)
//				{
//					return !types.contains(t);
//				}
//			});
//		}
		
		@Override
		public Dynamic getValue()
		{
			return data;
		}

		@Override
		public void set(Dynamic t)
		{
			if(t == null)
				t = new Dynamic("", HaxeTypes._String);
			data = t;
			typeChooser.setValue(t.type);
		}
		
		@SuppressWarnings("unchecked")
		private void setType(HaxeDataType newType)
		{
			if(valueEditor == null || !newType.equals(data.type))
			{
				data.type = newType;
				if(!newType.dataType.javaType.isInstance(data.value))
					data.value = newType.dataType.decode("");
				
				valueEditorWrapper.removeAll();
				
				if(valueEditor != null)
					valueEditor.dispose();
				
				JComponent editor = null;
				
				valueEditor = newType.dataType.createEditor(noProps, PropertiesSheetStyle.DARK);
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
			return comps;
		}
		
		public DialogPanel createMiniPage()
		{
			DialogPanel page = new DialogPanel(style.pageBg.darker());
			page.addGenericRow("Type", Layout.horizontalBox(typeChooser.getComponents()));
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
			valueEditor = null;
			style = null;
		}
	}
}