package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.polydes.common.res.ResourceLoader;
import com.polydes.common.util.PopupUtil;
import com.polydes.common.util.PopupUtil.MenuItemAccess;

public interface NodeCreator<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	ArrayList<CreatableNodeInfo> getCreatableNodeList(U creationBranch);
	T createNode(CreatableNodeInfo selected, String nodeName);
	
	ArrayList<NodeAction<T>> getNodeActions(T[] targets);
	
	void editNode(T DefaultLeaf);
	void nodeRemoved(T toRemove);
	boolean attemptRemove(List<T> toRemove);
	
	static final ImageIcon folderIcon = ResourceLoader.loadIcon("tree/folder-enabled.png");
	public static CreatableNodeInfo folderInfo = new CreatableNodeInfo("Folder", null, folderIcon);
	
	public static class ActionInfo implements MenuItemAccess
	{
		public String name;
		public Object data;
		public ImageIcon icon;
		
		public ActionInfo(String name, Object data, ImageIcon icon)
		{
			this.name = name;
			this.data = data;
			this.icon = icon;
		}
		
		@Override
		public JMenuItem asMenuItem()
		{
			return PopupUtil.menuItem(name, this, icon);
		}
	}
	
	public static class CreatableNodeInfo extends ActionInfo
	{
		public CreatableNodeInfo(String name, Object data, ImageIcon icon)
		{
			super(name, data, icon);
		}
	}
	
	public static class NodeAction<T> extends ActionInfo
	{
		public Consumer<T> callback;
		
		public NodeAction(String name, ImageIcon icon, Consumer<T> callback)
		{
			super(name, callback, icon);
			this.callback = callback;
		}
	}
}
