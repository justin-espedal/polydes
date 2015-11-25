package com.polydes.dialog.app.editors.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.nodes.Leaf;
import com.polydes.dialog.data.TextSource;

public class TextArea extends TitledPanel implements PropertyChangeListener
{
	public static final Color TEXT_EDITOR_COLOR = new Color(43, 43, 43);
	public static final Color TEXT_COLOR_BASE = Color.WHITE;
	
	private JTextPane textPane;

	private StyledDocument doc;
	private SimpleAttributeSet plain;
	private SimpleAttributeSet cjk;
	private Pattern nonASCII;
	
	private Highlighter highlighter;
	
	private TextSource source;
	
	public TextArea(TextSource source, Highlighter highlighter)
	{
		super(source.getName(), null);
		label.setBackground(TEXT_EDITOR_COLOR);
		
		this.highlighter = highlighter;
		
		load(source);

		add(textPane, BorderLayout.CENTER);
		
		source.addListener(Leaf.NAME, this);
	}

	private void load(TextSource source)
	{
		this.source = source;
		
		textPane = new JTextPane();
		textPane.setBackground(TEXT_EDITOR_COLOR);
		
		textPane.setCaretColor(TEXT_COLOR_BASE);
		textPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		this.setBorder(null);

		textPane.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				textPane.getCaret().setBlinkRate(0);
				textPane.getCaret().setVisible(true);
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				textPane.getCaret().setBlinkRate(500);
				textPane.getCaret().setVisible(true);
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
			}
		});

		doc = textPane.getStyledDocument();

		FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
		int charWidth = fm.charWidth('w');
		int tabWidth = charWidth * 3;
		TabStop[] tabs = new TabStop[20];
		for (int j = 0; j < tabs.length; j++)
		{
			int tab = j + 1;
			tabs[j] = new TabStop(tab * tabWidth);
		}
		TabSet tabSet = new TabSet(tabs);
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(attributes, tabSet);
		int length = textPane.getDocument().getLength();
		doc.setParagraphAttributes(0, length, attributes, true);

		plain = new SimpleAttributeSet();
		StyleConstants.setForeground(plain, TEXT_COLOR_BASE);
		StyleConstants.setBackground(plain, TEXT_EDITOR_COLOR);
		StyleConstants.setBold(plain, true);
		StyleConstants.setFontFamily(plain, "Verdana");
		StyleConstants.setFontSize(plain, 10);

		cjk = new SimpleAttributeSet();
		StyleConstants.setBold(cjk, false);
		StyleConstants.setFontSize(cjk, 16);

		nonASCII = Pattern.compile("[^\\x00-\\x80]+");

		int i = 0;
		int lines = source.getLines().size();
		for (String line : source.getLines())
		{
			try
			{
				doc.insertString(doc.getLength(), line
						+ (++i < lines ? "\n" : ""), plain);
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}

		updateDocumentStyle(0, doc.getLength());
		updateHighlight(0, doc.getLength());

		doc.addDocumentListener(new DocumentListener()
		{

			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
				setDirty();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
				final DocumentEvent e = arg0;
				setDirty();
				Runnable doUpdateStyle = new Runnable()
				{
					@Override
					public void run()
					{
						updateDocumentStyle(e.getOffset(), e.getLength());
						updateHighlight(0, doc.getLength());
					}
				};
				SwingUtilities.invokeLater(doUpdateStyle);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
				setDirty();
				Runnable doUpdateStyle = new Runnable()
				{
					@Override
					public void run()
					{
						updateHighlight(0, doc.getLength());
					}
				};
				SwingUtilities.invokeLater(doUpdateStyle);
			}

		});
	}

	public void updateDocumentStyle(int offset, int length)
	{
		Matcher matcher = null;
		try
		{
			matcher = nonASCII.matcher(doc.getText(offset, length));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		doc.setCharacterAttributes(offset, length, plain, true);
		if (matcher != null)
		{
			while (matcher.find())
			{
				int start = matcher.start() + offset;
				int end = matcher.end() + offset;
				doc.setCharacterAttributes(start, end - start, cjk, false);
			}
		}
	}

	public void updateHighlight(int offset, int length)
	{
		highlighter.update(doc, offset, length);
	}
	
	public void setDirty()
	{
		source.setDirty(true);
	}
	
	public ArrayList<String> getLines()
	{
		StyledDocument doc = textPane.getStyledDocument();

		ArrayList<String> lines = null;

		try
		{
			lines = new ArrayList<String>(Arrays.asList(doc.getText(0,
					doc.getLength()).split("\n")));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}

		return lines;
	}
	
	public JTextPane getTextPane()
	{
		return textPane;
	}
	
	private boolean expandAllowed;
	
	public void allowExpandVertical(boolean value)
	{
		expandAllowed = value;
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		if(expandAllowed)
			return new Dimension(getParent().getParent().getParent().getWidth(), Short.MAX_VALUE);
		else
			return new Dimension(getParent().getParent().getParent().getWidth(), getMinimumSize().height);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		source.removeListener(Leaf.NAME, this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		label.setText((String) evt.getNewValue());
	}
}
