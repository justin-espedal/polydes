package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.core.Dynamic;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.DynamicType.DynamicEditor;
import com.polydes.common.data.types.builtin.basic.DynamicType.Extras;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class DynamicHaxeType extends HaxeDataType
{
	public DynamicHaxeType()
	{
		super(Types._Dynamic, "Dynamic", "OBJECT");
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<Dynamic> defaultField = new DynamicEditor(panel.style);
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