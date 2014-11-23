package stencyl.ext.polydes.datastruct.data.folder;

public class FolderPolicy
{
	public boolean duplicateItemNamesAllowed;
	public boolean itemCreationEnabled;
	public boolean itemRemovalEnabled;
	public boolean itemEditingEnabled;
	public boolean folderCreationEnabled;
	
	public boolean canAcceptItem(Folder folder, DataItem item)
	{
		return duplicateItemNamesAllowed || folder.getItemByName(item.getName()) == null;
	}
	
	public boolean canCreateItemWithName(Folder folder, String itemName)
	{
		return duplicateItemNamesAllowed || folder.getItemByName(itemName) == null;
	}
}
