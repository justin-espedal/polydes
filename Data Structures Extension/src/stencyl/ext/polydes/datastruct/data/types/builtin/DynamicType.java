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
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
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
	public DynamicType()
	{
		super(Dynamic.class, "Dynamic", "OBJECT", "Dynamic");
	}
	
	@Override
	public JComponent[] getEditor(final DataUpdater<Dynamic> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		
		if(updater.get() == null)
			updater.set(new Dynamic("", "String"));
		final Dynamic element = updater.get();
		
		final UpdatingCombo<DataType<?>> typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), e.excludeFilter);
		typeChooser.setSelectedItem(Types.fromXML(element.type));
		
		final DynamicPanel dynamicPanel = new DynamicPanel(updater);
		
		typeChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dynamicPanel.setType(typeChooser.getSelected());
			}
		});
		
		return comps(typeChooser, dynamicPanel);
	}
	
	public JComponent getVerticalEditor(final DataUpdater<Dynamic> updater, PropertiesSheetStyle style)
	{
		final Dynamic element = updater.get();
		
		final UpdatingCombo<DataType<?>> typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), arrayDynamicExclude);
		typeChooser.setSelectedItem(Types.fromXML(element.type));
		
		final DynamicPanel dynamicPanel = new DynamicPanel(updater);
		dynamicPanel.setBackground(style.pageBg.darker());
		
		typeChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dynamicPanel.setType(typeChooser.getSelected());
			}
		});
		
		DialogPanel page = new DialogPanel(style.pageBg.darker());
		page.addGenericRow("Type", typeChooser);
		page.addGenericRow("Value", dynamicPanel);
		page.finishBlock();
		
		return page;
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
	
	class DynamicPanel extends JPanel
	{
		DataType<?> type;
		Dynamic data;
		DataUpdater<?> outerUpdater;
		
		public DynamicPanel(DataUpdater<?> outerUpdater)
		{
			super(new BorderLayout());
			this.outerUpdater = outerUpdater;
			data = (Dynamic) outerUpdater.get();
			setType(Types.fromXML(data.type));
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void setType(DataType<?> type)
		{
			if(!type.equals(this.type))
			{
				this.type = type;
				data.type = type.xml;
				
				if(!type.javaType.isInstance(data.value))
					data.value = type.decode("");
				
				removeAll();
				
				final DataUpdater updater = new DataUpdater(data.value, null);
				updater.listener = new UpdateListener()
				{
					@Override
					public void updated()
					{
						data.value = updater.get();
						outerUpdater.updated();
					}
				};
				
				add(Layout.horizontalBox(type.getEditor(updater, dynamicExcludeExtras, PropertiesSheetStyle.DARK)), BorderLayout.CENTER);
				revalidate();
			}
		}
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
}