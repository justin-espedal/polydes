package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public interface PreviewableEditor
{
	public void setPreviewSheet(PropertiesSheet sheet, DefaultLeaf key);
}
