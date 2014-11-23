package stencyl.ext.polydes.datastruct.data.types.hidden;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.core.CollectionPredicate;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

@SuppressWarnings("rawtypes")
public class DataTypeType extends HiddenType<DataType>
{
	public DataTypeType()
	{
		super(DataType.class, "Type");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<DataType> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final Extras e = (Extras) extras;
		CollectionPredicate<DataType<?>> filter = new CollectionPredicate<DataType<?>>()
		{
			public boolean test(DataType<?> t)
			{
				return !e.excludedTypes.contains(t);
			};
		};
		
		final UpdatingCombo<DataType<?>> typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), filter);
		
		typeChooser.setSelectedItem(updater.get());
		
		typeChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(typeChooser.getSelected());
			}
		});
		
		return comps(typeChooser);
	}

	@Override
	public DataType decode(String s)
	{
		return Types.fromXML(s);
	}

	@Override
	public String toDisplayString(DataType data)
	{
		return data.xml;
	}

	@Override
	public String encode(DataType data)
	{
		return data.xml;
	}
	
	@Override
	public DataType copy(DataType t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		String limit = extras.get("typeLimit", "");
		if(limit.equals("DynamicSubType"))
			e.excludedTypes = dynamicExclude;
		else if(limit.equals("DynamicArraySubType"))
			e.excludedTypes = arrayDynamicExclude;
		else
			e.excludedTypes = noExclude;
		
		return e;
	}
	
	public static HashSet<DataType<?>> noExclude = new HashSet<DataType<?>>();
	public static HashSet<DataType<?>> dynamicExclude = new HashSet<DataType<?>>();
	public static HashSet<DataType<?>> arrayDynamicExclude = new HashSet<DataType<?>>();
	static
	{
		dynamicExclude.add(Types._Dynamic);
		arrayDynamicExclude.add(Types._Dynamic);
		arrayDynamicExclude.add(Types._Array);
		arrayDynamicExclude.add(Types._Selection);
		arrayDynamicExclude.add(Types._Set);
	}
	
	class Extras extends ExtraProperties
	{
		public HashSet<DataType<?>> excludedTypes;
	}
}
