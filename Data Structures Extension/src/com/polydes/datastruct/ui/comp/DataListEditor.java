package com.polydes.datastruct.ui.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.types.ExtraProperties;
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
	
	//Focus
	private HierarchyLeaveListener hll;
	
	//Model
	private DataList model;
	private ExtraProperties genTypeExtras;
	
	private DataListTableWrapper tableModel;
	
	private final EventListenerSupport<ActionListener> actionListeners;
	
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
		
//		table.setDragEnabled(true);
//		table.setDropMode(DropMode.ON_OR_INSERT);
//		table.setTransferHandler(transferHandler = new DataListTransferHandler(this));
		
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
		boolean newItemSelected = table.getSelectedRow() == model.size();
		
		int onmask = KeyEvent.CTRL_DOWN_MASK;
		if ((e.getModifiersEx() & onmask) == onmask)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				//pushUp();
			}
			if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				//pushDown();
			}
		}
		else
		{
			if (e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				if(!newItemSelected)
					removeSelected();
			}
			
			if(e.getKeyCode() == KeyEvent.VK_N)
			{
				add();
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				//this is only called when enter is refused by the cell editor
				//-- likely because the "new item" cell is selected
				
				if(newItemSelected)
					add();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
//	private static HashSet<DataType<?>> excludedTypes = Lang.hashset(Types._Array, Types._DataType, Types._Dynamic, Types._Selection, Types._Set);
	
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
		
		int[] rows = table.getSelectedRows();
		if(ArrayUtils.contains(rows, model.size()))
			rows = ArrayUtils.removeElement(rows, model.size());
		
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
	
//	public void pushUp()
//	{
//		Object above = tree.getModel().getElementAt(
//				tree.getSelectedIndex() - 1);
//		Object curr = tree.getModel().getElementAt(tree.getSelectedIndex());
//
//		model.set(tree.getSelectedIndex(), above);
//		model.set(tree.getSelectedIndex() - 1, curr);
//
//		((DefaultListModel) tree.getModel()).setElementAt(above,
//				tree.getSelectedIndex());
//		((DefaultListModel) tree.getModel()).setElementAt(curr,
//				tree.getSelectedIndex() - 1);
//
//		tree.setSelectedIndex(tree.getSelectedIndex() - 1);
//
//		refreshActions();
//	}
//	
//	public void pushDown()
//	{
//		Object below = tree.getModel().getElementAt(
//				tree.getSelectedIndex() + 1);
//		Object curr = tree.getModel().getElementAt(tree.getSelectedIndex());
//
//		model.set(tree.getSelectedIndex(), below);
//		model.set(tree.getSelectedIndex() + 1, curr);
//
//		((DefaultListModel) tree.getModel()).setElementAt(below,
//				tree.getSelectedIndex());
//		((DefaultListModel) tree.getModel()).setElementAt(curr,
//				tree.getSelectedIndex() + 1);
//
//		tree.setSelectedIndex(tree.getSelectedIndex() + 1);
//
//		refreshActions();
//	}
	
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
		if(e.getClickCount() >= 2)
		{
			//this is only called when enter is refused by the cell editor
			//-- likely because the "new item" cell is selected
			
			if(table.getSelectedRow() == model.size())
				add();
		}
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
}
