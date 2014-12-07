package stencyl.ext.polydes.paint.app.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import stencyl.ext.polydes.paint.app.MainEditor;
import stencyl.ext.polydes.paint.app.StatusBar;
import stencyl.ext.polydes.paint.data.DataItem;
import stencyl.ext.polydes.paint.data.Folder;
import stencyl.ext.polydes.paint.data.FolderHierarchyModel;
import stencyl.ext.polydes.paint.res.Resources;
import stencyl.sw.util.UI;

//Short for "Dialog" Tree. Just because. ...But it could also mean "DataItem Tree".
@SuppressWarnings("serial")
public class DTree extends JPanel implements TreeSelectionListener,
		ActionListener, KeyListener, CellEditorListener, CellEditValidator
{
	public static final int DEF_WIDTH = 150;
	public static final int MINI_BUTTON_WIDTH = 25;
	public static final int MINI_BUTTON_HEIGHT = 21;
	public static final int ITEM_HEIGHT = 20;
	
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private ArrayList<DefaultMutableTreeNode> selectedNodes;
	private FolderHierarchyModel folderModel;
	
	private DTreeNodeCreator nodeCreator;
	private DTreeCellRenderer renderer;
	private DTreeCellEditor editor;
	private DTreeSelectionState selectionState;

	private ArrayList<DTreeSelectionListener> listeners;
	
	private JScrollPane scroller;
	
	private JPanel miniButtonBar;
	private JButton newItemButton;
	private JButton removeItemButton;
	private JButton newFolderButton;
	private JButton searchButton;
	
	private static ImageIcon newFolderEnabled = Resources
			.loadIcon("tree/folder-enabled.png");
	private static ImageIcon newFolderDisabled = Resources
			.loadIcon("tree/folder-disabled.png");
	private static ImageIcon newFolderPressed = Resources
			.loadIcon("tree/folder-pressed.png");
	
	private static ImageIcon newItemEnabled = Resources
			.loadIcon("tree/plus-enabled.png");
	private static ImageIcon newItemDisabled = Resources
			.loadIcon("tree/plus-disabled.png");
	private static ImageIcon newItemPressed = Resources
			.loadIcon("tree/plus-pressed.png");
	
	private static ImageIcon removeItemEnabled = Resources
			.loadIcon("tree/minus-enabled.png");
	private static ImageIcon removeItemDisabled = Resources
			.loadIcon("tree/minus-disabled.png");
	private static ImageIcon removeItemPressed = Resources
			.loadIcon("tree/minus-pressed.png");

	private boolean listEditEnabled;

	public DTree(FolderHierarchyModel folderModel)
	{
		super(new BorderLayout());

		this.folderModel = folderModel;
		
		listEditEnabled = false;

		root = new DefaultMutableTreeNode(new Folder("root"));
		tree = new JTree(root)
		{
			@Override
		    public Dimension getPreferredScrollableViewportSize()
		    {
		        return getPreferredSize();
		    }
		};
		model = (DefaultTreeModel) tree.getModel();
		editor = new DTreeCellEditor(this);
		editor.setValidator(this);
		editor.addCellEditorListener(this);
		tree.setUI(new DTreeUI(this));
		tree.setCellEditor(editor);
		tree.setInvokesStopCellEditing(true);
		tree.setBackground(MainEditor.SIDEBAR_COLOR);
		tree.setRowHeight(ITEM_HEIGHT);
		((DTreeUI) tree.getUI()).setLeftChildIndent(7);
		((DTreeUI) tree.getUI()).setRightChildIndent(8);
		
		tree.addTreeSelectionListener(this);
		tree.addKeyListener(this);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
//		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setEditable(true);
		tree.setToggleClickCount(0);
		tree.setExpandsSelectedPaths(true);
		
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new DTreeTransferHandler(folderModel, this));
		
		scroller = UI.createScrollPane(tree);
		scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		miniButtonBar = StatusBar.createStatusBar();
		
		newItemButton = createButton(newItemEnabled, newItemDisabled, newItemPressed);
		removeItemButton = createButton(removeItemEnabled, removeItemDisabled, removeItemPressed);
		newFolderButton = createButton(newFolderEnabled, newFolderDisabled, newFolderPressed);
//		searchButton = createButton(searchEnabled, searchDisabled);

		miniButtonBar.add(newItemButton);
		miniButtonBar.add(removeItemButton);
		miniButtonBar.add(newFolderButton);
//		miniButtonBar.add(searchButton);
		
		add(scroller, BorderLayout.CENTER);
		add(miniButtonBar, BorderLayout.SOUTH);

		setOpaque(true);
		setBorder(null);

		listeners = new ArrayList<DTreeSelectionListener>();
		selectedNodes = new ArrayList<DefaultMutableTreeNode>();
		selectionState = new DTreeSelectionState();
		selectionState.nodes = selectedNodes;
	}

	public void loadRoot(DefaultMutableTreeNode top)
	{
		model.setRoot(top);
		root = top;

		tree.expandPath(new TreePath(root.getPath()));
		
		renderer = new DTreeCellRenderer();
		tree.setCellRenderer(renderer);
		
		tree.setSelectionPath(new TreePath(model.getPathToRoot(root)));
		
		refreshDisplay();
	}
	
	public void refreshDisplay()
	{
		revalidate();
		repaint();
		
		TreeSelectionModel model = tree.getSelectionModel();
		((AbstractLayoutCache) model.getRowMapper()).invalidateSizes();
	    tree.treeDidChange();
	}
	
	public void valueChanged(TreeSelectionEvent e)
	{
		selectedNodes.clear();
		
		if(tree.getSelectionPaths() != null)
		{
			TreePath[] paths = tree.getSelectionPaths();
			int[] rows = new int[paths.length];
			for(int i = 0; i < paths.length; ++i)
			{
				rows[i] = tree.getRowForPath(paths[i]);
				if(rows[i] == -1)
					selectedNodes.add((DefaultMutableTreeNode) paths[i].getLastPathComponent());
			}
			
			Arrays.sort(rows);
			
			for(int row : rows)
			{
				if(row == -1)
					continue;
				
				selectedNodes.add((DefaultMutableTreeNode) tree.getPathForRow(row).getLastPathComponent());
			}
		}
		
		selectedNodes.remove(root);
		if(selectedNodes.size() == 0)
			selectedNodes.add(root);
		
		int folderCounter = 0;
		int itemCounter = 0;
		
		for(DefaultMutableTreeNode node : selectedNodes)
		{
			if(node.getUserObject() instanceof Folder)
				++folderCounter;
			else
				++itemCounter;
		}
		
		if(folderCounter > 0 && itemCounter > 0)
			selectionState.type = SelectionType.MIX;
		else if(folderCounter > 0)
			selectionState.type = SelectionType.FOLDERS;
		else
			selectionState.type = SelectionType.ITEMS;
		
		for (DTreeSelectionListener l : listeners)
		{
			l.selectionStateChanged();
		}

		if (nodeCreator != null)
		{
			newItemButton.setEnabled(nodeCreator.isNodeCreationEnabled());
			removeItemButton.setEnabled(nodeCreator.isRemovalEnabled());
			newFolderButton.setEnabled(nodeCreator.isFolderCreationEnabled());
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newItemButton || e.getSource() == newFolderButton)
		{
			createNewItem(e.getSource() == newFolderButton);
		}
		else if(e.getSource() == removeItemButton)
		{
			removeSelected();
		}
		else if(e.getSource() == searchButton)
		{
			//later
		}
	}
	
	private void createNewItem(boolean makefolder)
	{
		Folder newNodeFolder;
		int insertPosition;
		
		if(selectionState.type == SelectionType.FOLDERS)
		{
			newNodeFolder = (Folder) selectedNodes.get(selectedNodes.size() - 1).getUserObject();
			insertPosition = newNodeFolder.getItems().size();
		}
		else
		{
			newNodeFolder = ((DataItem) selectedNodes.get(selectedNodes.size() - 1).getUserObject()).getParent();
			insertPosition = newNodeFolder.getItems().indexOf(selectedNodes.get(selectedNodes.size() - 1).getUserObject()) + 1;
		}
		
		Object newNodeObject;
		
		if (nodeCreator == null)
			return;
		
		String newName = makefolder? "New Folder " : "New Item "; 
		int i = 1;
		
		while(!nodeCreator.canCreate(newName + i, newNodeFolder))
			++i;
		newName = newName + i;
		
		newNodeObject = makefolder ? nodeCreator.createFolder(newName) : nodeCreator.createNode(newName);
		
		folderModel.addItem((DataItem) newNodeObject, newNodeFolder, insertPosition);
		
		editor.allowEdit();
		TreePath path = new TreePath(model.getPathToRoot(folderModel.getRecentlyCreated()));
		
		System.out.println("Set Selection Path");
		
		tree.setSelectionPath(path);
		
		System.out.println();
		System.out.println(((DataItem) folderModel.getRecentlyCreated().getUserObject()).getName());
		System.out.println(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
		System.out.println(selectedNodes.toString());
		System.out.println();
		
		tree.startEditingAtPath(path);
		editor.clearText();
	}
	
	private void removeSelected()
	{
		DefaultMutableTreeNode reselectNode;
		@SuppressWarnings("unchecked")
		ArrayList<DefaultMutableTreeNode> toRemoveList = (ArrayList<DefaultMutableTreeNode>) selectedNodes.clone();
		
		reselectNode = selectedNodes.get(selectedNodes.size() - 1).getNextSibling();
		if (reselectNode == null)
			reselectNode = selectedNodes.get(selectedNodes.size() - 1).getPreviousSibling();
		if (reselectNode == null)
			reselectNode = (DefaultMutableTreeNode) selectedNodes.get(selectedNodes.size() - 1).getParent();
		
		for(DefaultMutableTreeNode toRemoveNode : toRemoveList)
		{
			DataItem toRemove = (DataItem) toRemoveNode.getUserObject();
			Folder parent = (Folder) ((DefaultMutableTreeNode) toRemoveNode.getParent()).getUserObject();

			folderModel.removeItem(toRemove, parent);
		}

		if (reselectNode != null)
			tree.setSelectionPath(new TreePath(model.getPathToRoot(reselectNode)));
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			if(!listEditEnabled)
				return;
			
			removeSelected();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(!listEditEnabled)
				return;
			
			if(selectedNodes.size() != 1 || selectedNodes.get(0) == root)
				return;
			
			editor.allowEdit();
			TreePath path = new TreePath(model.getPathToRoot(selectedNodes.get(0)));
			tree.setSelectionPath(path);
			tree.startEditingAtPath(path);
			editor.selectText();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	public void addTreeListener(DTreeSelectionListener l)
	{
		l.setSelectionState(selectionState);
		listeners.add(l);
	}

	public void setNodeCreator(DTreeNodeCreator nodeCreator)
	{
		this.nodeCreator = nodeCreator;
		
		if (nodeCreator != null)
		{
			nodeCreator.setSelectionState(selectionState);
			newItemButton.setEnabled(nodeCreator.isNodeCreationEnabled());
			removeItemButton.setEnabled(nodeCreator.isRemovalEnabled());
			newFolderButton.setEnabled(nodeCreator.isFolderCreationEnabled());
		}
	}

//	public DataItem getSelected()
//	{
//		if (selected == null)
//			return null;
//
//		return (DataItem) selected.getUserObject();
//	}

	public void setListEditEnabled(Boolean value)
	{
		tree.setEditable(value);

		listEditEnabled = value;

		if (value)
		{
			newItemButton.addActionListener(this);
			removeItemButton.addActionListener(this);
			newFolderButton.addActionListener(this);
//				searchButton.addActionListener(this);
		}
		else
		{
			newItemButton.removeActionListener(this);
			removeItemButton.removeActionListener(this);
			newFolderButton.removeActionListener(this);
//				searchButton.removeActionListener(this);
			
			newItemButton.setEnabled(false);
			removeItemButton.setEnabled(false);
			newFolderButton.setEnabled(false);
//				searchButton.setEnabled(false);
		}
	}
	
	public DefaultMutableTreeNode getRoot()
	{
		return root;
	}

	public DefaultTreeModel getModel()
	{
		return model;
	}

	public JTree getTree()
	{
		return tree;
	}
	
	public JScrollPane getScroller()
	{
		return scroller;
	}
	
	public DTreeSelectionState getSelectionState()
	{
		return selectionState;
	}

	public JButton createButton(ImageIcon enabled, ImageIcon disabled, ImageIcon pressed)
	{
		JButton button = new JButton()
		{
			public void paintComponent(Graphics g)
			{
				if (((DefaultButtonModel) getModel()).isPressed())
				{
					Graphics2D g2d = (Graphics2D) g;
					
					GradientPaint gp = new GradientPaint(0, -1, new Color(0x1e1e1e), 0, getHeight() - 1, new Color(0x272727));
			        g2d.setPaint(gp);
			        g2d.fillRect(0, 0, getWidth(), getHeight());
				}
				
				super.paintComponent(g);
			}
		};

		button.setIcon(enabled);
		button.setDisabledIcon(disabled);
		button.setPressedIcon(pressed);

		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2d2d2d)));

		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setVerticalAlignment(SwingConstants.CENTER);

		button.setMinimumSize(new Dimension(MINI_BUTTON_WIDTH,
				MINI_BUTTON_HEIGHT));
		button.setMaximumSize(new Dimension(MINI_BUTTON_WIDTH,
				MINI_BUTTON_HEIGHT));
		button.setPreferredSize(new Dimension(MINI_BUTTON_WIDTH,
				MINI_BUTTON_HEIGHT));

		return button;
	}

	@Override
	public void editingCanceled(ChangeEvent e)
	{
	}

	@Override
	public void editingStopped(ChangeEvent e)
	{
//		String oldName = "" + ((DTreeCellEditor) e.getSource()).getCellEditorPreviousTextValue();
//		String newName = "" + ((DTreeCellEditor) e.getSource()).getCellEditorTextValue();
//		Folder f = (Folder)((DefaultMutableTreeNode) selected.getParent()).getUserObject();
	}

	@Override
	public boolean validate(String newName)
	{
		return folderModel.canCreate(newName, (Folder)((DefaultMutableTreeNode) selectedNodes.get(0).getParent()).getUserObject());
	}
}
