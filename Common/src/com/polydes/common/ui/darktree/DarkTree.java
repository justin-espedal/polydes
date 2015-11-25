package com.polydes.common.ui.darktree;

import static com.polydes.common.util.Lang.asArray;
import static com.polydes.common.util.Lang.newarray;

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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
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
import javax.swing.tree.TreePath;

import com.polydes.common.comp.StatusBar;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeUtils;
import com.polydes.common.res.ResourceLoader;
import com.polydes.common.util.PopupUtil;

import stencyl.sw.util.UI;

public class DarkTree<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel
	implements TreeSelectionListener, CellEditorListener, CellEditValidator,
				ActionListener, KeyListener, MouseListener
{
	public static final int DEF_WIDTH = 200;
	public static final int MINI_BUTTON_WIDTH = 25;
	public static final int MINI_BUTTON_HEIGHT = 21;
	public static final int ITEM_HEIGHT = 20;
	public static final Color TREE_COLOR = new Color(62, 62, 62);
	
	private JTree tree;
	private DTreeModel<T,U> treeModel;
	private U root;
	private HierarchyModel<T,U> folderModel;
	
	private DTreeCellRenderer<T,U> renderer;
	private DTreeCellEditor<T,U> editor;
	private NodeSelection<T,U> selection;

	private boolean nameEditingAllowed = true;
	
	private JScrollPane scroller;
	
	private JPanel miniButtonBar;
	private JButton newItemButton;
	private JButton removeItemButton;
	private JButton propertiesButton;
	
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
		
		this.folderModel = folderModel;
		treeModel = new DTreeModel<>(folderModel);
		root = folderModel.getRootBranch();
		tree = new JTree(treeModel)
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
				return ((T) value).getName();
			}
		};
		
		tree.expandPath(treeModel.getPath(root));
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
		
		tree.addKeyListener(this);
		tree.addMouseListener(this);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
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
		
		listEditEnabled = false;
		
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
		
		selection = folderModel.getSelection();
		tree.setSelectionModel(new DTreeSelectionModel<>(folderModel));
		tree.addTreeSelectionListener(this);
		tree.setSelectionPath(treeModel.getPath(root));
		
		refreshDisplay();
	}
	
	public void dispose()
	{
		removeAll();
		
		treeModel.dispose();
		tree.removeTreeSelectionListener(this);
		tree.removeKeyListener(this);
		transferHandler.dispose();
		tree.setTransferHandler(null);
		
		editor = null;
		renderer = null;
		selection = null;
		transferHandler = null;
		folderModel = null;
		root = null;
		treeModel = null;
		tree = null;
	}
	
	public void refreshDisplay()
	{
		revalidate();
		repaint();
		
//		TreeSelectionModel model = tree.getSelectionModel();
//		((AbstractLayoutCache) model.getRowMapper()).invalidateSizes();
	    tree.treeDidChange();
	}
	
	public void expand(U branch)
	{
		tree.expandPath(treeModel.getPath(branch));
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
				tree.expandPath(treeModel.getPath(item));
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
	
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		U newNodeFolder = folderModel.getCreationParentFolder(selection);
		if(newNodeFolder == null)
			return;
		newItemButton.setEnabled(newNodeFolder.isFolderCreationEnabled() || newNodeFolder.isItemCreationEnabled());
		removeItemButton.setEnabled(newNodeFolder.isItemRemovalEnabled());
		propertiesButton.setEnabled(newNodeFolder.isItemEditingEnabled() && selection.size() == 1 && selection.firstNode() != root);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newItemButton)
		{
			U newNodeFolder = folderModel.getCreationParentFolder(selection);
			ArrayList<CreatableNodeInfo> items = folderModel.getCreatableNodes(newNodeFolder);
			
			if(items.size() == 1)
				folderModel.createNewItem(items.get(0));
			else
			{
				JPopupMenu menu = PopupUtil.buildPopup(PopupUtil.asMenuItems(items));
				PopupUtil.installListener(menu, (item) -> {
					folderModel.createNewItem((CreatableNodeInfo) item);
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
			folderModel.editItem(selection.firstNode());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeSelected()
	{
		T reselectNode;
		ArrayList<T> toRemoveList = selection.copyList();
		
		reselectNode = NodeUtils.getNextSibling(selection.lastNode());
		if (reselectNode == null)
			reselectNode = NodeUtils.getPreviousSibling(selection.lastNode());
		if (reselectNode == null)
			reselectNode = (T) selection.lastNode().getParent();
		
		NodeUtils.includeDescendants(toRemoveList);
		NodeUtils.depthSort(toRemoveList);
		
		if(folderModel.getNodeCreator().attemptRemove(toRemoveList))
		{
			selection.removeAll(asArray(toRemoveList, folderModel.leafClass));
			
			for(T toRemove : toRemoveList)
			{
				U parent = (U) toRemove.getParent();
				
				folderModel.removeItem(toRemove, parent);
				folderModel.getNodeCreator().nodeRemoved(toRemove);
			}
			
			if(reselectNode != null)
				tree.setSelectionPath(treeModel.getPath(reselectNode));
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
			
			if(selection.size() != 1 || selection.firstNode() == root)
				return;
			
			if(!selection.firstNode().canEditName())
				return;
			
			editor.allowEdit();
			TreePath path = treeModel.getPath(selection.firstNode());
			tree.startEditingAtPath(path);
			editor.selectText();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}
	
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
	
	public U getRoot()
	{
		return root;
	}
	
	public TreePath getRootPath()
	{
		return treeModel.getPath(root);
	}

	public DTreeModel<T,U> getModel()
	{
		return treeModel;
	}

	public JTree getTree()
	{
		return tree;
	}
	
	public HierarchyModel<T, U> getFolderModel()
	{
		return folderModel;
	}
	
	public JScrollPane getScroller()
	{
		return scroller;
	}
	
	public NodeSelection<T,U> getSelectionState()
	{
		return selection;
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
		if(selection.isEmpty() || selection.firstNode() == root)
			return false;
		
		return selection.firstNode().getParent().canCreateItemWithName(newName);
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

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}
	
	private boolean eventIsOverSelection(MouseEvent e)
	{
		TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
		if(path != null)
			return tree.isPathSelected(path);
		return false;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}
	
	@SuppressWarnings("unchecked")
	private void maybeShowPopup(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			boolean selectionTargeted = eventIsOverSelection(e);
			
			if(!selectionTargeted)
			{
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(path);
				if(path != null)
					selectionTargeted = true;
			}
			
			boolean singleFolderTargeted = !selectionTargeted ||
				(selection.size() == 1 && selection.firstNode() instanceof Branch);
			
			T[] targets = selectionTargeted ?
				asArray(selection.copyList(), folderModel.leafClass) :
				newarray(folderModel.leafClass, (T) root);
			
			ArrayList<JMenuItem> menuItems = new ArrayList<>();
			
			if(singleFolderTargeted)
			{
				ArrayList<CreatableNodeInfo> createItems = folderModel.getCreatableNodes((U) selection.firstNode());
				menuItems.add(PopupUtil.menu("Create", PopupUtil.asMenuItems(createItems)));
			}
			if(selectionTargeted)
			{
				ArrayList<NodeAction<T>> actionItems = folderModel.getNodeActions(targets);
				menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
			}
			
			JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
			
			PopupUtil.installListener(popup, (item) -> {
				
				if(item instanceof NodeAction)
					for(T target : targets)
						((NodeAction<T>) item).callback.accept(target);
				else if(item instanceof CreatableNodeInfo)
					folderModel.createNewItem((CreatableNodeInfo) item);
				
			});
			
			Point p = getMousePosition(true);
			if(p == null)
			{
				p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, this);
			}
			popup.show(this, p.x, p.y);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
}
