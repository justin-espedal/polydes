package stencyl.ext.polydes.datastruct.data.folder;

public interface FolderHierarchyRepresentation
{
	public void dataItemStateChanged(DataItem source);
	public void dataItemNameChanged(DataItem source, String oldName);
	public void itemAdded(Folder folder, DataItem item, int position);
	public void itemRemoved(DataItem item);
}
