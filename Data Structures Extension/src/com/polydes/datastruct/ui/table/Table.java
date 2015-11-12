package com.polydes.datastruct.ui.table;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import layout.TableLayout;

public class Table extends Card
{
	public static final double[] defaultColumns = new double[] {TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.FILL};
	public final double[][] tableSize;
	public PropertiesSheetStyle style;
	
	public Table(PropertiesSheetStyle style)
	{
		super("", false);
		this.style = style;
		
		tableSize = new double[][]
			{{TableLayout.PREFERRED, 10, style.fieldDimension.width, TableLayout.FILL},
			{}};
		setLayout(layout = new TableLayout(getRoot().tableSize));
	}
	
	@Override
	public Table getRoot()
	{
		return this;
	}
}