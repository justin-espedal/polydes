package stencyl.ext.polydes.extrasmanager.app.list;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import stencyl.ext.polydes.common.ui.darktree.DTreeSelectionListener;
import stencyl.ext.polydes.common.ui.darktree.DTreeSelectionState;
import stencyl.ext.polydes.common.ui.darktree.DarkTree;
import stencyl.ext.polydes.extrasmanager.ExtrasManagerExtension;
import stencyl.ext.polydes.extrasmanager.app.FileRenameDialog;
import stencyl.ext.polydes.extrasmanager.app.pages.MainPage;
import stencyl.ext.polydes.extrasmanager.data.FileClipboard;
import stencyl.ext.polydes.extrasmanager.data.FileEditor;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFile;
import stencyl.ext.polydes.extrasmanager.data.folder.SysFolder;
import stencyl.ext.polydes.extrasmanager.io.FileOperations;
import stencyl.ext.polydes.extrasmanager.res.Resources;
import stencyl.sw.SW;
import stencyl.sw.app.lists.AbstractItemRenderer;
import stencyl.sw.app.lists.AbstractList;
import stencyl.sw.app.lists.ListListener;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.Util;
import stencyl.sw.util.comp.GroupButton;
import stencyl.thirdparty.misc.comp.FileDrop;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.jidesoft.list.QuickListFilterField;
import com.jidesoft.swing.PaintPanel;

public class FileList extends JList implements MouseListener, MouseMotionListener, FileClipboard.Listener
{
	private static final Logger log = Logger.getLogger(FileList.class);
	
	public static final int H_PADDING = 40;
	public static final int V_PADDING = 56;
	
	public static final int DEFAULT_WIDTH = AbstractItemRenderer.DEFAULT_WIDTH;
	public static final int DEFAULT_HEIGHT = AbstractItemRenderer.DEFAULT_HEIGHT;
	
	protected int rolloverIndex = -1;
	private ListListener listener;
	
	private final boolean showPopup = true;
	
	//---
	
	private TitlePanel titlePanel;
	
	private JButton homeButton;
	private JButton upButton;
	
	private JButton copyButton;
	private JButton cutButton;
	private JButton pasteButton;
	
	private JButton editButton;
	private JButton renameButton;
	private JButton previewButton;
	private JButton deleteButton;
	
	private JButton folderButton;
	private JButton addButton;
	/*
	private JButton sortAzButton;
	private JButton sortZaButton;
	private JButton findButton;
	*/
	
	protected AbstractList list;
    protected QuickListFilterField searchField;
    
	//---
	
