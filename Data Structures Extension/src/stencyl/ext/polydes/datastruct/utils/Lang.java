package stencyl.ext.polydes.datastruct.utils;

public class Lang
{
	public static final <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue : item;
	}
}
