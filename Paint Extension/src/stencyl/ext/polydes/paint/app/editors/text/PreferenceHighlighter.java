package stencyl.ext.polydes.paint.app.editors.text;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;



public class PreferenceHighlighter implements Highlighter
{
	public static final Color TEXT_COLOR_COMMENT = new Color(0x5da54c);
	public static final Color TEXT_COLOR_KEY = new Color(0x95BED8);
	public static final Color TEXT_COLOR_VALUE = new Color(0x7D8C93);
	
	private SimpleAttributeSet commentStyle;
	private SimpleAttributeSet keyStyle;
	private SimpleAttributeSet valueStyle;
	private Pattern commentPattern;
	private Pattern keyValuePattern;

	public PreferenceHighlighter()
	{
		commentStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(commentStyle, TEXT_COLOR_COMMENT);
		
		keyStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(keyStyle, TEXT_COLOR_KEY);

		valueStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(valueStyle, TEXT_COLOR_VALUE);
		
		commentPattern = Pattern.compile
		(
			"^(#.*)$", Pattern.MULTILINE
		);
		keyValuePattern = Pattern.compile
		(
			"^([^#][^=]*)=?(.*)$", Pattern.MULTILINE
		);
	}
	
	@Override
	public void update(StyledDocument doc, int offset, int length)
	{
		Matcher matcher = null;
		
		try
		{
			matcher = keyValuePattern.matcher(doc.getText(offset, length));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		
		if (matcher != null)
		{
			while (matcher.find())
			{
				paint(doc, keyStyle, matcher.start(1), matcher.start(2));
				paint(doc, valueStyle, matcher.start(2), matcher.end(2));
			}
		}
		
		try
		{
			matcher = commentPattern.matcher(doc.getText(offset, length));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		
		if (matcher != null)
		{
			while (matcher.find())
			{
				int start = matcher.start(1) + offset;
				int end = matcher.end(1) + offset;
				
				paint(doc, commentStyle, start, end);
			}
		}
	}
	
	private void paint(StyledDocument doc, SimpleAttributeSet style, int fromPosition, int toPosition)
	{
		doc.setCharacterAttributes(fromPosition, toPosition - fromPosition, style, false);
	}
}
