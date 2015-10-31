package com.polydes.datastruct.ui.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.types.DataTransferable;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.SerializedData;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.HierarchyLeaveListener;

import stencyl.sw.lnf.Theme;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.UI;

public class DataListEditor extends JPanel implements KeyListener, MouseListener
{
	private static final LanguagePack lang = LanguagePack.get();
	
	//Component
	private final JTable table;
	private final DataListCellRenderer renderer;
	private final DataListCellEditor editor;
	
	//Focus, Transfer, Actions
	private final HierarchyLeaveListener hll;
	private final EventListenerSupport<ActionListener> actionListeners;
	
	//Model
	private DataList model;
	private ExtraProperties genTypeExtras;
	private DataListTableWrapper tableModel;
	
	public DataListEditor(DataList model, ExtraProperties genTypeExtras, PropertiesSheetStyle style)
	{
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		setBackground(style.pageBg.darker());
		
		table = new JTable();
		table.setBackground(style.pageBg.darker());
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setSurrendersFocusOnKeystroke(true);
		InputMap tableIM = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		tableIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
		tableIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
		tableIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "none");
//		tableIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), "none");
//		tableIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), "none");
		
		table.setShowGrid(false);
		table.setColumnSelectionAllowed(false);
		table.setTableHeader(null);
		table.setRowMargin(0);
		
		table.addKeyListener(this);
		table.addMouseListener(this);
		
		editor = new DataListCellEditor(table);
		renderer = new DataListCellRenderer();
		
