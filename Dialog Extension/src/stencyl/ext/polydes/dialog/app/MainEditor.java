package stencyl.ext.polydes.dialog.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import stencyl.ext.polydes.dialog.app.editors.text.TextArea;
import stencyl.ext.polydes.dialog.app.pages.DialogPage;
import stencyl.ext.polydes.dialog.app.pages.MacrosPage;
import stencyl.ext.polydes.dialog.res.Resources;
import stencyl.sw.lnf.Theme;

@SuppressWarnings("serial")
public class MainEditor extends JPanel
{
	private static MainEditor _instance;
	
	private static final int BUTTON_WIDTH = 70;
	private static final int BUTTON_HEIGHT = 57;
	
	private ButtonGroup buttonGroup;
	private JToggleButton dialogButton;
	private JToggleButton macrosButton;
	
	private JPanel buttonBar;
	private JPanel currentPage;
	private JPanel blank;
	
	public static final Color SIDEBAR_COLOR = new Color(62, 62, 62);
	
	private MainEditor()
	{
		super(new BorderLayout());
		
		add(createVerticalButtonBar(), BorderLayout.WEST);
		
		blank = new JPanel(new BorderLayout());
		blank.setBackground(TextArea.TEXT_EDITOR_COLOR);
		blank.add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		currentPage = blank;
		
		add(blank);
	}
	
	public static MainEditor get()
	{
		if(_instance == null)
			_instance = new MainEditor();
		
		return _instance;
	}
	
	private JPanel createVerticalButtonBar()
	{
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(SIDEBAR_COLOR);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x333333)));
		
		buttonBar = new JPanel();
		
		buttonBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x333333)));
		
		buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.Y_AXIS));
		buttonBar.setBackground(SIDEBAR_COLOR);
		buttonBar.setMaximumSize(new Dimension(BUTTON_WIDTH,1000));
		buttonBar.setMinimumSize(new Dimension(BUTTON_WIDTH,100));
		
		//---
		
		buttonGroup = new ButtonGroup();
		
		dialogButton = createButton("Dialog", Resources.loadIcon("main/dialog.png"));
		macrosButton = createButton("Macros", Resources.loadIcon("main/macros.png"));
		
		buttonBar.add(dialogButton);
		buttonBar.add(macrosButton);
		
		buttonPanel.add(buttonBar, BorderLayout.NORTH);
		buttonPanel.add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		return buttonPanel;
	}
	
	public JToggleButton createButton(String name, ImageIcon icon)
	{
		JToggleButton button = new JToggleButton()
		{
			public void paintComponent(Graphics g)
			{
				if(!isSelected())
				{
					super.paintComponent(g);
					setForeground(Theme.TEXT_COLOR.darker());
					return;
				}
				
				g.setColor(new Color(0x666666));
				setForeground(Theme.TEXT_COLOR);
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		
		button.setIconTextGap(8);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setBorder
		(
			BorderFactory.createCompoundBorder
			(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x454545)), 
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x333333))
			)
		);
		
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setForeground(Theme.TEXT_COLOR.darker());		
		
		button.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		button.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		
		button.setAction
		(
			new AbstractAction(name, icon)
			{
				public void actionPerformed(ActionEvent e) 
				{
					switchToPage(e.getActionCommand());
				}
			}
		);
		
		buttonGroup.add(button);
				
		return button;
	}
	
	public void switchToPage(String pageName)
	{
		if(currentPage != null)
			remove(currentPage);
		
		if(pageName.equals("Dialog"))
		{
			currentPage = DialogPage.get();			
			dialogButton.setSelected(true);			
		}
		else if(pageName.equals("Macros"))
		{
			currentPage = MacrosPage.get();			
			macrosButton.setSelected(true);			
		}
		
		add(currentPage, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	public static void disposePages()
	{
		DialogPage.dispose();
		MacrosPage.dispose();
		
		_instance = null;
	}

	public void gameSaved()
	{
		revalidate();
		repaint();
	}
}
