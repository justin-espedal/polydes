package stencyl.ext.polydes.datastruct.data.structure.cond;

import stencyl.ext.polydes.datastruct.data.structure.StructureField;

public interface StructureConditionVerifier
{
	public boolean verify(StructureField field, String value);
}
