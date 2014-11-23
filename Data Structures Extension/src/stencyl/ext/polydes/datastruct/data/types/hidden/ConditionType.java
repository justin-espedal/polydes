package stencyl.ext.polydes.datastruct.data.types.hidden;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureConditionPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class ConditionType extends HiddenType<StructureCondition>
{
	public ConditionType()
	{
		super(StructureCondition.class, "Condition");
	}

	@Override
	public JComponent[] getEditor(DataUpdater<StructureCondition> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		return getEditor(null, updater, extras, style);
	}
	
	public JComponent[] getEditor(StructureDefinition def, final DataUpdater<StructureCondition> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
//		if(def == null)
			return comps(style.createSoftLabel("ERROR: Null Definition / unimplemented"));
//		else
//			return comps(new StructureConditionPanel(def, updater));
	}

	@Override
	public StructureCondition decode(String s)
	{
		return null;
	}

	@Override
	public String toDisplayString(StructureCondition data)
	{
		return data.toString();
	}

	@Override
	public String encode(StructureCondition data)
	{
		return data.toString();
	}
	
	@Override
	public StructureCondition copy(StructureCondition t)
	{
		//TODO reimplement copy
		return null;//t.copy();
	}
}
