package stencyl.ext.polydes.datastruct.data.folder;


public interface DataItemListener
{
	public void dataItemStateChanged(DataItem source);
	public void dataItemNameChanged(DataItem source, String oldName);
}
