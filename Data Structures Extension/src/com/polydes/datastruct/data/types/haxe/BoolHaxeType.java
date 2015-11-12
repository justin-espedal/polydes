package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.BoolType.BooleanEditor;
import com.polydes.common.data.types.builtin.basic.BoolType.Extras;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class BoolHaxeType extends HaxeDataType
{
	public BoolHaxeType()
	{
		super(Types._Bool, "Bool", "BOOLEAN");
	}

	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<Boolean> defaultField = new BooleanEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addGenericRow(expansion, "Default", defaultField);
	}
}
