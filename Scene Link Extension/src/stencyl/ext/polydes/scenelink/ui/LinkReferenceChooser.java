package stencyl.ext.polydes.scenelink.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import stencyl.core.lib.Game;
import stencyl.core.lib.scene.SceneModel;
import stencyl.ext.polydes.scenelink.SceneLinkExtension;
import stencyl.ext.polydes.scenelink.data.Link;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;
import stencyl.ext.polydes.scenelink.data.PageLink;
import stencyl.sw.SW;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.UI;
import stencyl.sw.util.comp.RoundedPanel;

import com.jidesoft.list.FilterableListModel;
import com.jidesoft.list.QuickListFilterField;
import com.jidesoft.swing.PaintPanel;


public class LinkReferenceChooser extends JWindow
{
	/*-------------------------------------*\
	 * Data
	\*-------------------------------------*/ 
	
	private static final int WIDTH = 320;
	private static final int HEIGHT = 480;
	
	private JPanel wrapper;	
	private QuickListFilterField field;
	
	private JComboBox chooser;
	
	private ResourceList resourceList;
	
	private Link link;
	
	private ArrayList<ActionListener> listeners;
	
	/*-------------------------------------*\
	 * Constructor
	\*-------------------------------------*/ 

	public LinkReferenceChooser(JWindow owner, Link link)
	{
		super(owner != null ? owner : SW.get());
		setFocusable(true);
		setSize(WIDTH, HEIGHT);
		
		this.link = link;
		listeners = new ArrayList<ActionListener>();
		
		add(createContentPanel(), BorderLayout.CENTER);
		
		addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowLostFocus(WindowEvent e)
			{
				dispose();
			}
		});
	}
	
	/*-------------------------------------*\
	 * Construct UI
	\*-------------------------------------*/ 

	public JComponent createContentPanel()
	{
		wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(true);
		wrapper.setBackground(Theme.EDITOR_BG_COLOR);
		wrapper.setBorder(BorderFactory.createRaisedBevelBorder());
		
		//---
		
		field = new QuickListFilterField(new DefaultListModel());
		field.setPreferredSize(new Dimension(1, 20));
		field.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 8));
		field.setBackground(Color.WHITE);
		field.setOpaque(false);
		field.getTextField().setBackground(Color.WHITE);
		field.getTextField().setOpaque(false);
		field.getTextField().setForeground(Color.DARK_GRAY);
		field.getTextField().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		field.setWildcardEnabled(true);
		field.setFromStart(true);
		field.setSearchingDelay(0);
		
		if(link instanceof PageLink)
			resourceList = new ResourceList(field.getDisplayListModel(), SceneLinkExtension.getPages());
		else
			resourceList = new ResourceList(field.getDisplayListModel(), Game.getGame().getScenes());
		
		field.addKeyListener
		(
			new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						selectLinkRef();
					}
				}
			}
		);
		
		field.getTextField().registerKeyboardAction(new AbstractAction() 
		{
            @Override
			public void actionPerformed(ActionEvent e) 
            {
            	resourceList.requestFocus();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
		
		field.getTextField().registerKeyboardAction(new AbstractAction() 
		{
            @Override
			public void actionPerformed(ActionEvent e) 
            {
            	resourceList.requestFocus();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
		
		field.getTextField().registerKeyboardAction(new AbstractAction() 
		{
            @Override
			public void actionPerformed(ActionEvent e) 
            {
            	selectLinkRef();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		
		resourceList.registerKeyboardAction(new AbstractAction() 
		{
            @Override
			public void actionPerformed(ActionEvent e) 
            {
            	selectLinkRef();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		
		resourceList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					selectLinkRef();
				}
			}
		});
		
		RoundedPanel roundedPanel = new RoundedPanel(0, 20);
        roundedPanel.setBackground(Color.WHITE);
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        roundedPanel.add(field, BorderLayout.CENTER);
        
        chooser = new JComboBox(new ScenePageChooserModel());
		chooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String s = "" + chooser.getSelectedItem();
				if(s.equals("Scene"))
					resourceList.refreshList(Game.getGame().getScenes());
				else if(s.equals("Page"))
					resourceList.refreshList(SceneLinkExtension.getPages());
			}
		});
		chooser.setSelectedItem((link instanceof PageLink) ? "Page" : "Scene");
        
        PaintPanel panel = new PaintPanel();
        panel.setBorder
        (
        	BorderFactory.createCompoundBorder
        	(
        		BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
        		BorderFactory.createEmptyBorder(5, 5, 5, 5)
			)
        );
        	
        panel.setVertical(true);
        panel.setStartColor(Theme.BUTTON_BAR_START);
        panel.setEndColor(Theme.BUTTON_BAR_END);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(new JLabel("Link to:"));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(chooser);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Filter by Name:"));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(roundedPanel);
        panel.add(Box.createHorizontalStrut(5));
        
        wrapper.add(panel, BorderLayout.NORTH);
		
		//---

        JScrollPane scroller = UI.createScrollPane(resourceList);
		scroller.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Theme.BORDER_COLOR));
		scroller.setBackground(Theme.LIGHT_BG_COLOR);
		scroller.setPreferredSize(new Dimension(350, Integer.MAX_VALUE));
		
		wrapper.add(scroller, BorderLayout.CENTER);

		//---

		return wrapper;
	}
	
	public void selectLinkRef()
	{
		Object o = resourceList.getSelectedValue();
		if(o == null)
			link = Link.createBlank();
		else if(o instanceof LinkPageModel)
			link = Link.create("Page", ((LinkPageModel) o).getId());
		else if(o instanceof SceneModel)
			link = Link.create("Scene", ((SceneModel) o).getID());
		
		for(ActionListener l : listeners)
		{
			l.actionPerformed(new ActionEvent(this, 0, ""));
			setVisible(false);
			dispose();
		}
	}
	
	public Link getLink()
	{
		return link;
	}
	
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
	
	static class ResourceFilter
	{
		String name;
		Class<?> type;
		
		public ResourceFilter(String name, Class<?> type)
		{
			this.name = name;
			this.type = type;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	static class ScenePageChooserModel extends DefaultComboBoxModel
	{
		@Override
		public Object getElementAt(int index)
		{
			return index == 0 ? "Scene" : "Page";
		}
		
		@Override
		public int getSize()
		{
			return 2;
		}
	}
	
	class ResourceList extends JList
	{
		CustomModel customModel;
		
		public ResourceList(FilterableListModel model, Collection<?> objects)
		{
			super(model);
			
			customModel = new CustomModel(objects);
			
			model.setActualModel(customModel);
			
			setCellRenderer(new CustomRenderer());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setBackground(Theme.LIGHT_BG_COLOR);
			setForeground(Color.WHITE);
			setFixedCellHeight(22);
			setVisibleRowCount(-1);
		}
		
		public void refreshList(Collection<?> objects)
		{
			customModel.refresh(objects);
		}
	}
	
	class CustomModel extends DefaultListModel
	{
		public CustomModel(Collection<?> objects)
		{
			refresh(objects);
		}
		
		public void refresh(Collection<?> objects)
		{
			clear();
			
			for(Object r : objects)
			{
				addElement(r);
			}
		}
	}
	
	class CustomRenderer extends JPanel implements ListCellRenderer 
	{
		JLabel mainLabel;
		
		public CustomRenderer()
		{
			super(new BorderLayout());
			
			mainLabel = new JLabel();
			
			add(mainLabel, BorderLayout.WEST);
			
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent
		(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus
		) 
		{
			mainLabel.setForeground(list.getForeground());
			
			if(isSelected)
				setBackground(new Color(0x336699));
			else
				setBackground(list.getBackground());
			
			mainLabel.setText("" + value);
			setToolTipText("" + value);
			
			return this;
		}
	}
	
	@Override
	public void dispose()
	{
		removeAll();
	}
}