	public FileList(final FileListRenderer renderer, ListListener listener, final FileListModel model, final DarkTree<SysFile> tree)
	{
		super(model);
		
		new FileDrop(this, BorderFactory.createEmptyBorder(), true, new FileDrop.Listener()
	    {
			@Override
			public void filesDropped(java.io.File[] files)
	        {
				FileOperations.copy(Arrays.asList(files));
				FileOperations.paste(MainPage.get().getViewedFolder().getFile());
	        }
			
			@Override
			public void stringDropped(String s, String type){}
	    });
		
		this.listener = listener;
		
		setCellRenderer(renderer);
		
		setBorder(null);
		setBackground(Theme.BG_COLOR);
		setFont(Fonts.getTitleBoldFont());
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		
		setFixedCellWidth(H_PADDING + DEFAULT_WIDTH);
		setFixedCellHeight(V_PADDING + DEFAULT_HEIGHT);

		setVisibleRowCount(-1);
		
		//---
		
		addMouseListener
		(
			new MouseAdapter()
			{
				@Override
				public void mouseExited(MouseEvent e)
				{
					repaint();
				}
			}
		);
		
		addMouseListener(this);
		
		//---
		
		tree.addTreeListener(new DTreeSelectionListener<SysFile>()
		{
			DTreeSelectionState<SysFile> state;
			
			@Override
			public void setSelectionState(DTreeSelectionState<SysFile> state)
			{
				this.state = state;
			}
			
			@Override
			public void selectionStateChanged()
			{
				MainPage.get().setViewedFile(state.nodes.get(0).getUserObject());
			}
		});
		
		//---
		
		homeButton = createButton("home", 1, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MainPage.get().setViewedFile((SysFolder) ExtrasManagerExtension.getModel().getRootBranch());
			}
		});
		
		upButton = createButton("back_up", 3, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(model.currView == null || model.currView.getParent() == null || model.currView == ExtrasManagerExtension.getModel().getRootBranch())
				return;
			MainPage.get().setViewedFile((SysFolder) model.currView.getParent());
		}
		});
		
		//---
		
		copyButton = createButton("copy", 1, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileOperations.copy(getSelectedFiles());
		}
		});
		
		cutButton = createButton("cut", 2, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileOperations.cut(getSelectedFiles());
		}
		});
		
		pasteButton = createButton("paste", 3, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			File targetParent = null;
			if(isSelectionEmpty())
				targetParent = model.currView.getFile();
			else if(getSelectedValues().length == 1 && getSelectedValue() instanceof SysFolder)
				targetParent = getSelectedValue().getFile();
			else
				return;
			
			FileOperations.paste(targetParent);
		}
		});
		
		//---
		
		editButton = createButton("edit", 1, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			for(SysFile o : getSelectedValues())
				FileEditor.edit(o.getFile());
		}
		});
		
		renameButton = createButton("rename", 2, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			File primaryFile = getSelectedValue().getFile();
			FileRenameDialog rename = new FileRenameDialog(SW.get(), primaryFile);
			if(rename.getString() == null)
				return;
			
			String name = rename.getString();
			
			String ext = FileOperations.getNameParts(primaryFile.getName())[1];
			
			if(!name.endsWith(ext))
				name += ext;
			
			FileOperations.renameFiles(getSelectedFiles(), name);
		}
		});
		
		previewButton = createButton("preview", 2, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			MainPage.get().setViewedFile(getSelectedValue());
		}
		});
		
		deleteButton = createButton("delete", 3, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileOperations.deleteFiles(getSelectedFiles());
		}
		});
		
		//---
		
		folderButton = createButton("folder", 1, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileOperations.createFolder(model.currView.getFile());
		}
		});
		
		addButton = createButton("add", 3, new ActionListener()
		{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileOperations.createFile(model.currView.getFile());
		}
		});
		
		/*sortAzButton = createButton("sort_az", 2, new ActionListener()
		{
		public void actionPerformed(ActionEvent e)
		{
			//SET SORT MODE AZ
		}
		});
		
		sortZaButton = createButton("sort_za", 3, new ActionListener()
		{
		public void actionPerformed(ActionEvent e)
		{
			//SET SORT MODE ZA
		}
		});
		
		findButton = createButton("find", 4, new ActionListener()
		{
		public void actionPerformed(ActionEvent e)
		{
			//SEARCH
		}
		});*/
		
		model.setListener(new FileListModelListener()
		{
			@Override
			public void viewUpdated(FileListModel src, SysFolder currView)
			{
				upButton.setEnabled(currView != ExtrasManagerExtension.getModel().getRootBranch());
			}
		});
		
		FileClipboard.listeners.add(this);
		
		addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting())
					return;
				
				boolean filesSelected = !isSelectionEmpty();
				copyButton.setEnabled(filesSelected);
				cutButton.setEnabled(filesSelected);
				editButton.setEnabled(filesSelected);
				renameButton.setEnabled(filesSelected);
				previewButton.setEnabled(filesSelected);
				deleteButton.setEnabled(filesSelected);
				
				if(FileClipboard.list().isEmpty())
					pasteButton.setEnabled(false);
				else
				{
					if(!filesSelected)
						pasteButton.setEnabled(true);
					else if(getSelectedValues().length == 1 && getSelectedValue() instanceof SysFolder)
						pasteButton.setEnabled(true);
					else
						pasteButton.setEnabled(false);
				}
			}
		});
		
		upButton.setEnabled(false);
		pasteButton.setEnabled(false);
		copyButton.setEnabled(false);
		cutButton.setEnabled(false);
		editButton.setEnabled(false);
		renameButton.setEnabled(false);
		previewButton.setEnabled(false);
		deleteButton.setEnabled(false);
		
		addMouseMotionListener(this);
		
		registerKeyboardAction(new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				doubleClicked();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		registerKeyboardAction(new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				FileOperations.deleteFiles(getSelectedFiles());
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
		
		registerKeyboardAction(new AbstractAction() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				FileOperations.deleteFiles(getSelectedFiles());
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), JComponent.WHEN_FOCUSED);
		
		//---
		
		titlePanel = new TitlePanel();
		
		revalidate();
		requestFocusInWindow();
	}
	
	public JPanel getTitlePanel()
	{
		return titlePanel;
	}
	
	public JButton createButton(String img, int buttonType, ActionListener l)
	{
		Dimension d = new Dimension(30, 23);
		GroupButton b = new GroupButton(buttonType);
        b.disableEtching();
        b.setIcon(Resources.loadIcon("nav/" + img + ".png"));
        b.setTargetHeight(23);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setMinimumSize(d);
        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.addActionListener(l);
        
        return b;
	}
	
	protected class TitlePanel extends PaintPanel
	{
		//private static final int SEARCH_WIDTH = 120;
		public JLabel label;
		
		public TitlePanel()
		{
			setVertical(true);
	        setStartColor(Theme.COMMAND_BAR_START);
	        setEndColor(Theme.COMMAND_BAR_END);
	        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	        setPreferredSize(new Dimension(1, 35));
	        
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0), BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR)));
			
			add(Box.createHorizontalStrut(8));
	        add(homeButton);
	        add(upButton);
	        add(Box.createHorizontalStrut(8));
	        add(copyButton);
	        add(cutButton);
	        add(pasteButton);
	        add(Box.createHorizontalStrut(8));
	        add(editButton);
	        add(renameButton);
	        add(previewButton);
	        add(deleteButton);
	        add(Box.createHorizontalStrut(8));
	        add(folderButton);
	        add(addButton);
	        /*
	        add(sortAzButton);
	        add(sortZaButton);
	        add(Box.createHorizontalStrut(8));
	        add(findButton);
	        */
	        
			label = HudWidgetFactory.createHudLabel("Extras");
			label.setForeground(Theme.TEXT_COLOR);
			label.setFont(Fonts.getTitleBoldFont());
			
			/*
			searchField = new QuickListFilterField(listModel);
			searchField.setMinimumSize(new Dimension(1, 20));
			searchField.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 8));
			searchField.setBackground(Theme.TEXT_COLOR);
			searchField.setOpaque(false);
			searchField.getTextField().setBackground(Theme.TEXT_COLOR);
			searchField.getTextField().setOpaque(false);
			searchField.getTextField().setForeground(Color.black);
			searchField.getTextField().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			searchField.setFromStart(true);
			
			list = createList(searchField);
			list.setBackground(Theme.EDITOR_BG_COLOR);
			
			RoundedPanel roundedPanel = new RoundedPanel(0);
			roundedPanel.arcSize = 15;
			roundedPanel.setMinimumSize(new Dimension(SEARCH_WIDTH, 22));
	        roundedPanel.setMaximumSize(new Dimension(SEARCH_WIDTH, 22));
	        roundedPanel.setPreferredSize(new Dimension(SEARCH_WIDTH, 22));
	        roundedPanel.setBackground(Color.WHITE);
	        roundedPanel.setLayout(new BorderLayout());
	        roundedPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        roundedPanel.add(searchField, BorderLayout.CENTER);
	        */
			
	        add(Box.createHorizontalStrut(16));
	        add(label);
			/*
	        add(Box.createHorizontalGlue());
	        add(roundedPanel);
			add(Box.createHorizontalStrut(8));
			*/
		}
		
		public void refreshBreadcrumb()
		{
			label.setText("Parent" +  " > " + "Child");
		}
	}
	
	public void dispose()
	{
		FileClipboard.listeners.remove(this);
		listener = null;
	}
	
	@Override
    protected void processMouseEvent(MouseEvent e) 
	{
		if(e.getID() == MouseEvent.MOUSE_EXITED)
		{
			super.processMouseEvent(e);
			return;
		}
		
		Rectangle r = getCellBounds(0, getModel().getSize() - 1);
		
        if(r != null && !(r.contains(e.getPoint()))) 
        {
        	clearSelection();
            e.consume();
        } 
        
        else
        {
            super.processMouseEvent(e);
        }
    }
	
	@Override
	public void mousePressed(MouseEvent e) 
    {     
        int clickCount = e.getClickCount();
        int index = getSelectedIndex();
        
        if(clickCount > 2)
        {
        	e.consume();
        	return;
        }
        
        if(Util.isMacOSX() || Util.isLinux())
        {
        	if(e.isPopupTrigger() && clickCount == 1)
        	{
        		Point p = new Point(e.getX(), e.getY());
    			index = locationToIndex(p);
    			
        		setSelectedIndex(index);
        	}	
        }
        
        else
        {
            if(showPopup && SwingUtilities.isRightMouseButton(e)) 
            {
            	try 
            	{
            		Robot robot = new java.awt.Robot();
            		
            		robot.mousePress(InputEvent.BUTTON1_MASK);
            		robot.mouseRelease(InputEvent.BUTTON1_MASK);
            	} 
            	
            	catch(AWTException ae) 
            	{
            		log.info(ae);
            	}
            }	
        }

        if(index != -1)
        {
        	if(e.getButton() == MouseEvent.BUTTON1 && clickCount > 1)
        	{
        		doubleClicked();
        	}
        }
    }
	
	public void doubleClicked()
	{
		listener.pickedItem(getSelectedValue());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
    { 
    }
	
	@Override
	public void mouseEntered(MouseEvent e) 
    { 
    }
	
	@Override
	public void mouseExited(MouseEvent e) 
    { 
		rolloverIndex = -1;
		
		repaint();
    }
	
	@Override
	public void mouseReleased(MouseEvent e) 
    { 
    }
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouseMoved(e.getPoint());
	}
	
	public void mouseMoved(Point e)
	{
		int oldIndex = rolloverIndex;
		rolloverIndex = -1;
					
		Rectangle b = getCellBounds(0, getModel().getSize() - 1);
						
		if(b == null)
		{
			return;
		}
		
		if(b.contains(e) == false)
		{
			if(oldIndex != -1)
			{
				repaint();
			}
			
			return;
		}
		
		//Same row but no cell
		Rectangle r = getCellBounds(getModel().getSize() - 1, getModel().getSize() - 1);
		Rectangle deadArea = new Rectangle(r.x + r.width, r.y, getBounds().width, r.height);
						
		if(deadArea.contains(e))
		{
			if(oldIndex != -1)
			{
				repaint();
			}
			
			return;
		}

		rolloverIndex = locationToIndex(e);
		
		if(oldIndex == rolloverIndex)
		{
			return;
		}

		if(oldIndex != -1)
		{
			Rectangle repaint = getCellBounds(oldIndex, oldIndex);
			
			repaint(repaint);
		}
		
		if(rolloverIndex != -1)
		{				
			Rectangle repaint = getCellBounds(rolloverIndex, rolloverIndex);
			
			repaint(repaint);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height + getFixedCellHeight());
	}
	
	@Override
	public SysFile getSelectedValue()
	{
		return (SysFile) super.getSelectedValue();
	}
	
	@Override
	public SysFile[] getSelectedValues()
	{
		Object[] objs = super.getSelectedValues();
		SysFile[] vals = new SysFile[objs.length];
		for(int i = 0; i < objs.length; ++i)
			vals[i] = (SysFile) objs[i];
		
		return vals;
	}
	
	public List<File> getSelectedFiles()
	{
		Object[] objs = super.getSelectedValues();
		
		ArrayList<File> files = new ArrayList<File>(objs.length);
		for(Object o : objs)
			files.add(((SysFile) o).getFile());
		return files;
	}

	@Override
	public void clipboardContentsUpdated()
	{
		boolean enabled = false;
		
		if(!FileClipboard.list().isEmpty())
		{
			if(isSelectionEmpty())
				enabled = true;
			else if(getSelectedValues().length == 1 && getSelectedValue() instanceof SysFolder)
				enabled = true;
		}
		
		pasteButton.setEnabled(enabled);
	}
}
