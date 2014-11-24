package stencyl.ext.polydes.dialog.data;

public interface FolderListener
{
	public void folderItemAdded(Folder folder, DataItem item);
	public void folderItemRemoved(Folder folder, DataItem item);
	public void folderItemMoved(Folder folder, DataItem item, int oldPosition);
}
