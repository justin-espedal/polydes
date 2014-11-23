package stencyl.ext.polydes.datastruct.data.structure.cond;

public class OrCondition extends SubCondition
{
	public SubCondition c1;
	public SubCondition c2;
	
	public OrCondition(SubCondition c1, SubCondition c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		
		if(c1 instanceof AndCondition || c1 instanceof OrCondition)
			s += "( " + c1 + " )";
		else
			s += c1;
		s += " or ";
		if(c2 instanceof AndCondition || c2 instanceof OrCondition)
			s += "( " + c2 + " )";
		else
			s += c2;
		
		return s;
	}

	@Override
	public boolean check(StructureConditionVerifier v)
	{
		if(c1 == null || c2 == null)
			return true;
		
		return c1.check(v) || c2.check(v);
	}
	
	@Override
	public SubCondition copy()
	{
		return new OrCondition(c1 == null ? null : c1.copy(), c2 == null ? null: c2.copy());
	}
}