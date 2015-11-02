package com.polydes.common.ui.darktree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.common.comp.StatusBar;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeUtils;
import com.polydes.common.res.ResourceLoader;
import com.polydes.common.util.PopupUtil;
import com.polydes.common.util.PopupUtil.PopupItem;
import com.polydes.common.util.PopupUtil.PopupSelectionListener;

import stencyl.sw.util.UI;

public class DarkTree<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel implements TreeSelectionListener, ActionListener, KeyListener,
	CellEditorListener, CellEditValidator, HierarchyRepresentation<T,U>
{
	public static final int DEF_WIDTH = 200;
	public static final int MINI_BUTTON_WIDTH = 25;
	public static final int MINI_BUTTON_HEIGHT = 21;
	public static final int ITEM_HEIGHT = 20;
	public static final Color TREE_COLOR = new Color(62, 62, 62);
	
	private JTree tree;
	private DefaultTreeModel model;
	private TNode<T,U> root;
	private ArrayList<TNode<T,U>> selectedNodes;
	private HierarchyModel<T,U> folderModel;
	private HashMap<T, TNode<T,U>> nodeMap;
	
	private DTreeNodeCreator<T,U> nodeCreator;
	private DTreeCellRenderer<T,U> renderer;
	private DTreeCellEditor<T,U> editor;
	private DTreeSelectionState<T,U> selectionState;

	private boolean nameEditingAllowed = true;
	
	private ArrayList<DTreeSelectionListener<T,U>> listeners;
	
	private JScrollPane scroller;
	
	private JPanel miniButtonBar;
	private JButton newItemButton;
	private JButton removeItemButton;
	private JButton propertiesButton;
	
	private static ImageIcon folderIcon = ResourceLoader.loadIcon("tree/folder-enabled.png");
	
	private static ImageIcon newItemEnabled = ResourceLoader.loadIcon("tree/plus-enabled.png");
	private static ImageIcon newItemDisabled = ResourceLoader.loadIcon("tree/plus-disabled.png");
	private static ImageIcon newItemPressed = ResourceLoader.loadIcon("tree/plus-pressed.png");
	
	private static ImageIcon removeItemEnabled = ResourceLoader.loadIcon("tree/minus-enabled.png");
	private static ImageIcon removeItemDisabled = ResourceLoader.loadIcon("tree/minus-disabled.png");
	private static ImageIcon removeItemPressed = ResourceLoader.loadIcon("tree/minus-pressed.png");

	private static ImageIcon propertiesEnabled = ResourceLoader.loadIcon("tree/properties-enabled.png");
	private static ImageIcon propertiesDisabled = ResourceLoader.loadIcon("tree/properties-disabled.png");
	private static ImageIcon propertiesPressed = ResourceLoader.loadIcon("tree/properties-pressed.png");
	
	private boolean listEditEnabled;
	private DTreeTransferHandler<T,U> transferHandler;
	
	public DarkTree(HierarchyModel<T,U> folderModel)
	{
		super(new BorderLayout());
		
		folderModel.addRepresentation(this);
		
		this.folderModel = folderModel;
		nodeMap = new HashMap<T, TNode<T,U>>();
		
		listEditEnabled = false;
		
		root = createNodeFromFolder(folderModel.getRootBranch());
		tree = new JTree(root)
		{
			@Override
		    public Dimension getPreferredScrollableViewportSize()
		    {
				return getPreferredSize();
		    }
			
			@SuppressWarnings("unchecked")
			@Override
			public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				TNode<T,U> node = (TNode<T,U>) value;
				return DarkTree.this.convertValueToText(node.getUserObject());
			}
		};
		model = (DefaultTreeModel) tree.getModel();
		tree.expandPath(new TreePath(root.getPath()));
		editor = new DTreeCellEditor<T,U>(this);
		editor.setValidator(this);
		editor.addCellEditorListener(this);
		renderer = new DTreeCellRenderer<T,U>();
		tree.setUI(new DTreeUI(this));
		tree.setCellEditor(editor);
		tree.setCellRenderer(renderer);
		tree.setInvokesStopCellEditing(true);
		tree.setBackground(TREE_COLOR);
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
		tree.setTransferHandler(transferHandler = new DTreeTransferHandler<T,U>(folderModel, this));
		
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
		
		listeners = new ArrayList<DTreeSelectionListener<T,U>>();
		selectedNodes = new ArrayList<TNode<T,U>>();
		selectionState = new DTreeSelectionState<T,U>();
		selectionState.nodes = selectedNodes;
		
		tree.setSelectionPath(new TreePath(model.getPathToRoot(root)));
		refreshDisplay();
	}
	
	public String convertValueToText(T item)
	{
		return item.getName();
	}
	
	public void dispose()
	{
		removeAll();
		listeners.clear();
		nodeMap.clear();
		selectedNodes.clear();
		
		folderModel.removeRepresentation(this);
		tree.removeTreeSelectionListener(this);
		tree.removeKeyListener(this);
		model.setRoot(null);
		transferHandler.dispose();
		tree.setTransferHandler(null);
		
		editor = null;
		renderer = null;
		nodeCreator = null;
		selectionState = null;
		recentlyCreated = null;
		transferHandler = null;
		folderModel = null;
		root = null;
		model = null;
		tree = null;
	}
	
	public void setNode(T item, TNode<T,U> node)
	{
		nodeMap.put(item, node);
	}
	
	public TNode<T,U> getNode(T item)
	{
		return nodeMap.get(item);
	}
	
	public void removeNode(T item)
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
	
	@SuppressWarnings("unchecked")
	public void expand(U branch)
	{
		tree.expandPath(new TreePath(getNode((T) branch).getPath()));
	}
	
	public void expandLevel(int level)
	{
		expandLevel(level, folderModel.getRootBranch());
	}
	
	@SuppressWarnings("unchecked")
	private void expandLevel(int level, U branch)
	{
		for(T item : branch.getItems())
		{
			if(item instanceof Branch)
			{
				tree.expandPath(new TreePath(getNode(item).getPath()));
				if(level > 0)
					expandLevel(level - 1, (U) item);
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
	
	@SuppressWarnings("unchecked")
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
					selectedNodes.add((TNode<T,U>) paths[i].getLastPathComponent());
			}
			
			Arrays.sort(rows);
			
			for(int row : rows)
			{
				if(row == -1)
					continue;
				
				selectedNodes.add((TNode<T,U>) tree.getPathForRow(row).getLastPathComponent());
			}
		}
		
		selectedNodes.remove(root);
		if(selectedNodes.size() == 0)
			selectedNodes.add(root);
		
		int folderCounter = 0;
		int itemCounter = 0;
		
		for(TNode<T,U> node : selectedNodes)
		{
			if(node.getUserObject() instanceof Branch)
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
		
		for (DTreeSelectionListener<T,U> l : listeners)
			l.selectionStateChanged();
		
		U newNodeFolder = getCreationParentFolder(selectionState);
		newItemButton.setEnabled(newNodeFolder.isFolderCreationEnabled() || newNodeFolder.isItemCreationEnabled());
		removeItemButton.setEnabled(newNodeFolder.isItemRemovalEnabled());
		propertiesButton.setEnabled(newNodeFolder.isItemEditingEnabled() && selectedNodes.size() == 1 && selectedNodes.get(0) != root);
	}
	
	@SuppressWarnings("unchecked")
	public U getCreationParentFolder(DTreeSelectionState<T,U> state)
	{
		if(selectionState.type == SelectionType.FOLDERS)
			return (U) selectedNodes.get(selectedNodes.size() - 1).getUserObject();
		else
			return (U) selectedNodes.get(selectedNodes.size() - 1).getUserObject().getParent();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newItemButton)
		{
			U newNodeFolder = getCreationParentFolder(selectionState);
			
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
				if(p == null)
				{
					p = MouseInfo.getPointerInfo().getLocation();
					SwingUtilities.convertPointFromScreen(p, this);
				}
				menu.show(this, p.x, p.y);
			}
		}
		else if(e.getSource() == removeItemButton)
		{
			removeSelected();
		}
		else if(e.getSource() == propertiesButton)
		{
			nodeCreator.editNode(selectionState.nodes.get(0).getUserObject());
		}
	}
	
	public void createNewItem(PopupItem item)
	{
		U newNodeFolder = getCreationParentFolder(selectionState);
		
		int insertPosition;
		
		if(selectionState.type == SelectionType.FOLDERS)
			insertPosition = newNodeFolder.getItems().size();
		else
			insertPosition = newNodeFolder.getItems().indexOf(selectedNodes.get(selectedNodes.size() - 1).getUserObject()) + 1;
		
		createNewItemFromFolder(item, newNodeFolder, insertPosition);
	}
	
	public void createNewItemFromFolder(PopupItem item, U newNodeFolder, int insertPosition)
	{
		T newNodeObject;
		
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
		
		folderModel.addItem(newNodeObject, newNodeFolder, insertPosition);
		
		TreePath path = new TreePath(model.getPathToRoot(recentlyCreated));
		tree.setSelectionPath(path);
		recentlyCreated = null;
		
		if(nameEditingAllowed && newNodeObject.canEditName())
		{
			editor.allowEdit();
			tree.startEditingAtPath(path);
			editor.clearText();
		}
	}
	
	private void removeSelected()
	{
		TNode<T,U> reselectNode;
		@SuppressWarnings("unchecked")
		ArrayList<TNode<T,U>> toRemoveList = (ArrayList<TNode<T,U>>) selectedNodes.clone();
		
		reselectNode = selectedNodes.get(selectedNodes.size() - 1).getNextSibling();
		if (reselectNode == null)
			reselectNode = selectedNodes.get(selectedNodes.size() - 1).getPreviousSibling();
		if (reselectNode == null)
			reselectNode = selectedNodes.get(selectedNodes.size() - 1).getParent();
		
		//Remove any objects that are under parents that will be deleted.
		final HashSet<T> toRemoveDiSet = new HashSet<T>();
		for(TNode<T,U> toRemoveNode : toRemoveList)
			NodeUtils.recursiveRun(toRemoveNode.getUserObject(), (T item) -> toRemoveDiSet.add(item));
		
		//Sort it so that the deepest items in hierarchy are removed one by one before their parents.
		ArrayList<T> toRemoveDiList = new ArrayList<>(toRemoveDiSet);
		Collections.sort(toRemoveDiList, (a, b) -> Integer.compare(NodeUtils.getDepth(b), NodeUtils.getDepth(a)));
		
		if(nodeCreator.attemptRemove(new ArrayList<T>(toRemoveDiList)))
		{
			for(T toRemove : toRemoveDiList)
			{
				U parent = (U) toRemove.getParent();
				
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
			
			if(!selectedNodes.get(0).getUserObject().canEditName())
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

	public void addTreeListener(DTreeSelectionListener<T,U> l)
	{
		l.setSelectionState(selectionState);
		listeners.add(l);
	}
	
	public void removeTreeListener(DTreeSelectionListener<T,U> l)
	{
		listeners.remove(l);
	}

	public void setNodeCreator(DTreeNodeCreator<T,U> nodeCreator)
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
	
	public void disableButtonBar()
	{
		remove(miniButtonBar);
	}
	
	public TNode<T,U> getRoot()
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
	
	public DTreeSelectionState<T,U> getSelectionState()
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
		return selectedNodes.get(0).getUserObject().getParent().canCreateItemWithName(newName);
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
	
	private TNode<T,U> recentlyCreated = null;
	
	@Override
	public void leafNameChanged(T item, String oldName)
	{
		model.nodeChanged(getNode(item));
		repaint();
	}
	
	@Override
	public void leafStateChanged(T item)
	{
		model.nodeChanged(getNode(item));
		repaint();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void itemAdded(U folder, T item, int position)
	{
		TNode<T,U> itemNode;
		if(folderModel.isMovingItem())
			itemNode = getNode(item);
		else
		{
			itemNode = new TNode<T,U>(item);
			recentlyCreated = itemNode;
			setNode(item, itemNode);
		}
		model.insertNodeInto(itemNode, getNode((T) folder), position);
	}
	
	@Override
	public void itemRemoved(U folder, T item, int position)
	{
		model.removeNodeFromParent(getNode(item));
		if(!folderModel.isMovingItem())
			removeNode(item);
	}
	
	@SuppressWarnings("unchecked")
	private TNode<T,U> createNodeFromFolder(U folder)
	{
		TNode<T,U> newNode = new TNode<T,U>((T) folder);
		TNode<T,U> newSubNode;
		setNode((T) folder, newNode);
		
		for(T item : folder.getItems())
		{
			if(item instanceof Branch)
				newNode.add(createNodeFromFolder((U) item));
			else
			{
				newSubNode = new TNode<T,U>(item);
				setNode(item, newSubNode);
				newNode.add(newSubNode);
			}
		}
		
		return newNode;
	}
}
