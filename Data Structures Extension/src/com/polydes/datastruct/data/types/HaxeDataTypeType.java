package com.polydes.datastruct.data.types;

import javax.swing.JComponent;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.DataStructuresExtension;

public class HaxeDataTypeType extends DataType<HaxeDataType>
{
	public HaxeDataTypeType()
	{
		super(HaxeDataType.class);
	}

	@Override
	public DataEditor<HaxeDataType> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new HaxeDataTypeEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
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
	
	public static class HaxeDataTypeEditor extends DataEditor<HaxeDataType>
	{
		UpdatingCombo<HaxeDataType> typeChooser;
		
		public HaxeDataTypeEditor()
		{
			typeChooser = new UpdatingCombo<HaxeDataType>(DataStructuresExtension.get().getHaxeTypes().values(), null);
			typeChooser.setComparator((t1, t2) -> t1.getHaxeType().compareTo(t2.getHaxeType()));
			typeChooser.addActionListener(event -> updated());
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
			return new JComponent[] {typeChooser};
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