package com.polydes.datastruct.data.structure;

import com.polydes.common.ui.object.EditableObject;

/** StructureDefinitionElement **/
public abstract class SDE extends EditableObject
{
	public abstract String getDisplayLabel();
	
	@Override
	public boolean fillsViewHorizontally()
	{
		return false;
	}
}
