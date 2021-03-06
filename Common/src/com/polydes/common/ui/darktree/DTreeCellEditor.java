package com.polydes.common.ui.darktree;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.tree.TreeCellEditor;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.util.IconUtil;

public class DTreeCellEditor<T extends Leaf<T,U>, U extends Branch<T,U>> implements TreeCellEditor, KeyListener, DocumentListener, FocusListener
{
	private DarkTree<T,U> dtree;
	
	private InlineTreeInput input;
	private ArrayList<CellEditorListener> listeners;
	
	private T value;
	private T previousValue;
	private String previousTextValue;
	
	private CellEditValidator validator;
	
	private boolean forceEdit = false;
	
	public DTreeCellEditor(DarkTree<T,U> dtree)
	{
		input = new InlineTreeInput(dtree);
		input.addKeyListener(this);
		input.getDocument().addDocumentListener(this);
		input.addFocusListener(this);
		listeners = new ArrayList<CellEditorListener>();
		this.dtree = dtree;
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
	public T getCellEditorValue()
	{
		return value;
	}
	
	public String getCellEditorTextValue()
	{
		return input.getText();
	}
	
	public String getCellEditorPreviousTextValue()
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
		@SuppressWarnings("unchecked")
		T node = (T) tree.getLastSelectedPathComponent();
		if(node == null || !node.canEditName() || node.getParent() == null)
			return false;
		
		if(e instanceof MouseEvent)
			return dtree.isNameEditingAllowed() && ((MouseEvent) e).getClickCount() >= 2;
		
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
		
		value = previousValue;
		value.setName(getCellEditorTextValue());
		previousValue = null;
		
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
		@SuppressWarnings("unchecked")
		T uo = (T) value;
		
		input.setNodeType(!(uo instanceof Branch));
		input.setText(uo.getName());
		if(uo instanceof Branch)
			input.setIcon(null);
		else
			input.setIcon(IconUtil.getIcon(uo.getIcon(), DTreeCellRenderer.ICON_SIZE));
			
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

	@Override
	public void focusGained(FocusEvent e)
	{
		
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		cancelCellEditing();
	}
}
