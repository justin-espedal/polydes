package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.data.core.CollectionPredicate;
import stencyl.ext.polydes.datastruct.data.core.Dynamic;
import stencyl.ext.polydes.datastruct.data.core.PredicateFactory;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;
import stencyl.sw.util.dg.DialogPanel;

public class DynamicType extends BuiltinType<Dynamic>
{
	private static final ExtrasMap noExtras = new ExtrasMap();
	
	public DynamicType()
	{
		super(Dynamic.class, "Dynamic", "OBJECT", "Dynamic");
	}
	
	@Override
	public DataEditor<Dynamic> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DynamicEditor(extras, style);
	}
	
	@Override
	public Dynamic decode(String s)
	{
		int i = s.lastIndexOf(":");
		if(i == -1)
			return new Dynamic(s, "String");
		
		String value = s.substring(0, i);
		String type = s.substring(i + 1);
		return new Dynamic(Types.fromXML(type).decode(value), type);
	}

	@Override
	public String encode(Dynamic e)
	{
		return Types.fromXML(e.type).checkEncode(e.value) + ":" + e.type;
	}

	@Override
	public String toDisplayString(Dynamic data)
	{
		return Types.fromXML(data.type).checkToDisplayString(data.value);
	}
	
	@Override
	public Dynamic copy(Dynamic t)
	{
		return new Dynamic(Types.fromXML(t.type).checkCopy(t.value), t.type);
	}
	
	public static String TYPE_LIMIT = "typeLimit";
	public static String DYNAMIC_SUB = "dynamicSubType";
	public static String DYNAMIC_ARRAY_SUB = "dynamicArraySubType";
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		String limit = extras.get(TYPE_LIMIT, "");
		if(limit.equals(DYNAMIC_SUB))
			e.excludeFilter = dynamicExclude;
		else if(limit.equals(DYNAMIC_ARRAY_SUB))
			e.excludeFilter = arrayDynamicExclude;
		else
			e.excludeFilter = noExclude;
		
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.excludeFilter == dynamicExclude)
			emap.put(TYPE_LIMIT, DYNAMIC_SUB);
		else if(e.excludeFilter == arrayDynamicExclude)
			emap.put(TYPE_LIMIT, DYNAMIC_ARRAY_SUB);
		
		return emap;
	}
	
	public static CollectionPredicate<DataType<?>> noExclude;
	public static CollectionPredicate<DataType<?>> dynamicExclude;
	public static CollectionPredicate<DataType<?>> arrayDynamicExclude;
	
	public static Extras noExcludeExtras = new Extras();
	public static Extras dynamicExcludeExtras = new Extras();
	public static Extras arrayDynamicExcludeExtras = new Extras();
	
	static
	{
		HashSet<DataType<?>> toExclude;
		
		noExclude = null;
		
		toExclude = new HashSet<DataType<?>>();
		toExclude.add(Types._Dynamic);
		dynamicExclude = PredicateFactory.isNotIn(toExclude);
		
		toExclude = new HashSet<DataType<?>>();
		toExclude.add(Types._Dynamic);
		toExclude.add(Types._Array);
		toExclude.add(Types._Selection);
		toExclude.add(Types._Set);
		arrayDynamicExclude = PredicateFactory.isNotIn(toExclude);
		
		noExcludeExtras.excludeFilter = noExclude;
		dynamicExcludeExtras.excludeFilter = dynamicExclude;
		arrayDynamicExcludeExtras.excludeFilter = arrayDynamicExclude;
	}
	
	static class Extras extends ExtraProperties
	{
		public CollectionPredicate<DataType<?>> excludeFilter;
	}
	
	@SuppressWarnings("rawtypes")
	public static class DynamicEditor extends DataEditor<Dynamic>
	{
		private UpdatingCombo<DataType<?>> typeChooser;
		private JPanel valueEditorWrapper;
		private DataEditor valueEditor;
		private PropertiesSheetStyle style;
		
		private Dynamic data;
		
		public DynamicEditor(ExtraProperties extras, PropertiesSheetStyle style)
		{
			Extras e = (Extras) extras;
			this.style = style;
			
			typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), e.excludeFilter);
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
		
		@Override
		public Dynamic getValue()
		{
			return data;
		}

		@Override
		public void set(Dynamic t)
		{
			if(t == null)
				t = new Dynamic("", "String");
			data = t;
			typeChooser.setSelectedItem(Types.fromXML(t.type));
		}
		
		@SuppressWarnings("unchecked")
		private void setType(DataType newType)
		{
			if(valueEditor == null || !newType.xml.equals(data.type))
			{
				data.type = newType.xml;
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
			return comps(typeChooser, valueEditorWrapper);
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