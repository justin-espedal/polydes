package stencyl.ext.polydes.datastruct.data.structure.cond;

import stencyl.ext.polydes.datastruct.data.structure.StructureField;

public class NotCondition extends SubCondition
{
	public SubCondition c;
	
	public NotCondition(SubCondition c)
	{
		this.c = c;
	}
	
	@Override
	public String toString()
	{
		if(c instanceof IsCondition)
		{
			IsCondition sub = (IsCondition) c;
			StructureField f = sub.field;
			return (f == null ? "null" : sub.field.getLabel()) + " isn't " + sub.value;
		}
		else
			return "not " + c;
	}

	@Override
	public boolean check(StructureConditionVerifier v)
	{
		if(c == null)
			return true;
		
		return !c.check(v); 
	}
	
	@Override
	public SubCondition copy()
	{
		return new NotCondition(c == null ? null : c.copy());
	}
}