		hll = new HierarchyLeaveListener(table, () -> {
			if(table.isEditing())
			{
				table.getCellEditor().stopCellEditing();
				table.removeEditor();
			}
//			table.clearSelection();
		});
		table.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				table.repaint();
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				table.repaint();
			}
		});
		
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(new DataListTransferHandler(this));
		table.setFillsViewportHeight(true);
		
		setData(model, genTypeExtras);
		
		actionListeners = EventListenerSupport.create(ActionListener.class);
		
		setLayout(new BorderLayout());
		
		JScrollPane scrollPane = UI.createScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setBackground(style.pageBg.darker());
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Theme.BORDER_COLOR));
		
		int viewHeight = table.getRowHeight() * 6;
		
		table.setPreferredScrollableViewportSize(new Dimension(100, viewHeight));
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setData(DataList model, ExtraProperties genTypeExtras)
	{
		if(this.model != model)
		{
			this.model = model;
			
			tableModel = new DataListTableWrapper(model);
			table.setModel(tableModel);
			
			renderer.setModel(model);
			editor.setType(model, model.genType, genTypeExtras);
			
			TableColumnModel cm = table.getColumnModel();
			TableColumn indices = cm.getColumn(0);
			TableColumn values = cm.getColumn(1);
			
			cm.setColumnMargin(0);
			
			indices.setCellRenderer(renderer);
			indices.setMinWidth(30);
			indices.setMaxWidth(30);
			
			values.setCellEditor(editor);
			values.setCellRenderer(renderer);
		}
		
		this.genTypeExtras = genTypeExtras;
	}
	
	public DataList getModel()
	{
		return model;
	}
	
	public ExtraProperties getGenericExtras()
	{
		return genTypeExtras;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(table.getSelectedRow() < 0)
			return;
		
		int onmask = KeyEvent.CTRL_DOWN_MASK;
		if ((e.getModifiersEx() & onmask) == onmask)
		{
//			if(e.getKeyCode() == KeyEvent.VK_UP)
//			{
//				shiftSelection(-1);
//			}
//			if(e.getKeyCode() == KeyEvent.VK_DOWN)
//			{
//				shiftSelection(1);
//			}
		}
		else
		{
			if (e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				removeSelected();
			}
			
			if(e.getKeyCode() == KeyEvent.VK_N)
			{
				add();
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				//this is only called when enter is refused by the cell editor
				//-- perhaps the "new item" cell is selected
				
				if(table.getSelectedRow() == model.size())
					add();
				
				//or the index column is selected
				table.editCellAt(table.getSelectedRow(), 1);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public void add()
	{
		Object newItem = model.genType.decode("");
		int insertAt = Math.max(0, table.getSelectedRow());
		
		tableModel.insert(newItem, insertAt);
		selectRow(insertAt);
		table.editCellAt(insertAt, 1);
	}
	
	public void removeSelected()
	{
		int selectionLead = table.getSelectionModel().getLeadSelectionIndex();
		
		int[] rows = getCleanRowSelection();
		
		if(rows.length == 0)
			return;
		
		if(rows.length == 1)
		{
			tableModel.deleteRows(rows);
			selectRow(selectionLead);
			return;
		}
		
		UI.Choice result = UI.showYesNoPrompt(
			String.format("Delete %s items?", rows.length),
			"",
			lang.get("globals.remove"),
			lang.get("globals.noremove")
		);
		
		for(int r : rows)
			if(r < selectionLead)
				--selectionLead;
		
		if(result == UI.Choice.YES)
			tableModel.deleteRows(rows);
		
		table.requestFocus();
		selectRow(selectionLead);
	}
	
	public void selectRow(int r)
	{
		if(r == model.size())
			--r;
		table.getSelectionModel().setSelectionInterval(r, r);
	}
	
	public int[] getCleanRowSelection()
	{
		int[] rows = table.getSelectedRows();
		if(ArrayUtils.contains(rows, model.size()))
			rows = ArrayUtils.removeElement(rows, model.size());
		return rows;
	}
	
	public void shiftSelection(int space)
	{
		int[] rows = table.getSelectedRows();
		if(ArrayUtils.contains(rows, model.size()))
			rows = ArrayUtils.removeElement(rows, model.size());
		
		if(rows.length == 0)
			return;
		
		tableModel.shiftRows(rows, space);
	}
	
	//TODO: Send the actionListeners actionPerformed notifications.
	
	public void addActionListener(ActionListener listener)
	{
		actionListeners.addListener(listener);
	}
	
	public void removeActionListener(ActionListener listener)
	{
		actionListeners.removeListener(listener);
	}
	
	public void dispose()
	{
		hll.dispose();
		editor.dispose();
		genTypeExtras = null;
		model = null;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int row = table.getSelectedRow();
		if(row < 0)
			return;
		
		if(e.getClickCount() >= 2)
		{
			//this is only called when enter is refused by the cell editor
			//-- likely because the "new item" cell is selected
			
			if(row == model.size())
				add();
			
			//or the index column was clicked
			table.editCellAt(table.getSelectedRow(), 1);
		}
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	public static class DataListRowTransfer
	{
		public DataListEditor dle;
		public int[] rows;
		
		//only holds value if a DLE imported the data.
		public DataListEditor targetDLE = null;
		public int targetRow = -1;
	}
	
	public static class DataListTransferable implements Transferable
	{
		public static final DataFlavor dataListRowFlavor =
				new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Data List Row Indices");
		
		DataFlavor[] flavors;
		DataListRowTransfer data;
		
		public DataListTransferable(DataListEditor dle, int[] rows)
		{
			data = new DataListRowTransfer();
			data.dle = dle;
			data.rows = rows;
			
			flavors = new DataFlavor[]
			{
				DataTransferable.getFlavor(),
				dataListRowFlavor,
				DataFlavor.stringFlavor
			};
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			for (int i = 0; i < flavors.length; i++)
				if (flavors[i].equals(flavor))
					return true;
			return false;
		}
		
		private SerializedData cachedSD = null;
		private SerializedData getAsSerializedData()
		{
			if(cachedSD == null)
			{
				DataType<?> t = data.dle.model.genType;
				DataList transferList = new DataList(t);
				for(int i = 0; i < data.rows.length; ++i)
					transferList.add(data.dle.model.get(data.rows[i]));
				cachedSD = new SerializedData(t.haxeType, t.checkEncode(transferList));
			}
			return cachedSD;
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			if(flavor.equals(dataListRowFlavor))
				return data;
			if(flavor.equals(DataTransferable.getFlavor()))
				return getAsSerializedData();
			else if(flavor.equals(DataFlavor.stringFlavor))
				return getAsSerializedData().data;
			return null;
		}
	}
	
	public static final class DataListTransferHandler extends TransferHandler
	{
		private final DataListEditor dle;
		
		public DataListTransferHandler(DataListEditor dle)
		{
			this.dle = dle;
		}
		
		@Override
		public boolean canImport(TransferSupport support)
		{
			if(!support.isDrop())
				return false;
			
			if(support.isDataFlavorSupported(DataListTransferable.dataListRowFlavor))
			{
				DataListRowTransfer td = getDLRT(support.getTransferable());
				if(td.dle.model.genType != dle.model.genType)
					return false;
				
				return true;
			}
			if(support.isDataFlavorSupported(DataTransferable.getFlavor()))
			{
				// should check if it's an array of genType, or if it's a single item of genType
				SerializedData sd = getSerializedData(support.getTransferable());
				if(!sd.type.equals(dle.model.genType.haxeType))
					return false;
				
				return true;
			}
			if(support.isDataFlavorSupported(DataFlavor.stringFlavor))
				return true;
			
			System.out.println(StringUtils.join(support.getDataFlavors(),','));
			
			return false;
		}
		
		@Override
		protected Transferable createTransferable(JComponent c)
		{
			if(c != dle.table)
				return null;
			
			return new DataListTransferable(dle, dle.getCleanRowSelection());
		}
		
		private DataListRowTransfer getDLRT(Transferable t)
		{
			try
			{
				return (DataListRowTransfer) t.getTransferData(DataListTransferable.dataListRowFlavor);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		private SerializedData getSerializedData(Transferable t)
		{
			try
			{
				return (SerializedData) t.getTransferData(DataTransferable.getFlavor());
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		private String getStringData(Transferable t)
		{
			try
			{
				return (String) t.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void exportDone(JComponent source, Transferable t, int action)
		{
			DataListRowTransfer dlrt = getDLRT(t);
			if(dlrt.targetDLE != dle)
				dle.tableModel.deleteRows(dlrt.rows);
			else
			{
				int firstRow = dlrt.targetRow;
				for(int row : dlrt.rows)
					if(row < firstRow)
						--firstRow;
				dle.table.setRowSelectionInterval(firstRow, firstRow + dlrt.rows.length - 1);
			}
		}
		
		@Override
		public int getSourceActions(JComponent c)
		{
			return TransferHandler.COPY_OR_MOVE;
		}
		
		@Override
		public boolean importData(TransferSupport support)
		{
			int dropRow = ((JTable.DropLocation) support.getDropLocation()).getRow();
			if(dropRow > dle.model.size())
				dropRow = dle.model.size();
			
			if(support.isDataFlavorSupported(DataListTransferable.dataListRowFlavor))
			{
				DataListRowTransfer dlrt = getDLRT(support.getTransferable());
				
				dlrt.targetRow  = dropRow;
				dlrt.targetDLE = dle;
				
				if(dlrt.dle == dle)
				{
					dle.tableModel.moveLines(dlrt.rows, dropRow);
				}
				else
				{
					DataType<?> t = dlrt.dle.model.genType;
					DataList transferList = new DataList(t);
					for(int i = 0; i < dlrt.rows.length; ++i)
						transferList.add(dlrt.dle.model.get(dlrt.rows[i]));
					
					dle.tableModel.insertList(transferList, dropRow);
				}
				
				return true;
			}
			if(support.isDataFlavorSupported(DataTransferable.getFlavor()))
			{
				Transferable t = support.getTransferable();
				
				SerializedData sd = getSerializedData(t);
				DataType<?> type = Types.fromXML(sd.type);
				if(type == Types._Array)
				{
					DataList values = Types._Array.decode(sd.data);
					dle.tableModel.insertList(values, dropRow);
					return true;
				}
				else if(type == dle.model.genType)
				{
					Object value = type.decode(sd.data);
					dle.tableModel.insert(value, dropRow);
					return true;
				}
				
				return false;
			}
			else if(support.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String s = getStringData(support.getTransferable());
				
				DataList newData = new DataList(dle.model.genType);
				for(String sub : s.split("\n"))
				{
					try
					{
						newData.add(newData.genType.decode(sub));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return false;
					}
				}
				
				dle.tableModel.insertList(newData, dropRow);
				
				return true;
			}
			
			return false;
		}
	}
}
