package stencyl.ext.polydes.datastruct.ui.objeditors;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;

public interface PreviewableEditor
{
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key);
}
