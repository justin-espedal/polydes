package com.polydes.datastruct.ui.objeditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport.FieldInfo;
import com.polydes.common.ui.propsheet.PropertiesSheetWrapper;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.ui.table.Table;
import com.polydes.datastruct.ui.utils.Layout;

public class StructureObjectPanel extends Table implements PreviewableEditor
{
	public static final int RESIZE_FLAG = 0x01;
	
	protected PropertiesSheetSupport sheet;
	
	protected PropertiesSheet preview;
	protected DataItem previewKey;
	
	private HashMap<String, PropertiesSheetSupport> extensions = new HashMap<>();
	private HashMap<String, DisposableSheetWrapper> wrappers = new HashMap<>();
	
	public StructureObjectPanel(PropertiesSheetStyle style, Object model)
	{
		super(style);
		sheet = createSheetExtension(model, "base");
		
		setBorder(BorderFactory.createEmptyBorder(style.rowgap, style.rowgap, 0, style.rowgap));
	}
	
	private void addGenericRowAtInternal(int row, String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(row, group);
	}
	
	public int addGenericRow(String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(rows.length, group);
		return rows.length - 1;
	}
	
	//TODO: Get rid of this
	public int addGenericRow(String label, DataEditor<?> editor, final int flags)
	{
		final JComponent[] comps = editor.getComponents();
		if((flags & RESIZE_FLAG) > 0)
		{
			for(JComponent comp : comps)
				comp.addComponentListener(resizeListener);
			editor.addDisposeListener(() -> {
				for(JComponent comp : comps)
					comp.removeComponentListener(resizeListener);
			});
		}
		
		return addGenericRow(label, comps);
	}
	
	//TODO: Make this better (use DisabledPanel)
	public JCheckBox createEnabler(final DataEditor<?> editor, final boolean initialValue)
	{
		final JCheckBox enabler = new JCheckBox();
		enabler.setSelected(initialValue);
		enabler.setBackground(null);
		
		enabler.addActionListener(new ActionListener()
		{
			private boolean enabled = initialValue;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(enabled != enabler.isSelected())
				{
					for(JComponent comp : editor.getComponents())
						comp.setVisible(enabler.isSelected());
					if(!enabler.isSelected())
						editor.setValue(null);
					previewKey.setDirty(true);
					enabled = enabler.isSelected();
					
					revalidate();
				}
			}
		});
		
		for(JComponent comp : editor.getComponents())
			comp.setVisible(initialValue);
		
		return enabler;
	}
	
	public void setRowVisibility(PropertiesSheetSupport sheet, String id, boolean visible)
	{
		int rowID = ((DisposableSheetWrapper) sheet.getWrapper()).rowIndex.get(id);
		rows[rowID].setConditionallyVisible(visible);
		
		layoutContainer();
		revalidate();
		setSize(getPreferredSize());
	}
	
	private ComponentListener resizeListener = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			setSize(getPreferredSize());
		}
	};
	
	public PropertiesSheetSupport createSheetExtension(Object model, String id)
	{
		DisposableSheetWrapper wrapper = new DisposableSheetWrapper();
		PropertiesSheetSupport support = new PropertiesSheetSupport(wrapper, style, model);
		
		extensions.put(id, support);
		wrappers.put(id, wrapper);
		
		return support;
	}
	
	public void clearSheetExtension(String id)
	{
		PropertiesSheetSupport support = extensions.remove(id);
		wrappers.remove(id);
		support.dispose();
	}
	
	private void removeRow(int rowID)
	{
		removeGroup(rowID);
		for(DisposableSheetWrapper wrapper : wrappers.values())
			wrapper.decrementGreaterThan(rowID, 1);
	}
	
	@Override
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key)
	{
		preview = sheet;
		previewKey = key;
	}
	
	public void revertChanges()
	{
		for(PropertiesSheetSupport support : extensions.values())
			support.revertChanges();
	}
	
	public void dispose()
	{
		sheet = null;
		preview = null;
		previewKey = null;
		
		removeAll();
		
		for(PropertiesSheetSupport support : extensions.values())
			support.dispose();
		
		extensions = null;
		wrappers = null;
	}
	
	// PropertiesSheetWrapper
	
	public class DisposableSheetWrapper implements PropertiesSheetWrapper
	{
		private HashMap<String, Integer> rowIndex = new HashMap<>();
		private boolean disposing;
		
		public void decrementGreaterThan(int pivot, int amount)
		{
			if(disposing)
				return;
			
			for(String rowKey : rowIndex.keySet())
			{
				int rowIsAt = rowIndex.get(rowKey);
				if(rowIsAt > pivot)
					rowIndex.put(rowKey, rowIsAt - amount);
			}
		}
		
		private JComponent[] buildRow(FieldInfo field, DataEditor<?> editor)
		{
			JComponent[] comps = editor.getComponents();
			
			String hint = field.getHint();
			if(hint != null && !hint.isEmpty())
				comps = ArrayUtils.add(comps, style.createEditorHint(hint));
			
			//TODO: editor.getValue() will NOT be an accurate value at this time.
			if(field.isOptional())
				comps = ArrayUtils.add(comps, 0, createEnabler(editor, editor.getValue() != null));
			
			return comps;
		}
		
		@Override
		public void addField(FieldInfo newField, DataEditor<?> editor)
		{
			JComponent[] comps = buildRow(newField, editor);
			editor.addListener(() -> {if(previewKey != null) previewKey.setDirty(true);});
			
			int row = addGenericRow(newField.getLabel(), comps);
			rowIndex.put(newField.getVarname(), row);
		}
		
		@Override
		public void changeField(String varname, FieldInfo field, DataEditor<?> editor)
		{
			JComponent[] comps = buildRow(field, editor);
			editor.addListener(() -> {if(previewKey != null) previewKey.setDirty(true);});
			
			int row = rowIndex.get(varname);
			removeGroup(row);
			addGenericRowAtInternal(row, field.getLabel(), comps);
		}
		
		@Override
		public void addHeader(String title)
		{
			int row = addGenericRow("", style.createRoundedLabel(title));
			rowIndex.put("H: " + title, row);
		}
		
		@Override
		public void finish()
		{
			
		}
		
		@Override
		public void dispose()
		{
			disposing = true;
			for(Integer row : rowIndex.values())
				removeRow(row);
			rowIndex = null;
		}
	}
}