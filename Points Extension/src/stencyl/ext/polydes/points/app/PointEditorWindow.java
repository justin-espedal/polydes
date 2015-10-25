package stencyl.ext.polydes.points.app;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import stencyl.ext.polydes.common.comp.MiniSplitPane;
import stencyl.ext.polydes.points.Prefs;
import stencyl.sw.SW;

public class PointEditorWindow extends JDialog
{
	private static PointEditorWindow _instance;
	
	public static PointEditorWindow get()
	{
		if(_instance == null)
			_instance = new PointEditorWindow();
		
		return _instance;
	}
	
	private MiniSplitPane splitPane;
	private JPanel contents;
	private boolean initialized;
	
	public PointEditorWindow()
	{
		super(SW.get(), "Point Editor", true);
		
		contents = new JPanel(new BorderLayout());
		
		contents.add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		setContentPane(contents);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeWindow();
			}
		});
		
		setVisible(false);
		initialized = false;
	}
	
	private void closeWindow()
	{
		Prefs.set(Prefs.POINTWIN_X, getX());
		Prefs.set(Prefs.POINTWIN_Y, getY());
		Prefs.set(Prefs.POINTWIN_WIDTH, getWidth());
		Prefs.set(Prefs.POINTWIN_HEIGHT, getHeight());
		Prefs.set(Prefs.POINTWIN_SIDEWIDTH, splitPane.getDividerLocation());
		Prefs.save();
		
		setVisible(false);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if(!initialized && visible)
			init();
		
		super.setVisible(visible);
	}
	
	private void init()
	{
		initialized = true;
		
		int x = Prefs.get(Prefs.POINTWIN_X);
		int y = Prefs.get(Prefs.POINTWIN_Y);
		int w = Prefs.get(Prefs.POINTWIN_WIDTH);
		int h = Prefs.get(Prefs.POINTWIN_HEIGHT);
		int dl = Prefs.get(Prefs.POINTWIN_SIDEWIDTH);
		
		splitPane.setLeftComponent(PointEditorPage.get().getSidebar());
		splitPane.setRightComponent(PointEditorPage.get());
		splitPane.setDividerLocation(dl);
		
		setSize(w, h);
		
		if(x == -1 || y == -1)
			setLocationRelativeTo(SW.get());
		else
			setLocation(x, y);
	}
	
	@Override
	public void dispose()
	{
		splitPane.removeAll();
		contents.removeAll();
		
		super.dispose();
	}
	
	public static void disposeWindow()
	{
		if(_instance != null)
		{
			_instance.setVisible(false);
			_instance.dispose();
			_instance = null;
		}
	}
}
