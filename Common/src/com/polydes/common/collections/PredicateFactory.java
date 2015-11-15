package com.polydes.common.collections;

import java.util.Collection;
import java.util.function.Predicate;

public class PredicateFactory
{
	public static <T> Predicate<T> isIn(final Collection<T> collection)
	{
		return new IsInPredicate<T>(collection);
	}
	
	public static <T> Predicate<T> isNotIn(final Collection<T> collection)
	{
		return new IsNotInPredicate<T>(collection);
	}
	
	static class IsInPredicate<T> implements Predicate<T>
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
	
	static class IsNotInPredicate<T> implements Predicate<T>
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
}
