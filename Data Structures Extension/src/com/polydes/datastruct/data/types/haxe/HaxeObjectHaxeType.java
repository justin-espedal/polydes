package com.polydes.datastruct.data.types.haxe;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.core.HaxeObject;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeObjectType;
import com.polydes.datastruct.data.types.HaxeObjectType.Extras;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class HaxeObjectHaxeType extends HaxeDataType
{
	HaxeObjectType type;
	
	public HaxeObjectHaxeType(HaxeObjectType type)
	{
		super(type, type.getId(), "OBJECT");
		this.type = type;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<HaxeObject> defaultField = type.new HaxeObjectEditor(panel.style);
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
	
	@Override
	public List<String> generateHaxeReader()
	{
		if(type.getDef().haxereaderExpression != null)
		{
			return Lang.arraylist(String.format("StringData.registerReader(\"%s\", function(s) return %s);", getHaxeType(), type.getDef().haxereaderExpression));
		}
		else
		{
			String[] types = Lang.map(type.getDef().fields, String.class, (field) -> "\"" + field.type.getHaxeType() + "\"");
			return Lang.arraylist(String.format("StringData.registerHaxeObjectReader(\"%s\", %s, [%s]);", getHaxeType(), getHaxeType(), StringUtils.join(types, ",")));
		}
	}
}
