package stencyl.ext.polydes.datastruct.ui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.sw.lnf.Theme;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.comp.GroupButton;

public abstract class LightweightWindow extends SnappingDialog
{
	private static final LanguagePack lang = LanguagePack.get();
	
	private WindowDragger dragger;
	private ResizeListener resizeListener;
	protected JButton okButton;
	
	private JPanel wrapper;
	private JPanel contents;
	
	private Action closeWindowAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			cancel();
		}
	};
	
	public LightweightWindow(JDialog owner)
	{
		super(owner);
		setUndecorated(true);
		
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeWindow");
		getRootPane().getActionMap().put("closeWindow", closeWindowAction);
		
		resizeListener = new ResizeListener();
		resizeListener.setWindow(this);
		
		wrapper = new JPanel(new BorderLayout());
		wrapper.add(createTitleBar(), BorderLayout.NORTH);
		wrapper.add(createButtonBar(), BorderLayout.SOUTH);
		wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
		
		setContentPane(wrapper);
		
		pack();
		
		setVisible(true);
	}
	
	public void setContents(JPanel contents)
	{
		if(this.contents != null)
		{
			this.contents.removeComponentListener(resizeListener);
			wrapper.remove(this.contents);
		}
		wrapper.add(this.contents = contents, BorderLayout.CENTER);
		contents.addComponentListener(resizeListener);
		pack();
	}
	
	public JPanel createTitleBar()
	{
		final JButton closeButton = new JButton();
		
		final Icon normalIcon = Resources.loadIcon("window_close.png");
		final Icon hoverIcon = Resources.loadIcon("window_close_hovered.png");
		
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setIcon(normalIcon);
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setFocusPainted(false);
		
		closeButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				closeButton.setIcon(hoverIcon);
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				closeButton.setIcon(normalIcon);
			}
		});
		
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}
		});
		
		JPanel titleBar = new JPanel();
		titleBar.setLayout(new BoxLayout(titleBar, BoxLayout.X_AXIS));
		titleBar.setBorder
		(
			BorderFactory.createCompoundBorder
			(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)
			)
		);
		titleBar.setBackground(new Color(173, 173, 173));
		
		titleBar.add(Box.createHorizontalGlue());
		titleBar.add(closeButton);
		
		dragger = new WindowDragger(this);
		titleBar.addMouseListener(dragger);
		titleBar.addMouseMotionListener(dragger);
		
		return titleBar;
	}
	
	public JPanel createButtonBar()
	{
		okButton = new GroupButton(0);
		
		okButton.setAction(new AbstractAction(lang.get("globals.apply"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (verify())
				{
					submit();
				}
			}
		});
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
		buttons.setBackground(null);
		
		buttons.add(Box.createHorizontalGlue());
		buttons.add(okButton);
		buttons.add(Box.createHorizontalStrut(10));
		
		return buttons;
	}
	
	protected abstract boolean verify();
	
	public void submit()
	{
		setVisible(false);
	}
	
	public void cancel()
	{
		setVisible(false);
	}
	
	@Override
	public void dispose()
	{
		if(contents != null)
			contents.removeComponentListener(resizeListener);
		if(wrapper != null)
			wrapper.removeAll();
		
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), null);
		getRootPane().getActionMap().put("closeWindow", null);
		
		dragger = null;
		contents = null;
		wrapper = null;
		resizeListener = null;
		
		super.dispose();
	}
}

class ResizeListener extends ComponentAdapter
{
	private Window window;
	
	public ResizeListener()
	{
	}
	
	public void setWindow(Window window)
	{
		this.window = window;
	}
	
	@Override
	public void componentResized(ComponentEvent e)
	{
		if(window != null)
			window.pack();
	}
}