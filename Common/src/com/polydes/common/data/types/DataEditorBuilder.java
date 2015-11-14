package com.polydes.common.data.types;

import com.polydes.common.ui.propsheet.PropertiesSheetBuilder;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class DataEditorBuilder
{
	protected PropertiesSheetBuilder builder;
	protected DataType<?> type;
	protected EditorProperties props;
	
	public DataEditorBuilder(DataType<?> type, EditorProperties props)
	{
		this.type = type;
		this.props = props;
	}

	public DataEditor<?> build(PropertiesSheetStyle style)
	{
		return type.createEditor(props, style);
	}
	
	public PropertiesSheetBuilder add()
	{
		builder.createEditor(type.createEditor(props, builder.getStyle()));
		return builder;
	}
}
