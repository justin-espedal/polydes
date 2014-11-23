package stencyl.ext.polydes.datastruct.ui.tree;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.Folder;

public class DTreeCellEditor implements TreeCellEditor, KeyListener, DocumentListener
{
	private InlineTreeInput input;
	private ArrayList<CellEditorListener> listeners;
	
	private Object value;
	private Object previousValue;
	private String previousTextValue;
	
	private CellEditValidator validator;
	
	private boolean forceEdit = false;
	
	public DTreeCellEditor(DTree dtree)
	{
		input = new InlineTreeInput(dtree);
		input.addKeyListener(this);
		input.getDocument().addDocumentListener(this);
		listeners = new ArrayList<CellEditorListener>();
	}
	
	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		if(!listeners.contains(l))
			listeners.add(l);
	}

	@Override
	public void cancelCellEditing()
	{
		input.setText("");
		
		ChangeEvent e = new ChangeEvent(this);
		for(CellEditorListener l : listeners)
		{
			l.editingCanceled(e);
		}
	}

	@Override
	public Object getCellEditorValue()
	{
		return value;
	}
	
	public Object getCellEditorPreviousValue()
	{
		return previousValue;
	}
	
	public String getCellEditorTextValue()
	{
		return input.getText();
	}
	
	public Object getCellEditorPreviousTextValue()
	{
		return previousTextValue;
	}
	
	public void setValidator(CellEditValidator v)
	{
		validator = v;
	}

	@Override
	public boolean isCellEditable(EventObject e)
	{
		if(forceEdit)
		{
			forceEdit = false;
			return true;
		}
		
		if(e == null)
			return false;
		
		if(e.getSource() == null)
			return false;
		
		JTree tree = (JTree) e.getSource();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null)
			return false;
		
		DataItem item = (DataItem) node.getUserObject();
		if(!item.canEditName())
			return false;
		
		if(e instanceof MouseEvent)
			return ((MouseEvent) e).getClickCount() >= 2;
		
		return false;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		listeners.remove(l);
	}

	@Override
	public boolean shouldSelectCell(EventObject e)
	{
		return true;
	}

	@Override
	public boolean stopCellEditing()
	{
		if(!validate())
		{
			if(input.getText().equals(previousTextValue))
				cancelCellEditing();
			
			return false;
		}
		
		if(previousValue instanceof DataItem)
		{
			value = previousValue;
			((DataItem) value).setName(getCellEditorTextValue());
		}
		else
		{
			value = getCellEditorTextValue();
		}
		
		ChangeEvent e = new ChangeEvent(this);
		for(CellEditorListener l : listeners)
		{
			l.editingStopped(e);
		}
		return true;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
	{
		DataItem uo = (DataItem) ((DefaultMutableTreeNode) value).getUserObject();
		
		input.setNodeType(!(uo instanceof Folder));
		input.setText(uo.getName());
		input.setIcon(uo.getIcon());
		
		previousValue = uo;
		previousTextValue = uo.getName();
		
		input.setPreviousValue(previousTextValue);
		
		input.updateTreeWidth();
		
		return input;
	}
	
	public boolean validate()
	{
		if(input.getText().equals(""))
			return false;
		if(validator == null)
			return true;
		else
			return validator.validate(input.getText());
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			stopCellEditing();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	public void clearText()
	{
		input.setText("");
	}

	@Override
	public void changedUpdate(DocumentEvent arg0)
	{
		input.setValid(validate());
	}

	@Override
	public void insertUpdate(DocumentEvent arg0)
	{
		input.setValid(validate());
	}

	@Override
	public void removeUpdate(DocumentEvent arg0)
	{
		input.setValid(validate());
	}

	public void allowEdit()
	{
		forceEdit = true;
	}

	public void selectText()
	{
		input.selectAll();
	}
}
