package com.polydes.common.util;

import java.util.function.Predicate;

public class PredicateUtil
{
	public static <T> Predicate<T> and(Predicate<T> t1, Predicate<T> t2)
	{
		if(t1 == null)
			return t2;
		if(t2 == null)
			return t1;
		return (t) -> t1.test(t) && t2.test(t);
	}
}
