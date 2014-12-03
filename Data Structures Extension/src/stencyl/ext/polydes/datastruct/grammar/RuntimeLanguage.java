package stencyl.ext.polydes.datastruct.grammar;

import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class RuntimeLanguage
{
	public static Object invoke(Object o, String name, List<Object> args)
	{
		try
		{
			return MethodUtils.invokeMethod(o, name, args.toArray());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;//return null;//throw new SyntaxException(e);
		}
	}
	
	public static Object field(Object o, String name)
	{
		try
		{
			return FieldUtils.readDeclaredField(o, name, true);
		}
		catch (Exception e)
		{
			return null;//throw new SyntaxException(e);
		}
	}
	
	public static Number negate(Object o)
	{
		if(isFloat(o))
			return - ((Float) o);
		if(isInteger(o))
			return - ((Integer) o);
		
		return null;//throw new SyntaxException("Object is not a number: " + o);
	}
	
	public static Boolean not(Object o)
	{
		if(isBoolean(o))
			return ! ((Boolean) o);
		
		return null;//throw new SyntaxException("Object is not a boolean: " + o);
	}
	
	public static Number multiply(Object l, Object r)
	{
		if(!bothNumber(l, r))
			return null;//throw new SyntaxException("Objects not numbers: " + l + ", " + r);
		
		return (isFloat(l) ? (Float) l : (Integer) l) * (isFloat(r) ? (Float) r : (Integer) r);
	}
	
	public static Number divide(Object l, Object r)
	{
		if(!bothNumber(l, r))
			return null;//throw new SyntaxException("Objects not numbers: " + l + ", " + r);
		
		return (isFloat(l) ? (Float) l : (Integer) l) / (isFloat(r) ? (Float) r : (Integer) r);
	}
	
	public static Number mod(Object l, Object r)
	{
		if(!bothNumber(l, r))
			return null;//throw new SyntaxException("Objects not numbers: " + l + ", " + r);
		
		return (isFloat(l) ? (Float) l : (Integer) l) % (isFloat(r) ? (Float) r : (Integer) r);
	}
	
	public static Object add(Object l, Object r)
	{
		if(eitherString(l, r))
			return "" + l + r;
		
		if(!bothNumber(l, r))
			return null;//throw new SyntaxException("Objects not numbers or strings: " + l + ", " + r);
		
		return (isFloat(l) ? (Float) l : (Integer) l) + (isFloat(r) ? (Float) r : (Integer) r);
	}
	
	public static Number sub(Object l, Object r)
	{
		if(!bothNumber(l, r))
			return null;//throw new SyntaxException("Objects not numbers: " + l + ", " + r);
		
		return (isFloat(l) ? (Float) l : (Integer) l) - (isFloat(r) ? (Float) r : (Integer) r);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Object l, Object r)
	{
		try
		{
			return ((Comparable) l).compareTo((Comparable) r);
		}
		catch(Exception e)
		{
			return 0;//throw new SyntaxException(e);
		}
	}
	
	public static Boolean le(Object l, Object r)
	{
		return compare(l, r) <= 0;
	}
	
	public static Boolean ge(Object l, Object r)
	{
		return compare(l, r) >= 0;
	}
	
	public static Boolean lt(Object l, Object r)
	{
		return compare(l, r) < 0;
	}
	
	public static Boolean gt(Object l, Object r)
	{
		return compare(l, r) > 0;
	}
	
	public static Boolean equals(Object l, Object r)
	{
		if(l == null && r == null)
			return true;
		if((l == null) != (r == null))
			return false;
		return l == r || l.equals(r);		
	}
	
	public static Boolean and(Object l, Object r)
	{
		if(!bothBoolean(l, r))
			return null;//throw new SyntaxException("Objects not booleans: " + l + ", " + r);
		
		return (Boolean) l && (Boolean) r;
	}
	
	public static Boolean or(Object l, Object r)
	{
		if(!bothBoolean(l, r))
			return null;//throw new SyntaxException("Objects not booleans: " + l + ", " + r);
		
		return (Boolean) l || (Boolean) r;
	}
	
	private static boolean isNumber(Object o)
	{
		return isFloat(o) || isInteger(o);
	}
	
	private static boolean isFloat(Object o)
	{
		return o instanceof Float;
	}
	
	private static boolean isInteger(Object o)
	{
		return o instanceof Integer;
	}
	
	private static boolean isString(Object o)
	{
		return o instanceof String;
	}
	
	private static boolean isBoolean(Object o)
	{
		return o instanceof Boolean;
	}
	
	private static boolean eitherString(Object l, Object r)
	{
		return isString(l) || isString(r);
	}
	
	private static boolean bothNumber(Object l, Object r)
	{
		return isNumber(l) && isNumber(r);
	}
	
	private static boolean bothBoolean(Object l, Object r)
	{
		return isBoolean(l) && isBoolean(r);
	}
}
