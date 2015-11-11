package com.polydes.common.data.types.hidden;

import static com.polydes.common.util.Lang.array;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComponent;

import com.polydes.common.collections.CollectionPredicate;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.Lang;

@SuppressWarnings("rawtypes")
public class DataTypeType extends DataType<DataType>
{
	public DataTypeType()
	{
		super(DataType.class);
	}

	@Override
	public DataEditor<DataType> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DataTypeEditor((Extras) extras);
	}

	@Override
	public DataType decode(String s)
	{
		return Types.get().getItem(s);
	}

	@Override
	public String toDisplayString(DataType data)
	{
		return data.getId();
	}

	@Override
	public String encode(DataType data)
	{
		return data.getId();
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
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
	
	public static HashSet<DataType<?>> noExclude = Lang.hashset();
	public static HashSet<DataType<?>> dynamicExclude = Lang.hashset(Types._Dynamic);
	public static HashSet<DataType<?>> arrayDynamicExclude =
			Lang.hashset(Types._Dynamic, Types._Array, Types._Selection, Types._Set);
	
	public static class Extras extends ExtraProperties
	{
		public HashSet<DataType<?>> excludedTypes;
	}
	
	public static class DataTypeEditor extends DataEditor<DataType>
	{
		UpdatingCombo<DataType<?>> typeChooser;
		
		public DataTypeEditor()
		{
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), null);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		public DataTypeEditor(CollectionPredicate<DataType<?>> filter)
		{
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), filter);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		public DataTypeEditor(final Extras e)
		{
			CollectionPredicate<DataType<?>> filter = new CollectionPredicate<DataType<?>>()
			{
				@Override
				public boolean test(DataType<?> t)
				{
					return !e.excludedTypes.contains(t);
				};
			};
			
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), filter);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
			
			typeChooser.setMaximumRowCount(18);
		}
		
		public void setFilter(CollectionPredicate<DataType<?>> filter)
		{
			typeChooser.setFilter(filter);
		}
		
		@Override
		public void set(DataType t)
		{
			typeChooser.setSelectedItem(t);
		}
		
		@Override
		public DataType getValue()
		{
			return typeChooser.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(typeChooser);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			typeChooser.dispose();
			typeChooser = null;
		}
	}
}