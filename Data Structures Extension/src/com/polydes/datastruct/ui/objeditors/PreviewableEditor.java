package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public interface PreviewableEditor
{
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key);
}
