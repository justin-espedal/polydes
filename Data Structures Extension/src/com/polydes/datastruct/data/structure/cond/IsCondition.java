package com.polydes.datastruct.data.structure.cond;

import com.polydes.datastruct.data.structure.StructureField;

public class IsCondition extends SubCondition
{
	public StructureField field;
	public String value;
	
	public IsCondition(StructureField field, String value)
	{
		this.field = field;
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		if(field == null)
			return "null is " + value;
		else
			return field.getLabel() + " is " + value;
	}

	@Override
	public boolean check(StructureConditionVerifier v)
	{
		if(field == null)
			return true;
		
		return v.verify(field, value);
	}
	
	@Override
	public SubCondition copy()
	{
		return new IsCondition(field, value);
	}
}