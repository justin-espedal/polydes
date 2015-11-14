package com.polydes.common.comp.datalist;

import static com.polydes.common.util.Lang.or;

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

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.comp.utils.TemporaryAWTListener;
import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class DataListCellEditor implements TableCellEditor
{
	private static final EditController controller = new EditController();
	private static final EditorProperties defaultProps = new EditorProperties();
	
	private final JTable table;
	
	private DataList model;
	private DataType<?> genType;
	private EditorProperties genTypeProps;
	
	private final EventListenerSupport<CellEditorListener> cellListeners;
	
	private DataEditor<?> curEditor;
	
	public DataListCellEditor(JTable table)
	{
		cellListeners = EventListenerSupport.create(CellEditorListener.class);
		this.table = table;
	}
	
	public void setType(DataList model, DataType<?> genType, EditorProperties genTypeProps)
	{
		this.model = model;
		this.genType = genType;
		this.genTypeProps = genTypeProps;
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
		DataEditor<S> deditor = (DataEditor<S>) genType.createEditor(or(genTypeProps, defaultProps), PropertiesSheetStyle.DARK);
		
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
		genTypeProps = null;
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
