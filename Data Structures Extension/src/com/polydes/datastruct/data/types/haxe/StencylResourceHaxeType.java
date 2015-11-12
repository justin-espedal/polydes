package com.polydes.datastruct.data.types.haxe;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.StencylResourceType;
import com.polydes.common.data.types.builtin.StencylResourceType.Extras;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.core.lib.Resource;

public class StencylResourceHaxeType<T extends Resource> extends HaxeDataType
{
	public StencylResourceType<T> srt;
	
	public StencylResourceHaxeType(StencylResourceType<T> srt, String haxeType, String stencylType)
	{
		super(srt, haxeType, stencylType);
		this.srt = srt;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras<T> e = (Extras<T>) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<T> defaultField = srt.new DropdownResourceEditor();
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