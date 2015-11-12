package com.polydes.datastruct.data.types;

import static com.polydes.common.util.Lang.array;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.DataStructuresExtension;

public class HaxeDataTypeType extends DataType<HaxeDataType>
{
	public HaxeDataTypeType()
	{
		super(HaxeDataType.class);
	}

	@Override
	public DataEditor<HaxeDataType> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new HaxeDataTypeEditor();
	}

	@Override
	public HaxeDataType decode(String s)
	{
		return DataStructuresExtension.get().getHaxeTypes().getItem(s);
	}

	@Override
	public String toDisplayString(HaxeDataType data)
	{
		return data.getHaxeType();
	}

	@Override
	public String encode(HaxeDataType data)
	{
		return data.getHaxeType();
	}
	
	@Override
	public HaxeDataType copy(HaxeDataType t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
	
	public static class Extras extends ExtraProperties
	{
	}
	
	public static class HaxeDataTypeEditor extends DataEditor<HaxeDataType>
	{
		UpdatingCombo<HaxeDataType> typeChooser;
		
		public HaxeDataTypeEditor()
		{
			typeChooser = new UpdatingCombo<HaxeDataType>(DataStructuresExtension.get().getHaxeTypes().values(), null);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(HaxeDataType t)
		{
			typeChooser.setSelectedItem(t);
		}
		
		@Override
		public HaxeDataType getValue()
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