package stencyl.ext.polydes.paint.app.editors.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stencyl.ext.polydes.common.comp.StatusBar;
import stencyl.ext.polydes.common.comp.colors.ColorDialog;
import stencyl.ext.polydes.common.comp.colors.ColorDisplay;
import stencyl.ext.polydes.paint.app.editors.image.tools.Hand;
import stencyl.ext.polydes.paint.app.editors.image.tools.Pick;
import stencyl.ext.polydes.paint.app.editors.image.tools.Select;
import stencyl.ext.polydes.paint.app.editors.image.tools.ToolOptions;
import stencyl.sw.util.UI;

public class ImageEditPane extends JPanel
{
	public static Color WRAPPER_BACKGROUND = new Color(0x797F88);
	
	protected DrawTools toolbar;
	protected JPanel navBar;
	protected JPanel sidebar;
	private ToolOptions optionsBar;
	private ToolOptions blankOptionsBar;
	private ColorDisplay colorDisplay;
	@SuppressWarnings("unused")
	private JPanel statusbar;
	private JPanel wrapper;
	protected DrawArea currentEditor;
	
	private ColorDialog colorDialog;
	
	public ImageEditPane()
	{
		this(new DrawTools());
	}
	
	public ImageEditPane(DrawTools toolbar)
	{
		super(new BorderLayout());
		
		this.toolbar = toolbar;
		
		setBackground(WRAPPER_BACKGROUND);
		add(statusbar = StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.add(toolbar);
		sidebar.add(Box.createRigidArea(new Dimension(20, 15)));
		sidebar.add(colorDisplay = new ColorDisplay(15, 15, Color.BLACK, null));
		sidebar.add(Box.createRigidArea(new Dimension(20, 15)));
		
		colorDialog = new ColorDialog(colorDisplay, null);
		colorDialog.setVisible(false);
		
		colorDisplay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				colorDialog.setVisible(true);
				Point p = (Point) e.getPoint().clone();
				SwingUtilities.convertPointToScreen(p, (Component) e.getSource());
				colorDialog.setLocation(p.x, p.y);
				colorDialog.setDisplayColor(colorDisplay.getColor());
			}
		});
		
		colorDialog.addChangeListener(colorUpdater);
		
		blankOptionsBar = new ToolOptions();
		optionsBar = blankOptionsBar;
		sidebar.add(optionsBar);
		
		toolbar.setOptionsBarContainer(this);
		toolbar.getTool(Hand.class).setImmobilePane(this);
		toolbar.getTool(Pick.class).setColorDisplay(colorDisplay);
		
		add(sidebar, BorderLayout.WEST);
		
		navBar = new JPanel();
		
		add(navBar, BorderLayout.NORTH);
		
		wrapper = new JPanel(new GridBagLayout());
		wrapper.setBackground(WRAPPER_BACKGROUND);
		
		JScrollPane scroller = UI.createScrollPane(wrapper);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(scroller, BorderLayout.CENTER);
	}
	
	public void removeDrawArea()
	{
		wrapper.remove(currentEditor);
		
		uninstallListeners(currentEditor);
		
		currentEditor = null;
	}
	
	public void uninstallListeners(DrawArea area)
	{
		area.removeMouseListener(toolbar.getTool(Hand.class));
		area.removeMouseMotionListener(toolbar.getTool(Hand.class));
		area.removeKeyListener(toolbar.getTool(Select.class));
		area.removeKeyListener(toolbar);
	}
	
	public void setDrawArea(DrawArea drawArea)
	{
		currentEditor = drawArea;
		
		toolbar.setDrawArea(currentEditor);
		toolbar.setVisible(true);
		toolbar.getTool(Hand.class).setArea(currentEditor);
		
		installListeners(currentEditor);
		wrapper.add(currentEditor);
	}
	
	ChangeListener colorUpdater = new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			if(currentEditor != null)
			{
				currentEditor.currentColor = colorDisplay.getColor();
				currentEditor.currentRGB = colorDisplay.getColor().getRGB();
			}
		}
	};
	
	public void installListeners(DrawArea area)
	{
		area.addMouseListener(toolbar.getTool(Hand.class));
		area.addMouseMotionListener(toolbar.getTool(Hand.class));
		area.addKeyListener(toolbar.getTool(Select.class));
		area.addKeyListener(toolbar);
	}
	
	public void showToolbar(boolean value)
	{
		toolbar.setVisible(value);
	}
	
	public void setToolOptions(ToolOptions options)
	{
		if(optionsBar != null)
			sidebar.remove(optionsBar);
		optionsBar = (options != null) ? options : blankOptionsBar;
		sidebar.add(optionsBar);
		
		revalidate();
		repaint();
	}
}
