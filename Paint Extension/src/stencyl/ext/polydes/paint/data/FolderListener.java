package stencyl.ext.polydes.paint.data;

public interface FolderListener
{
	public void folderItemAdded(Folder folder, DataItem item);
	public void folderItemRemoved(Folder folder, DataItem item);
	public void folderItemMoved(Folder folder, DataItem item, int oldPosition);
}
