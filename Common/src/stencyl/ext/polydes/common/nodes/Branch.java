package stencyl.ext.polydes.common.nodes;

import java.util.List;

public interface Branch<T extends Leaf<T>> extends Leaf<T>
{
	public List<Leaf<T>> getItems();
	public void addFolderListener(BranchListener<T> l);
	public void removeFolderListener(BranchListener<T> l);
	public void addItem(Leaf<T> item);
	public void addItem(Leaf<T> item, int position);
	public Leaf<T> getItemByName(String name);
	public Leaf<T> getItemAt(int position);
	public int indexOfItem(Leaf<T> item);
	public void removeItem(Leaf<T> item);
	public boolean hasItem(Leaf<T> item);
	public boolean canAcceptItem(Leaf<T> item);
	public boolean canCreateItemWithName(String itemName);
	public boolean isItemCreationEnabled();
	public boolean isFolderCreationEnabled();
	public boolean isItemRemovalEnabled();
	public boolean isItemEditingEnabled();
	public void registerNameChange(String oldName, String name);
}