package stencyl.ext.polydes.paint.app.tree;

import stencyl.ext.polydes.paint.data.Folder;

public interface DTreeNodeCreator
{
	Object createNode(String nodeName);
	Folder createFolder(String newName);
	Boolean isNodeCreationEnabled();
	Boolean isFolderCreationEnabled();
	Boolean isRemovalEnabled();
	boolean canCreate(String string, Folder newNodeFolder);
	void setSelectionState(DTreeSelectionState selectionState);
}