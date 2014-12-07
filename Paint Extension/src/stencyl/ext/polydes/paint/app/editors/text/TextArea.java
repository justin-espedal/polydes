package stencyl.ext.polydes.paint.app.editors.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

import stencyl.ext.polydes.paint.app.editors.DataItemEditor;
import stencyl.ext.polydes.paint.data.TextSource;

@SuppressWarnings("serial")
public class TextArea extends JPanel implements DataItemEditor
{
	public static final Color TEXT_EDITOR_COLOR = new Color(43, 43, 43);
	public static final Color TEXT_COLOR_BASE = Color.WHITE;
	
	public static final Font displayNameFont = new Font("Arial", Font.BOLD, 20);
	
	private Boolean changed;
	private JLabel label;
	private JTextPane textPane;

	private StyledDocument doc;
	private SimpleAttributeSet plain;
	private SimpleAttributeSet cjk;
	private Pattern nonASCII;
	
	private Highlighter highlighter;
	
	public TextArea(TextSource source, Highlighter highlighter)
	{
		super(new BorderLayout());

		this.highlighter = highlighter;
		
		load(source);

		changed = false;

		add(label, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
	}

	private void load(TextSource source)
	{
		label = new JLabel(source.getName());
		label.setBackground(TEXT_EDITOR_COLOR);
		label.setForeground(new Color(0x717171));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setFont(displayNameFont);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		label.setOpaque(true);
		
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

		source.setEditor(this);
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

	@Override
	public boolean isDirty()
	{
		return changed;
	}

	@Override
	public Object getContents()
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

		System.out.println(lines);

		return lines;
	}

	@Override
	public void setClean()
	{
		changed = false;
	}

	@Override
	public void setDirty()
	{
		changed = true;
	}
	
	@Override
	public void nameChanged(String name)
	{
		label.setText(name);
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
}
