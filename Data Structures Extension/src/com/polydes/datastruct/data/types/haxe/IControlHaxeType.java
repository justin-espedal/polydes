package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.extra.IControlType.Extras;
import com.polydes.common.data.types.builtin.extra.IControlType.IControlEditor;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.core.engine.input.IControl;

public class IControlHaxeType extends HaxeDataType
{
	public IControlHaxeType()
	{
		super(Types._Control, "com.polydes.datastruct.Control", "CONTROL");
	}

	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<IControl> defaultField = new IControlEditor();
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
