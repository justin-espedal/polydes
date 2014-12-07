package stencyl.ext.polydes.paint.app.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeSelectionModel;

@SuppressWarnings("serial")
public class InlineTreeInput extends JPanel implements ComponentListener
{
	// private static final Font selectedFont =
	private static final Font branchFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 11.0f);
	private static final Font leafFont = UIManager.getFont("Label.font").deriveFont(11.0f);
	private static final Color inputActiveColor = Color.WHITE;
	private static final Color invalidColor = Color.RED;
	private static final Color selectedColor = new Color(102, 102, 102);
	
	private DTree dtree;
	private JTree tree;
	private boolean valid;
	private JTextField input;
	private String previousValue;
	private Rectangle viewRect;
	private int treeX;
	private int treeWidth;

	public InlineTreeInput(DTree dtree)
	{
		super(new BorderLayout());

		this.dtree = dtree;

		input = new SelectTextField()
		{
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				if (!(valid || input.getText().equals(previousValue)))
				{
					g.setColor(invalidColor);
					FontMetrics fm = g.getFontMetrics();
					g.drawLine(
							0,
							getHeight() - 4,
							fm.stringWidth(input.getText())
									- input.getScrollOffset(), getHeight() - 4);
				}
			}
		};

		input.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				input.select(0, 0);
				updatePanel();
			}

			@Override
			public void focusLost(FocusEvent e)
			{
			}
		});

		input.setFont(leafFont);
		input.setForeground(inputActiveColor);
		input.setBackground(selectedColor);
		input.setCaretColor(Color.WHITE);
		input.setBorder(null);
		input.setMargin(new Insets(0, 0, 0, 0));

		setBackground(selectedColor);
		setBorder(null);

		add(input, BorderLayout.CENTER);

		valid = true;
		tree = dtree.getTree();
		tree.addComponentListener(this);
	}

	public void paintComponent(Graphics g)
	{
		updateTreeWidth();

		g.setClip(treeX - getX(), 0, treeWidth, DTree.ITEM_HEIGHT);
		g.setColor(getBackground());
		g.fillRect(treeX - getX(), 0, treeWidth, DTree.ITEM_HEIGHT);

		super.paintComponent(g);
	}

	public void setValid(boolean value)
	{
		valid = value;
		repaint();
	}
	
	public void setNodeType(boolean isLeaf)
	{
		input.setFont(isLeaf ? leafFont : branchFont);
	}

	public void setText(String text)
	{
		input.setText(text);
		input.select(0, 0);
	}

	public String getText()
	{
		return input.getText();
	}

	public void selectAll()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				((SelectTextField) input).selectAll(true);
			}
		});
	}

	@Override
	public void addKeyListener(KeyListener l)
	{
		input.addKeyListener(l);
	}

	public Document getDocument()
	{
		return input.getDocument();
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		updatePanel();
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
	}

	public void updatePanel()
	{
		updateTreeWidth();

		TreeSelectionModel model = tree.getSelectionModel();
		((AbstractLayoutCache) model.getRowMapper()).invalidateSizes();
		tree.treeDidChange();

		input.setSize(treeWidth, DTree.ITEM_HEIGHT);
		setSize(treeWidth, DTree.ITEM_HEIGHT);
	}

	public void updateTreeWidth()
	{
		viewRect = dtree.getScroller().getViewport().getViewRect();
		treeX = viewRect.x;
		treeWidth = viewRect.width;
	}

	public void setPreviousValue(String value)
	{
		previousValue = value;
	}
}

@SuppressWarnings("serial")
class SelectTextField extends JTextField
{
//	public SelectTextField()
//	{
//		setSelectionColor(new Color(0x404061));
//	}
	
	@Override
	public void selectAll()
	{
		return;
	}

	public void selectAll(boolean override)
	{
		super.selectAll();
	}
}
