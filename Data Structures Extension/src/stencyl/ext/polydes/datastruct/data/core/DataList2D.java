package stencyl.ext.polydes.datastruct.data.core;

import java.util.ArrayList;

import stencyl.ext.polydes.datastruct.data.types.DataType;

public class DataList2D extends ArrayList<ArrayList<Object>>
{
	public DataType<?> genType;
	public int columns;
	public int rows;
	
	public DataList2D(DataType<?> genType)
	{
		this.genType = genType;
	}
	
	public int getColumns()
	{
		return columns;
	}
	
	public void setColumns(int columns)
	{
		this.columns = columns;
	}
	
	public int getRows()
	{
		return rows;
	}
	
	public void setRows(int rows)
	{
		this.rows = rows;
	}
	
	public Object get(int row, int column)
	{
		return get(row).get(column);
	}
	
	public void set(int row, int column, Object o)
	{
		get(row).set(column, o);
	}
}