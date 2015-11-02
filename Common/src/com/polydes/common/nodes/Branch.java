package com.polydes.common.nodes;

import java.util.List;

public interface Branch<T extends Leaf<T,U>, U extends Branch<T,U>> extends Leaf<T,U>
{
	public List<T> getItems();
	public void addFolderListener(BranchListener<T,U> l);
	public void removeFolderListener(BranchListener<T,U> l);
	public void addItem(T item);
	public void addItem(T item, int position);
	public T getItemByName(String name);
	public T getItemAt(int position);
	public int indexOfItem(T item);
	public void removeItem(T item);
	public boolean hasItem(T item);
	public boolean canAcceptItem(T item);
	public boolean canCreateItemWithName(String itemName);
	public boolean isItemCreationEnabled();
	public boolean isFolderCreationEnabled();
	public boolean isItemRemovalEnabled();
	public boolean isItemEditingEnabled();
	public void registerNameChange(String oldName, String name);
}