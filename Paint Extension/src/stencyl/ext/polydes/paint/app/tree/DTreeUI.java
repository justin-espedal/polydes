package stencyl.ext.polydes.paint.app.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

import stencyl.ext.polydes.paint.data.Folder;
import stencyl.ext.polydes.paint.res.Resources;

public class DTreeUI extends BasicTreeUI implements MouseListener
{
	public static final Icon iconNoChildren = Resources.loadIcon("tree/circle.png");
	public static final Icon iconExpanded = Resources.loadIcon("tree/arrow-down.png");
	public static final Icon iconCollapsed = Resources.loadIcon("tree/arrow-right.png");

	public static JPanel highlighter = null;
	public static final Color highlightColor = new Color(102, 102, 102);

	private DTree dtree;

	public DTreeUI(DTree dtree)
	{
		super();

		this.dtree = dtree;

		if (highlighter == null)
		{
			highlighter = new JPanel();
			highlighter.setBackground(highlightColor);
		}
	}

	private Rectangle viewRect;
	private int treeX;
	private int treeWidth;

	@Override
	public void paint(Graphics g, JComponent c)
	{
		viewRect = dtree.getScroller().getViewport().getViewRect();
		treeX = viewRect.x;
		treeWidth = viewRect.width;

		super.paint(g, c);
	}

	@Override
	protected boolean shouldPaintExpandControl(TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return false;
	}

	private boolean _shouldPaintExpandControl(TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		Object value = path.getLastPathComponent();

		if (!(((DefaultMutableTreeNode) value).getUserObject() instanceof Folder))
			return false;
		int depth = path.getPathCount() - 1;
		if ((depth == 0 || (depth == 1 && !isRootVisible()))
				&& !getShowsRootHandles())
			return false;

		return true;
	}

	@Override
	protected void paintExpandControl(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return;
	}

	private void _paintExpandControl(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		Object value = path.getLastPathComponent();
		if (((DefaultMutableTreeNode) value).getUserObject() instanceof Folder)
		{
			Folder f = (Folder) ((DefaultMutableTreeNode) value)
					.getUserObject();

			int middleXOfKnob = bounds.x - getRightChildIndent() + 1;
			int middleYOfKnob = bounds.y + (bounds.height / 2);

			Icon icon;

			if (f.getItems().isEmpty())
				icon = iconNoChildren;
			else if (isExpanded)
				icon = iconExpanded;
			else
				icon = iconCollapsed;

			drawCentered(tree, g, icon, middleXOfKnob, middleYOfKnob);
		}
	}

	@Override
	protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
			Rectangle bounds, TreePath path, int row, boolean isExpanded,
			boolean hasBeenExpanded, boolean isLeaf)
	{
		if (tree.isRowSelected(row))
		{
			// paint highlight
			Rectangle b = getHighlightBounds(row);
			rendererPane.paintComponent(g, null, highlighter, b.x, b.y,
					b.width, b.height, false);
		}

		if (_shouldPaintExpandControl(path, row, isExpanded, hasBeenExpanded,
				isLeaf))
			_paintExpandControl(g, clipBounds, insets, bounds, path, row,
					isExpanded, hasBeenExpanded, isLeaf);

		// Don't paint the renderer if editing this row.
		if (editingComponent != null && editingRow == row)
			return;

		Component component;

		component = currentCellRenderer.getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), tree.isRowSelected(row),
				isExpanded, isLeaf, row, false);

		rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
				bounds.width, bounds.height, true);
	}

	private Rectangle getHighlightBounds(int row)
	{
		return new Rectangle(treeX, tree.getRowBounds(row).y, treeWidth, DTree.ITEM_HEIGHT);
	}

	@Override
	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return;
	}

	@Override
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, TreePath path)
	{
		return;
	}
	
	@Override
	public void installListeners()
	{
		super.installListeners();
		
		tree.addMouseListener(this);
	}
	
	@Override
	public void uninstallListeners()
	{
		super.uninstallListeners();
		
		tree.removeMouseListener(this);
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
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		int row = tree.getClosestRowForLocation(x, y);
		TreePath path = tree.getClosestPathForLocation(x, y);
		
		if(row == -1)
			return;
		
		if(tree.getRowBounds(row).contains(e.getPoint()))
			return;
		
		if(SwingUtilities.isLeftMouseButton(e))
		{
			if(isLocationInExpandControl(path, x, y))
				return;
		}
		
		boolean multiEvent = (isMultiSelectEvent(e) || isToggleSelectionEvent(e));
		
		Rectangle b = tree.getRowBounds(row);
		if(b.y > y || b.y + b.height < y)
		{
			if(multiEvent)
				return;
			else
				tree.setSelectionPath(new TreePath(new TreeNode[] {dtree.getRoot()}));
		}
		else
		{
			
			if(!startEditing(path, e))
				selectPathForEvent(path, e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
}
