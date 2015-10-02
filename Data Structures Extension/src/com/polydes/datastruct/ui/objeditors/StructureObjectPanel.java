package com.polydes.datastruct.ui.objeditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.data.core.UPair;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DisposeListener;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.ui.table.Table;
import com.polydes.datastruct.ui.utils.Layout;

public class StructureObjectPanel extends Table implements PreviewableEditor
{
	public static final int RESIZE_FLAG = 0x01;
	//0001, 0002, 0004, 0008
	//0010, 0020. 0040, 0080
	//0100, 0200, 0400, 0800
	//1000, 2000, 4000, 8000
	
	protected PropertiesSheet preview;
	protected DataItem previewKey;
	
	private Expansion[] cardEditors;
	
	public StructureObjectPanel(PropertiesSheetStyle style)
	{
		super(style);
		
		setBorder(BorderFactory.createEmptyBorder(style.rowgap, style.rowgap, 0, style.rowgap));
		cardEditors = new Expansion[] {new Expansion()};
	}
	
	private void addEditor(int expansionID, int rowID, DataEditor<?> editor)
	{
		cardEditors[expansionID].add(rowID, editor);
		if(editor != null)
			editor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					previewKey.setDirty(true);
				}
			});
	}
	
	public int addEnablerRow(int expansionID, String label, DataEditor<?> editor, boolean initiallyEnabled)
	{
		JComponent[] comps = ArrayUtils.add(editor.getComponents(), 0, createEnabler(editor, initiallyEnabled));
		int rowID = addGenericRowInternal(expansionID, label, comps);
		addEditor(expansionID, rowID, editor);
		return rowID;
	}
	
	public int addGenericRow(int expansionID, String label, DataEditor<?> editor)
	{
		int rowID = addGenericRowInternal(expansionID, label, editor.getComponents());
		addEditor(expansionID, rowID, editor);
		return rowID;
	}
	
	private int addGenericRowInternal(int expansionID, String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(rows.length, group);
		return rows.length - 1;
	}
	
	public int addGenericRow(int expansionID, String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(rows.length, group);
		addEditor(expansionID, rows.length - 1, null);
		return rows.length - 1;
	}
	
	public int addGenericRow(int expansionID, String label, DataEditor<?> editor, final int flags)
	{
		final JComponent[] comps = editor.getComponents();
		if((flags & RESIZE_FLAG) > 0)
		{
			for(JComponent comp : comps)
				comp.addComponentListener(resizeListener);
			editor.addDisposeListener(new DisposeListener()
			{
				@Override
				public void disposed()
				{
					for(JComponent comp : comps)
						comp.removeComponentListener(resizeListener);
				
				}
			});
		}
		
		int rowID = addGenericRowInternal(expansionID, label, comps);
		addEditor(expansionID, rowID, editor);
		return rowID;
	}
	
	public int addGenericRow(String label, DataEditor<?> editor)
	{
		int rowID = addGenericRowInternal(0, label, editor.getComponents());
		addEditor(0, rowID, editor);
		return rowID;
	}
	
	public int addGenericRow(String label, JComponent... comps)
	{
		return addGenericRow(0, label, comps);
	}
	
	public int addGenericRow(String label, DataEditor<?> editor, final int flags)
	{
		return addGenericRow(0, label, editor, flags);
	}
	
	public int newExpander()
	{
		cardEditors = ArrayUtils.add(cardEditors, new Expansion());
		return cardEditors.length - 1;
	}
	
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
	
	public void setRowVisibility(int rowID, boolean visible)
	{
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
	
	public void clearExpansion(int expansionID)
	{
		for(UPair<Integer, DataEditor<?>> editor : cardEditors[expansionID])
		{
			removeGroup(editor.l);
			if(editor.r != null)
				editor.r.dispose();
			for(Expansion exp : cardEditors)
				for(UPair<Integer, DataEditor<?>> row : exp)
					if(row.l > editor.l)
						--row.l;
		}
		cardEditors[expansionID] = new Expansion();
	}
	
	@Override
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key)
	{
		preview = sheet;
		previewKey = key;
	}
}

class Expansion extends ArrayList<UPair<Integer, DataEditor<?>>>
{
	public void add(int rowID, DataEditor<?> editor)
	{
		add(new UPair<Integer, DataEditor<?>>(rowID, editor));
	}
}
