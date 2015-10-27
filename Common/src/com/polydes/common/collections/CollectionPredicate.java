package com.polydes.common.collections;

public interface CollectionPredicate<T>
{
	public boolean test(T t);
	
	public static <T> CollectionPredicate<T> and(CollectionPredicate<T> t1, CollectionPredicate<T> t2)
	{
		if(t1 == null)
			return t2;
		if(t2 == null)
			return t1;
		return (t) -> t1.test(t) && t2.test(t);
	}
}