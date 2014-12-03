package stencyl.ext.polydes.datastruct.ui.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.DataItemUtil;
import stencyl.ext.polydes.datastruct.data.folder.DataItemUtil.DataItemRunnable;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyRepresentation;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.ui.StatusBar;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupSelectionListener;
import stencyl.sw.util.UI;

//Short for "Dialog" Tree. Just because. ...But it could also mean "DataItem Tree".
public class DTree extends JPanel implements TreeSelectionListener, ActionListener, KeyListener,
	CellEditorListener, CellEditValidator, FolderHierarchyRepresentation
{
	public static final int DEF_WIDTH = 200;
	public static final int MINI_BUTTON_WIDTH = 25;
	public static final int MINI_BUTTON_HEIGHT = 21;
	public static final int ITEM_HEIGHT = 20;
	
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private ArrayList<DefaultMutableTreeNode> selectedNodes;
	private FolderHierarchyModel folderModel;
	private HashMap<DataItem, DefaultMutableTreeNode> nodeMap;
	
	private DTreeNodeCreator nodeCreator;
	private DTreeCellRenderer renderer;
	private DTreeCellEditor editor;
	private DTreeSelectionState selectionState;

	private boolean nameEditingAllowed = true;
	
	private ArrayList<DTreeSelectionListener> listeners;
	
	private JScrollPane scroller;
	
	private JPanel miniButtonBar;
	private JButton newItemButton;
	private JButton removeItemButton;
	private JButton propertiesButton;
	
	private static ImageIcon folderIcon = Resources.loadIcon("tree/folder-enabled.png");
	
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

	private static ImageIcon propertiesEnabled = Resources
			.loadIcon("tree/properties-enabled.png");
	private static ImageIcon propertiesDisabled = Resources
			.loadIcon("tree/properties-disabled.png");
	private static ImageIcon propertiesPressed = Resources
			.loadIcon("tree/properties-pressed.png");
	
	private boolean listEditEnabled;

	public DTree(FolderHierarchyModel folderModel)
	{
		super(new BorderLayout());
		
		folderModel.addRepresentation(this);
		
		this.folderModel = folderModel;
		nodeMap = new HashMap<DataItem, DefaultMutableTreeNode>();
		
		listEditEnabled = false;
		
		root = createNodeFromFolder(folderModel.getRootFolder());
		tree = new JTree(root)
		{
			@Override
		    public Dimension getPreferredScrollableViewportSize()
		    {
				return getPreferredSize();
		    }
			
			@Override
			public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				DataItem item = (DataItem) node.getUserObject();
				return item + (item.isDirty() ? "*" : "");
			}
		};
		model = (DefaultTreeModel) tree.getModel();
		tree.expandPath(new TreePath(root.getPath()));
		editor = new DTreeCellEditor(this);
		editor.setValidator(this);
		editor.addCellEditorListener(this);
		renderer = new DTreeCellRenderer();
		tree.setUI(new DTreeUI(this));
		tree.setCellEditor(editor);
		tree.setCellRenderer(renderer);
		tree.setInvokesStopCellEditing(true);
		tree.setBackground(UIConsts.SIDEBAR_COLOR);
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
		propertiesButton = createButton(propertiesEnabled, propertiesDisabled, propertiesPressed);
//		searchButton = createButton(searchEnabled, searchDisabled);
		
		
		miniButtonBar.add(newItemButton);
		miniButtonBar.add(removeItemButton);
//		miniButtonBar.add(searchButton);
		
		add(scroller, BorderLayout.CENTER);
		add(miniButtonBar, BorderLayout.SOUTH);

		setOpaque(true);
		setBorder(null);
		
		listeners = new ArrayList<DTreeSelectionListener>();
		selectedNodes = new ArrayList<DefaultMutableTreeNode>();
		selectionState = new DTreeSelectionState();
		selectionState.nodes = selectedNodes;
		
		tree.setSelectionPath(new TreePath(model.getPathToRoot(root)));
		refreshDisplay();
	}
	
	public void dispose()
	{
		editor = null;
		renderer = null;
		listeners.clear();
		nodeCreator = null;
		nodeMap.clear();
		root = null;
		selectedNodes.clear();
		selectionState = null;
		recentlyCreated = null;
		
		folderModel.removeRepresentation(this);
		tree.removeTreeSelectionListener(this);
		tree.removeKeyListener(this);
		model.setRoot(null);
	}
	
	public void setNode(DataItem item, DefaultMutableTreeNode node)
	{
		nodeMap.put(item, node);
	}
	
	public DefaultMutableTreeNode getNode(DataItem item)
	{
		return nodeMap.get(item);
	}
	
	public void removeNode(DataItem item)
	{
		nodeMap.remove(item);
	}
	
	public void refreshDisplay()
	{
		revalidate();
		repaint();
		
		TreeSelectionModel model = tree.getSelectionModel();
		((AbstractLayoutCache) model.getRowMapper()).invalidateSizes();
	    tree.treeDidChange();
	}
	
	public void expand(Folder folder)
	{
		tree.expandPath(new TreePath(getNode(folder).getPath()));
	}
	
	public void expandLevel(int level)
	{
		expandLevel(level, folderModel.getRootFolder());
	}
	
	private void expandLevel(int level, Folder folder)
	{
		for(DataItem item : folder.getItems())
		{
			if(item instanceof Folder)
			{
				tree.expandPath(new TreePath(getNode(item).getPath()));
				if(level > 0)
					expandLevel(level - 1, (Folder) item);
			}
		}
	}
	
	public void setNamingEditingAllowed(boolean value)
	{
		nameEditingAllowed = value;
	}
	
	public boolean isNameEditingAllowed()
	{
		return nameEditingAllowed;
	}
	
	@Override
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
			l.selectionStateChanged();
		
		Folder newNodeFolder = getCreationParentFolder(selectionState);
		newItemButton.setEnabled(newNodeFolder.isFolderCreationEnabled() || newNodeFolder.isItemCreationEnabled());
		removeItemButton.setEnabled(newNodeFolder.isItemRemovalEnabled());
		propertiesButton.setEnabled(newNodeFolder.isItemEditingEnabled() && selectedNodes.size() == 1 && selectedNodes.get(0) != root);
	}
	
	public Folder getCreationParentFolder(DTreeSelectionState state)
	{
		if(selectionState.type == SelectionType.FOLDERS)
			return (Folder) selectedNodes.get(selectedNodes.size() - 1).getUserObject();
		else
			return ((DataItem) selectedNodes.get(selectedNodes.size() - 1).getUserObject()).getParent();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newItemButton)
		{
			Folder newNodeFolder = getCreationParentFolder(selectionState);
			
			ArrayList<PopupItem> items = new ArrayList<PopupItem>();
			if(newNodeFolder.isFolderCreationEnabled())
				items.add(new PopupItem("Folder", null, folderIcon));
			items.addAll(nodeCreator.getCreatableNodeList());
			
			if(items.size() == 1)
				createNewItem(items.get(0));
			else
			{
				JPopupMenu menu = PopupUtil.buildPopup(items, new PopupSelectionListener()
				{
					@Override
					public void itemSelected(PopupItem item)
					{
						createNewItem(item);
					}
				});
				Point p = getMousePosition(true);
				menu.show(this, p.x, p.y);
			}
		}
		else if(e.getSource() == removeItemButton)
		{
			removeSelected();
		}
		else if(e.getSource() == propertiesButton)
		{
			nodeCreator.editNode((DataItem) selectionState.nodes.get(0).getUserObject());
		}
	}
	
	public void createNewItem(PopupItem item)
	{
		Folder newNodeFolder = getCreationParentFolder(selectionState);
		
		int insertPosition;
		
		if(selectionState.type == SelectionType.FOLDERS)
			insertPosition = newNodeFolder.getItems().size();
		else
			insertPosition = newNodeFolder.getItems().indexOf(selectedNodes.get(selectedNodes.size() - 1).getUserObject()) + 1;
		
		createNewItemFromFolder(item, newNodeFolder, insertPosition);
	}
	
	public void createNewItemFromFolder(PopupItem item, Folder newNodeFolder, int insertPosition)
	{
		Object newNodeObject;
		
		if (nodeCreator == null)
			return;
		
		String newName = "New " + item.text + " "; 
		int i = 1;
		
		while(!newNodeFolder.canCreateItemWithName(newName + i))
			++i;
		newName = newName + i;
		
		newNodeObject = nodeCreator.createNode(item, newName);
		if(newNodeObject == null)
			return;
		
		folderModel.addItem((DataItem) newNodeObject, newNodeFolder, insertPosition);
		
		TreePath path = new TreePath(model.getPathToRoot(recentlyCreated));
		tree.setSelectionPath(path);
		recentlyCreated = null;
		
		if(nameEditingAllowed && ((DataItem) newNodeObject).canEditName())
		{
			editor.allowEdit();
			tree.startEditingAtPath(path);
			editor.clearText();
		}
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
		
		final HashSet<DataItem> toRemoveDiList = new HashSet<DataItem>();
		for(DefaultMutableTreeNode toRemoveNode : toRemoveList)
		{
			DataItemUtil.recursiveRun((DataItem) toRemoveNode.getUserObject(), new DataItemRunnable()
			{
				@Override
				public void run(DataItem item)
				{
					toRemoveDiList.add(item);
				}
			});
		}
		
		if(nodeCreator.attemptRemove(new ArrayList<DataItem>(toRemoveDiList)))
		{
			for(DataItem toRemove : toRemoveDiList)
			{
				Folder parent = toRemove.getParent();
				
				folderModel.removeItem(toRemove, parent);
				nodeCreator.nodeRemoved(toRemove);
			}
			
			if(reselectNode != null)
				tree.setSelectionPath(new TreePath(model.getPathToRoot(reselectNode)));
		}
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
			
			if(!((DataItem) selectedNodes.get(0).getUserObject()).canEditName())
				return;
			
			editor.allowEdit();
			TreePath path = new TreePath(model.getPathToRoot(selectedNodes.get(0)));
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
	
	public void removeTreeListener(DTreeSelectionListener l)
	{
		listeners.remove(l);
	}

	public void setNodeCreator(DTreeNodeCreator nodeCreator)
	{
		this.nodeCreator = nodeCreator;
		
		if (nodeCreator != null)
			nodeCreator.setSelectionState(selectionState);
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
			propertiesButton.addActionListener(this);
//				searchButton.addActionListener(this);
		}
		else
		{
			newItemButton.removeActionListener(this);
			removeItemButton.removeActionListener(this);
			propertiesButton.removeActionListener(this);
//				searchButton.removeActionListener(this);
			
			newItemButton.setEnabled(false);
			removeItemButton.setEnabled(false);
			propertiesButton.setEnabled(false);
//				searchButton.setEnabled(false);
		}
	}
	
	public void enablePropertiesButton()
	{
		miniButtonBar.add(propertiesButton);
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
			@Override
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
		return ((Folder)((DefaultMutableTreeNode) selectedNodes.get(0).getParent()).getUserObject()).canCreateItemWithName(newName);
	}

	public void forceRerender()
	{
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				refreshDisplay();
			}
		}, 10);
		
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				refreshDisplay();
			}
		}, 100);
	}
	
	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	private DefaultMutableTreeNode recentlyCreated = null;
	
	@Override
	public void dataItemNameChanged(DataItem item, String oldName)
	{
		model.nodeChanged(getNode(item));
		repaint();
	}
	
	@Override
	public void dataItemStateChanged(DataItem item)
	{
		model.nodeChanged(getNode(item));
		repaint();
	}
	
	@Override
	public void itemAdded(Folder folder, DataItem item, int position)
	{
		DefaultMutableTreeNode itemNode;
		if(folderModel.isMovingItem())
			itemNode = getNode(item);
		else
		{
			itemNode = new DefaultMutableTreeNode(item);
			recentlyCreated = itemNode;
			setNode(item, itemNode);
		}
		model.insertNodeInto(itemNode, getNode(folder), position);
	}
	
	@Override
	public void itemRemoved(DataItem item)
	{
		model.removeNodeFromParent(getNode(item));
		if(!folderModel.isMovingItem())
			removeNode(item);
	}
	
	private DefaultMutableTreeNode createNodeFromFolder(Folder folder)
	{
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(folder);
		DefaultMutableTreeNode newSubNode;
		setNode(folder, newNode);
		
		for(DataItem item : folder.getItems())
		{
			if(item instanceof Folder)
				newNode.add(createNodeFromFolder((Folder) item));
			else
			{
				newSubNode = new DefaultMutableTreeNode(item);
				setNode(item, newSubNode);
				newNode.add(newSubNode);
			}
		}
		
		return newNode;
	}
}
