package stencyl.ext.polydes.extrasmanager.app.darktree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import stencyl.ext.polydes.extrasmanager.data.folder.Branch;
import stencyl.ext.polydes.extrasmanager.data.folder.Leaf;

public class DTreeCellRenderer<T extends Leaf<T>> extends JPanel implements TreeCellRenderer
{
	private JLabel text;
	
	private static final Color selectedColor = new Color(102, 102, 102);
	
	private static final Font branchFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 11.0f);
    private static final Font leafFont = UIManager.getFont("Label.font").deriveFont(11.0f);
//	private static final Font leafSelectedFont = leafFont.deriveFont(Font.BOLD);
    
	private int itemHeight = DarkTree.ITEM_HEIGHT;
	
	public DTreeCellRenderer()
	{
		super(new BorderLayout());
		((BorderLayout) getLayout()).setHgap(4);
		setOpaque(true);
		
		text = new JLabel();
		text.setForeground(Color.WHITE);
		
		add(text, BorderLayout.CENTER);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width + 5, itemHeight);
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus)
	{
		@SuppressWarnings("unchecked")
		TNode<T> node = (TNode<T>) value;
		Leaf<T> item = node.getUserObject();
		
		text.setText(tree.convertValueToText(value, isSelected, isExpanded, isLeaf, row, hasFocus));
		text.setIcon(item.getIcon());
		
		if(item instanceof Branch)
		{
			text.setFont(branchFont);
		}
		else
		{
//			if(isSelected)
//				text.setFont(leafSelectedFont);
//			else
				text.setFont(leafFont);
		}
		
		if(isSelected)
			setBackground(selectedColor);
		else
			setBackground(null);
		
		return this;
	}
	
//	public void paintChildren(Graphics g)
//	{
//		Graphics2D g2D = (Graphics2D) g;
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//		
//		super.paintChildren(g);
//	}
	
//	public void validate(){}
//	public void invalidate(){}
//	public void revalidate(){}
	public void repaint(long tm, int x, int y, int width, int height) {}
	public void repaint(Rectangle r) {}
	public void repaint(){}
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){}
	public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
	public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
	public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
	public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
	public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
	public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
	public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
	
	public boolean isOpaque(){return true;}
}
