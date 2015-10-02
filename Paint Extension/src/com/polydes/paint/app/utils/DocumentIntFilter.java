package com.polydes.paint.app.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;

public class DocumentIntFilter extends DocumentFilter
{
	private int min;
	private int max;
	
	public DocumentIntFilter()
	{
		this(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public DocumentIntFilter(int min, int max)
	{
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
	{
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.insert(offset, string);
		
		if (test(sb.toString()))
			fb.replace(0, doc.getLength(), getInput(sb.toString()), attr);
	}

	private boolean test(String text)
	{
		if(text.isEmpty())
			return true;
		
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	private String getInput(String text)
	{
		if(text.isEmpty())
			text = "0";
		
		try
		{
			int t = Integer.parseInt(text);
			
			if(t < min)
				t = min;
			else if(t > max)
				t = max;
			
			return "" + t;
		}
		catch (NumberFormatException e)
		{
			return "";
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException
	{
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		if (test(sb.toString()))
			fb.replace(0, doc.getLength(), getInput(sb.toString()), attr);
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)	throws BadLocationException
	{
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);

		if (test(sb.toString()))
			fb.replace(0, doc.getLength(), getInput(sb.toString()), new SimpleAttributeSet());
	}
}
