package stencyl.ext.polydes.paint.app.editors.text;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class DialogHighlighter implements Highlighter
{
	public static final Color TEXT_COLOR_BASE = Color.WHITE;
	public static final Color TEXT_COLOR_TAG = new Color(0x95BED8);
	public static final Color TEXT_COLOR_TAG_DATA = new Color(0x7D8C93);
	public static final Color TEXT_COLOR_COMMENT = new Color(0x5da54c);
	public static final Color TEXT_COLOR_MACRO = new Color(0x678CB1);
	
	private SimpleAttributeSet plainStyle;
	private SimpleAttributeSet tagStyle;
	private SimpleAttributeSet dataStyle;
	private SimpleAttributeSet macroStyle;
	private SimpleAttributeSet commentStyle;
	private Pattern finalPattern;

	public DialogHighlighter()
	{
		plainStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(plainStyle, TEXT_COLOR_BASE);
		
		tagStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(tagStyle, TEXT_COLOR_TAG);

		dataStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(dataStyle, TEXT_COLOR_TAG_DATA);
		
		macroStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(macroStyle, TEXT_COLOR_MACRO);
		
		commentStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(commentStyle, TEXT_COLOR_COMMENT);
		
		String noPrecedingEscape = "(?<!\\\\)";
		
		String tagOpenPattern = "\\<";
		String tagClosePattern = "\\>";
		String macroOpenPattern = "\\{";
		String macroClosePattern = "\\}";
		String commentPattern = "//";
		
		finalPattern = Pattern.compile
		(
			"(" + or(new String[] {tagOpenPattern, tagClosePattern, macroOpenPattern, macroClosePattern, commentPattern}) + ")" + noPrecedingEscape
		);
	}
	
	public String or(String[] items)
	{
		String toReturn = "";
		
		for(String item : items)
			toReturn += "|" + item;
		
		return toReturn.substring(1);
	}
	
	private int paintPosition = 0;
	private SimpleAttributeSet oldStyle = null;
	
	@Override
	public void update(StyledDocument doc, int offset, int length)
	{
		Matcher matcher = null;
		try
		{
			matcher = finalPattern.matcher(doc.getText(offset, length));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		
		boolean commented = false;
		boolean macro = false;
		int openTags = 0;
		
		if (matcher != null)
		{
			String g;
			oldStyle = plainStyle;
			paintPosition = 0;
			
			while (matcher.find())
			{
				int start = matcher.start() + offset;
				int end = matcher.end() + offset;
				
				if(commented)
					oldStyle = commentStyle;
				else if(macro)
					oldStyle = macroStyle;
				else if(openTags > 0)
					oldStyle = dataStyle;
				else
					oldStyle = plainStyle;
				
				g = matcher.group();
				
				if(!g.equals("//") && matcher.start() != 0)
				{
					try
					{
						String s = doc.getText(matcher.start() - 1, 1);
						if(s.equals("\\"))
							continue;
					}
					catch (BadLocationException e)
					{
						e.printStackTrace();
					}
				}
				
				if(commented)
				{
					commented = !g.equals("//");
					if(!commented)
						paint(doc, commentStyle, end);
				}
				else if(g.equals("//"))
				{
					commented = true;
					paint(doc, oldStyle, start);
				}
				else if(macro)
				{
					macro = !g.equals("}");
					if(!macro)
						paint(doc, macroStyle, end);
				}
				else if(g.equals("{"))
				{
					macro = true;
					paint(doc, oldStyle, start);
				}
				else if(g.equals("<"))
				{
					++openTags;
					paint(doc, oldStyle, start);
					paintWord(doc, tagStyle);
				}
				else if(g.equals(">"))
				{
					--openTags;
					paint(doc, oldStyle, start);
					paint(doc, tagStyle, end);
				}
			}
			
			if(commented)
				oldStyle = commentStyle;
			else if(macro)
				oldStyle = macroStyle;
			else if(openTags > 0)
				oldStyle = tagStyle;
			else
				oldStyle = plainStyle;
			
			paint(doc, oldStyle, doc.getLength());
		}
	}
	
	private void paint(StyledDocument doc, SimpleAttributeSet style, int toPosition)
	{
		if(paintPosition > toPosition)
			paintPosition = toPosition;
		
		doc.setCharacterAttributes(paintPosition, toPosition - paintPosition, style, false);
		
		paintPosition = toPosition;
	}
	
	private void paintWord(StyledDocument doc, SimpleAttributeSet style)
	{
		int i = paintPosition;
		String s = "";
		try
		{
			s = doc.getText(i, doc.getLength() - i);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		
		for(i = 0; i < s.length(); ++i)
		{
			if(Character.isWhitespace(s.charAt(i)) || s.charAt(i) == '>')
			{
				doc.setCharacterAttributes(paintPosition, paintPosition + i, style, false);
				
				paintPosition += i;
				
				break;
			}
		}
	}
}