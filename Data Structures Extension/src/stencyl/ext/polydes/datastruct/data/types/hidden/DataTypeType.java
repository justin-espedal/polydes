package stencyl.ext.polydes.datastruct.data.types.hidden;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComponent;

import stencyl.core.lib.Resource;
import stencyl.ext.polydes.common.collections.CollectionPredicate;
import stencyl.ext.polydes.common.util.Lang;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
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
	public DataEditor<DataType> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DataTypeEditor((Extras) extras);
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
	
	public static HashSet<DataType<?>> noExclude = Lang.hashset();
	public static HashSet<DataType<?>> dynamicExclude = Lang.hashset(Types._Dynamic);
	public static HashSet<DataType<?>> arrayDynamicExclude =
			Lang.hashset(Types._Dynamic, Types._Array, Types._Selection, Types._Set);
	
	public static CollectionPredicate<DataType<?>> onlyStructureDefinitions = new CollectionPredicate<DataType<?>>()
	{
		@Override
		public boolean test(DataType<?> t)
		{
			return t.javaType.isAssignableFrom(Structure.class);
		}
	};
	
	public static CollectionPredicate<DataType<?>> onlyStencylTypes = new CollectionPredicate<DataType<?>>()
	{
		@Override
		public boolean test(DataType<?> t)
		{
			return Resource.class.isAssignableFrom(t.javaType);
		}
	};
	
	public static CollectionPredicate<DataType<?>> dynamicArraySubTypes = new CollectionPredicate<DataType<?>>()
	{
		@Override
		public boolean test(DataType<?> t)
		{
			return !arrayDynamicExclude.contains(t);
		}
	};
	
	public static class Extras extends ExtraProperties
	{
		public HashSet<DataType<?>> excludedTypes;
	}
	
	public static class DataTypeEditor extends DataEditor<DataType>
	{
		UpdatingCombo<DataType<?>> typeChooser;
		
		public DataTypeEditor()
		{
			typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), null);
			
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
			typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), filter);
			
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
			
			typeChooser = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), filter);
			
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
			return comps(typeChooser);
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