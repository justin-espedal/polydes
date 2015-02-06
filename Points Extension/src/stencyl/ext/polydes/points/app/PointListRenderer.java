package stencyl.ext.polydes.points.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import stencyl.sw.util.Fonts;

public class PointListRenderer extends JPanel implements TableCellRenderer
{
	private static final Color selectedColor = new Color(102, 102, 102);
	
	private JLabel label;
	
	public PointListRenderer()
	{
		super(new BorderLayout());
		
		label = new JLabel();
		label.setFont(Fonts.getBoldFont());
		label.setForeground(Color.WHITE);
		label.setBackground(null);
		setBackground(null);
		
		setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));
		add(label, BorderLayout.WEST);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		label.setText("" + value);
		setBackground(isSelected ? selectedColor : null);
		return this;
	}
}