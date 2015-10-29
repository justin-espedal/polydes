package com.polydes.datastruct.ui.comp;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.Layout;
import com.polydes.datastruct.ui.utils.TemporaryAWTListener;

public class DataListCellEditor implements TableCellEditor
{
	private static final EditController controller = new EditController();
	private static final ExtrasMap defaultExtras = new ExtrasMap();
	
	private final JTable table;
	
	private DataList model;
	private DataType<?> genType;
	private ExtraProperties genTypeExtras;
	
	private final EventListenerSupport<CellEditorListener> cellListeners;
	
	private DataEditor<?> curEditor;
	
	public DataListCellEditor(JTable table)
	{
		cellListeners = EventListenerSupport.create(CellEditorListener.class);
		this.table = table;
	}
	
	public void setType(DataList model, DataType<?> genType, ExtraProperties genTypeExtras)
	{
		this.model = model;
		this.genType = genType;
		this.genTypeExtras = genTypeExtras;
	}

	@Override
	public Object getCellEditorValue()
	{
		return curEditor.getValue();
	}

	@Override
	public boolean isCellEditable(EventObject e)
	{
		if(e instanceof MouseEvent && ((MouseEvent) e).getClickCount() < 2)
			return false;
		
		if(e instanceof KeyEvent && ((KeyEvent) e).getKeyCode() != KeyEvent.VK_ENTER)
			return false;
		
		if(table.getSelectedRow() == model.size())
			return false;
		
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject e)
	{
		return true;
	}

	@Override
	public boolean stopCellEditing()
	{
		cellListeners.fire().editingStopped(new ChangeEvent(this));
		
		disposeEditor();
		return true;
	}

	@Override
	public void cancelCellEditing()
	{
		cellListeners.fire().editingCanceled(new ChangeEvent(this));
		
		disposeEditor();
	}
	
	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		cellListeners.addListener(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		cellListeners.removeListener(l);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		return generateEditor(value);
	}
	
	private <S> Component generateEditor(S value)
	{
		disposeEditor();
		
		@SuppressWarnings("unchecked")
		DataEditor<S> deditor = (genTypeExtras != null) ?
			(DataEditor<S>) genType.createEditor(genTypeExtras, PropertiesSheetStyle.DARK) :
			(DataEditor<S>) genType.createEditor(defaultExtras, PropertiesSheetStyle.DARK); 
		
		deditor.setValue(value);
		
		controller.startForEditor(this);
		curEditor = deditor;
		
		Component[] subEditors = curEditor.getComponents();
		JPanel panel = new JPanel()
		{
			@Override
			public void requestFocus()
			{
				if(!ArrayUtils.isEmpty(subEditors))
					subEditors[0].requestFocus();
			}
		};
		Layout.addHorizontally(panel, subEditors);
		
		return panel;
	}
	
	public void dispose()
	{
		disposeEditor();
		model = null;
		genType = null;
		genTypeExtras = null;
	}
	
	private void disposeEditor()
	{
		if(curEditor != null)
		{
			curEditor.dispose();
			curEditor = null;
			
			controller.dispose();
		}
	}
	
	private static final class EditController extends TemporaryAWTListener
	{
		DataListCellEditor editor;
		
		public EditController()
		{
			super(AWTEvent.KEY_EVENT_MASK);
		}
		
		@Override
		public void eventDispatched(AWTEvent e)
		{
			if(e instanceof KeyEvent)
			{
				KeyEvent ke = (KeyEvent) e;
				
				if(ke.getID() == KeyEvent.KEY_PRESSED && ke.getKeyCode() == KeyEvent.VK_ENTER)
				{
					editor.stopCellEditing();
					stop();
				}
			}
		}
		
		public void startForEditor(DataListCellEditor editor)
		{
			this.editor = editor;
			start();
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor = null;
		}
	}
}
