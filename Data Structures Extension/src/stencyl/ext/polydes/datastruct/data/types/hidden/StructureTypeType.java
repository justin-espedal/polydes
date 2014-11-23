package stencyl.ext.polydes.datastruct.data.types.hidden;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureTypeType extends HiddenType<StructureDefinition>
{
	public StructureTypeType()
	{
		super(StructureDefinition.class, "StructureType");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<StructureDefinition> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final UpdatingCombo<StructureDefinition> typeChooser = new UpdatingCombo<StructureDefinition>(StructureDefinitions.defMap.values(), null);
		typeChooser.setSelectedItem(updater.get());
		
		typeChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(typeChooser.getSelected());
			}
		});
		
		return comps(typeChooser);
	}

	@Override
	public StructureDefinition decode(String s)
	{
		return StructureDefinitions.defMap.get(s);
	}

	@Override
	public String toDisplayString(StructureDefinition data)
	{
		return data.name;
	}

	@Override
	public String encode(StructureDefinition data)
	{
		return data.name;
	}
	
	@Override
	public StructureDefinition copy(StructureDefinition t)
	{
		return t;
	}
}
