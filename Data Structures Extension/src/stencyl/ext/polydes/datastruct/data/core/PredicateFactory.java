package stencyl.ext.polydes.datastruct.data.core;

import java.util.Collection;

import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;

public class PredicateFactory
{
	public static <T> CollectionPredicate<T> isIn(final Collection<T> collection)
	{
		return new IsInPredicate<T>(collection);
	}
	
	public static <T> CollectionPredicate<T> isNotIn(final Collection<T> collection)
	{
		return new IsNotInPredicate<T>(collection);
	}
	
	public static CollectionPredicate<?> fromCondition(final StructureCondition c)
	{
		return new StructureConditionPredicate(c);
	}
	
	static class IsInPredicate<T> implements CollectionPredicate<T>
	{
		private Collection<T> collection;
		
		public IsInPredicate(Collection<T> collection)
		{
			this.collection = collection;
		}
		
		@Override
		public boolean test(T t)
		{
			return collection.contains(t);
		}
	}
	
	static class IsNotInPredicate<T> implements CollectionPredicate<T>
	{
		private Collection<T> collection;
		
		public IsNotInPredicate(Collection<T> collection)
		{
			this.collection = collection;
		}
		
		@Override
		public boolean test(T t)
		{
			return !collection.contains(t);
		}
	}
	
	static class StructureConditionPredicate implements CollectionPredicate<Object>
	{
		private StructureCondition condition;
		
		public StructureConditionPredicate(StructureCondition condition)
		{
			this.condition = condition;
		}
		
		@Override
		public boolean test(Object o)
		{
			return true;//condition.check(s, o);
		}
	}
}
