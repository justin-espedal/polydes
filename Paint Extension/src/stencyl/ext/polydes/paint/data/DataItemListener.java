package stencyl.ext.polydes.paint.data;

public interface DataItemListener
{
	public void dataItemStateChanged(DataItem source);
	public void dataItemNameChanged(DataItem source, String oldName);
}
