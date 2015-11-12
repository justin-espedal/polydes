package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.extra.ColorType.ColorEditor;
import com.polydes.common.data.types.builtin.extra.ColorType.Extras;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;

public class ColorHaxeType extends HaxeDataType
{
	public ColorHaxeType()
	{
		super(Types._Color, "com.polydes.datastruct.Color", "COLOR");
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final ColorEditor defaultField = new ColorEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		defaultField.setOwner(StructureDefinitionsWindow.get());
		
		panel.addGenericRow(expansion, "Default", defaultField);
	}
}
