package stencyl.ext.polydes.datastruct.data.structure.cond;

public abstract class SubCondition
{
	public abstract boolean check(StructureConditionVerifier v);
	public abstract SubCondition copy();
}