package stencyl.ext.polydes.paint.app.editors.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import stencyl.ext.polydes.paint.app.editors.image.tools.Brush;
import stencyl.ext.polydes.paint.app.editors.image.tools.Bucket;
import stencyl.ext.polydes.paint.app.editors.image.tools.Ellipse;
import stencyl.ext.polydes.paint.app.editors.image.tools.Erase;
import stencyl.ext.polydes.paint.app.editors.image.tools.Hand;
import stencyl.ext.polydes.paint.app.editors.image.tools.Line;
import stencyl.ext.polydes.paint.app.editors.image.tools.Pencil;
import stencyl.ext.polydes.paint.app.editors.image.tools.Pick;
import stencyl.ext.polydes.paint.app.editors.image.tools.Rectangle;
import stencyl.ext.polydes.paint.app.editors.image.tools.Select;
import stencyl.ext.polydes.paint.app.editors.image.tools.Tool;
import stencyl.ext.polydes.paint.res.Resources;

/*
 * Toolbar for image editing.
 */

public class DrawTools extends JPanel implements KeyListener
{
	public static Color BACKGROUND = Color.WHITE;
	public static int BUTTON_WIDTH = 24;
	public static int BUTTON_HEIGHT = 23;
	
	private ButtonGroup toolButtonGroup;
	private ToolButton brushButton;
	private ToolButton bucketButton;
	private ToolButton ellipseButton;
	private ToolButton eraseButton;
	private ToolButton handButton;
	private ToolButton lineButton;
	private ToolButton pencilButton;
	private ToolButton pickButton;
	private ToolButton rectangleButton;
	private ToolButton selectButton;
	
	private DrawArea drawArea;
	private Tool currentTool;
	private ImageEditPane editPane;
	
	private int buttonPairs;
	
	public DrawTools()
	{
		toolButtonGroup = new ButtonGroup();
		
		brushButton = createToolButton(Resources.loadIcon("draw/brush.png"), new Brush());
		bucketButton = createToolButton(Resources.loadIcon("draw/bucket.png"), new Bucket());
		ellipseButton = createToolButton(Resources.loadIcon("draw/ellipse.png"), new Ellipse());
		eraseButton = createToolButton(Resources.loadIcon("draw/erase.png"), new Erase());
		handButton = createToolButton(Resources.loadIcon("draw/hand.png"), new Hand());
		lineButton = createToolButton(Resources.loadIcon("draw/line.png"), new Line());
		pencilButton = createToolButton(Resources.loadIcon("draw/pencil.png"), new Pencil());
		pickButton = createToolButton(Resources.loadIcon("draw/pick.png"), new Pick());
		rectangleButton = createToolButton(Resources.loadIcon("draw/rectangle.png"), new Rectangle());
		selectButton = createToolButton(Resources.loadIcon("draw/select.png"), new Select());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(BACKGROUND);
		
		buttonPairs = 0;
		add(createButtonPair(selectButton, handButton));
		add(createButtonPair(pickButton, eraseButton));
		add(createButtonPair(brushButton, pencilButton));
		add(createButtonPair(bucketButton, lineButton));
		add(createButtonPair(rectangleButton, ellipseButton));
		
		editPane = null;
	}
	
	public void setDrawArea(DrawArea area)
	{
		drawArea = area;
		area.setTool(currentTool);
	}

	public DrawArea getDrawArea()
	{
		return drawArea;
	}
	
	public JButton createButton(ImageIcon icon)
	{
		JButton button = new JButton();

		button.setIcon(icon);

		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setVerticalAlignment(SwingConstants.CENTER);

		button.setMinimumSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		button.setMaximumSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		button.setPreferredSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		
		return button;
	}
	
	public ToolButton createToolButton(ImageIcon icon, Tool tool)
	{
		ToolButton button = new ToolButton(tool);

		button.setIcon(icon);

		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setVerticalAlignment(SwingConstants.CENTER);

		button.setMinimumSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		button.setMaximumSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		button.setPreferredSize(new Dimension(BUTTON_WIDTH,
				BUTTON_HEIGHT));
		
		toolButtonGroup.add(button);
		
		return button;
	}
	
	public JPanel createButtonPair(JToggleButton b1, JToggleButton b2)
	{
		JPanel pair = new JPanel();
		
		pair.setLayout(new BoxLayout(pair, BoxLayout.X_AXIS));
		pair.setBackground(BACKGROUND);
		pair.setMaximumSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
		pair.setMinimumSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
		
		if(b1 != null)
			pair.add(b1);
		if(b2 != null)
			pair.add(b2);
		
		++buttonPairs;
		
		setMaximumSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT * buttonPairs));
		setMinimumSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT * buttonPairs));
		
		return pair;
	}
	
	public void setTool(Tool newTool)
	{
		if(drawArea != null)
			drawArea.setTool(newTool);
		
		if(newTool != null)
			editPane.setToolOptions(newTool.getOptions());
		
		currentTool = newTool;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getTool(Class<T> toolClass)
	{
		ToolButton b;
		Enumeration<AbstractButton> e = toolButtonGroup.getElements();
		while(e.hasMoreElements())
		{
			b = (ToolButton) e.nextElement();
			if(toolClass.isInstance(b.tool))
			{
				return (T) b.tool;
			}
		}
		return null;
	}
	
	public class ToolButton extends JToggleButton
	{
		public Tool tool;
		
		public ToolButton(final Tool tool)
		{
			this.tool = tool;
			
			addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setTool(tool);
				}
			});
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			if(isSelected())
			{
				g.setColor(Color.BLUE);
				g.drawRect(0, 0, getWidth(), getHeight());
				g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
				//TODO
				//draw the selection blue rectangle.. better
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(drawArea.usingTool)
			return;
		
		if(e.isControlDown())
			return;
		
		switch(e.getKeyCode())
		{
			case(KeyEvent.VK_S):
				selectButton.doClick();
				break;
			case(KeyEvent.VK_H):
				handButton.doClick();
				break;
			case(KeyEvent.VK_F):
				bucketButton.doClick();
				break;
			case(KeyEvent.VK_B):
				brushButton.doClick();
				break;
			case(KeyEvent.VK_E):
				eraseButton.doClick();
				break;
			case(KeyEvent.VK_P):
				pencilButton.doClick();
				break;
			case(KeyEvent.VK_K):
				pickButton.doClick();
				break;
			case(KeyEvent.VK_O):
				if(lineButton.isSelected())
					rectangleButton.doClick();
				else if(rectangleButton.isSelected())
					ellipseButton.doClick();
				else
					lineButton.doClick();
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	public void setOptionsBarContainer(ImageEditPane editPane)
	{
		this.editPane = editPane;
	}
}
