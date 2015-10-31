package com.polydes.datastruct.ui.comp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.datastruct.data.core.DataList;

public class DataListTableWrapper implements TableModel
{
	private final DataList model;
	private final EventListenerSupport<TableModelListener> tableListeners;
	
	public DataListTableWrapper(DataList model)
	{
		this.model = Objects.requireNonNull(model);
		tableListeners = new EventListenerSupport<>(TableModelListener.class);
	}

	@Override
	public int getRowCount()
	{
		return model.size() + 1;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if(columnIndex == 0)
			return "Index";
		else
			return "Value";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if(columnIndex == 0)
			return String.class;
		else
			return model.genType.javaType;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex == model.size())
		{
			if(columnIndex == 0)
				return "+";
			else
				return null;
		}
		
		if(columnIndex == 0)
			return rowIndex;
		else
			return model.get(rowIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(columnIndex != 1)
			return;
		model.set(rowIndex, aValue);
		
		tableListeners.fire().tableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex));
	}
	
	public void insert(Object value, int row)
	{
		model.add(row, value);
		tableListeners.fire().tableChanged(new TableModelEvent(this, row, row, 1, TableModelEvent.INSERT));
	}
	
	public void insertList(DataList list, int row)
	{
		int firstRow = row;
		for(Object value : list)
			model.add(row++, value);
		tableListeners.fire().tableChanged(new TableModelEvent(this, firstRow, --row, 1, TableModelEvent.INSERT));
	}
	
	public void delete(int row)
	{
		model.remove(row);
		tableListeners.fire().tableChanged(new TableModelEvent(this, row, row, 1, TableModelEvent.DELETE));
	}
	
	public void deleteRows(int[] rows)
	{
		if(rows.length == 1)
		{
			delete(rows[0]);
			return;
		}
		
		Arrays.sort(rows);
		
		for(int i = rows.length - 1; i >= 0; --i)
			model.remove(rows[i]);
		tableListeners.fire().tableChanged(new TableModelEvent(this));
	}

	public void shiftRows(int[] rows, int space)
	{
		Arrays.sort(rows);
		
		int start = 0;
		int end = rows.length;
		int step = 1;
		
		if(space > 0)
		{
			start = end;
			end = -1;
			step = -1;
		}
		
		for(int i = start; i != end; i += step)
			swap(rows[i], space);
		
		int l = rows.length - 1;
		int changeMin = Math.min(rows[0], rows[0] + space);
		int changeMax = Math.max(rows[l], rows[l] + space);
		
		tableListeners.fire().tableChanged(new TableModelEvent(this, changeMin, changeMax, 1));
	}
	
	public void moveLines(int[] rows, int target)
	{
		Arrays.sort(rows);
		
		System.out.println(StringUtils.join(rows, ','));
		System.out.println(target);
		
		if(ArrayUtils.contains(rows, target))
			return;
		
		int min = Math.min(rows[0], target);
		int max = rows[rows.length - 1];
		
		ArrayList<Object> transferData = new ArrayList<Object>(rows.length);
		
		for(int i = rows.length - 1; i >= 0; --i)
		{
			transferData.add(model.remove(rows[i]));
			if(rows[i] < target)
				--target;
		}
		
		for(int i = rows.length - 1; i >= 0; --i)
			model.add(target++, transferData.get(i));
		
		max = Math.max(max, --target);
		
		tableListeners.fire().tableChanged(new TableModelEvent(this, min, max, 1));
	}
	
	private void swap(int row, int space)
	{
		if(row + space < 0 || row + space >= model.size())
			return;
		
		Object temp = model.get(row + space);
		model.set(row + space, model.get(row));
		model.set(row, temp);
	}
	
	@Override
	public void addTableModelListener(TableModelListener l)
	{
		tableListeners.addListener(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		tableListeners.removeListener(l);
	}
}