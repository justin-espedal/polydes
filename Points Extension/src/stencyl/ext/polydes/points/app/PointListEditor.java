package stencyl.ext.polydes.points.app;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import stencyl.ext.polydes.points.NamedPoint;

public class PointListEditor extends DefaultCellEditor
{
	public PointListEditor()
	{
		super(new JTextField());
	}
	
	NamedPoint editedPoint;
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		editedPoint = (NamedPoint) value;
		
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	
	@Override
	public boolean stopCellEditing()
	{
		boolean stopped = super.stopCellEditing();
		
		if(stopped)
			editedPoint.setName("" + super.getCellEditorValue());
			
		return stopped;
	}
	
	@Override
	public Object getCellEditorValue()
	{
		return editedPoint;
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent)
	{
		if(anEvent instanceof KeyEvent)
			return ((KeyEvent) anEvent).getKeyCode() == KeyEvent.VK_ENTER;
		
		return super.isCellEditable(anEvent);
	}

	public void requestFocus()
	{
		getComponent().requestFocus();
	}
}
