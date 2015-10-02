package com.polydes.datastruct.grammar;

public class SyntaxException extends Exception
{
	public SyntaxException(Exception ex)
	{
		super(ex);
	}

	public SyntaxException(String string)
	{
		super(string);
	}
}