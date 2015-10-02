package com.polydes.extrasmanager.app.list;

import javax.swing.DefaultListModel;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.nodes.Leaf;
import com.polydes.extrasmanager.data.folder.SysFile;
import com.polydes.extrasmanager.data.folder.SysFolder;

public class FileListModel extends DefaultListModel<Leaf<SysFile>> implements HierarchyRepresentation<SysFile> 
{
	HierarchyModel<SysFile> model;
	public SysFolder currView;
	public FileListModelListener listener;
	
	public FileListModel(HierarchyModel<SysFile> model)
	{
		this.model = model;
		model.addRepresentation(this);
		refresh((SysFolder) model.getRootBranch());
	}
	
	public void setListener(FileListModelListener listener)
	{
		this.listener = listener;
		if(listener != null && currView != null)
			listener.viewUpdated(this, currView);
	}
	
	public void refresh(SysFolder path)
	{
		clear();
		
		currView = path;
		
		if(listener != null)
			listener.viewUpdated(this, path);
		
		if(path == null)
			return;
		
		//HashSet<String> toExclude = (path == Main.getModel().getRootBranch()) ? Main.ownedFolderNames : null;
		
		for(Leaf<SysFile> f : path.getItems())
		{
			addElement(f);
		}
	}
	
	public void refresh()
	{
		refresh(currView);
	}

	@Override
	public void leafStateChanged(Leaf<SysFile> source)
	{
		if(source.getParent() == currView)
		{
			int i = currView.indexOfItem(source);
			fireContentsChanged(this, i, i);
		}
	}
	
	@Override
	public void leafNameChanged(Leaf<SysFile> source, String oldName)
	{
		if(source.getParent() == currView)
		{
			int i = currView.indexOfItem(source);
			fireContentsChanged(this, i, i);
		}	
	}
	
	@Override
	public void itemAdded(Branch<SysFile> folder, Leaf<SysFile> item, int position)
	{
		if(folder == currView)
		{
			add(position, item);
			fireIntervalAdded(this, position, position);
		}
	}

	@Override
	public void itemRemoved(Branch<SysFile> folder, Leaf<SysFile> item, int position)
	{
		if(folder == currView)
		{
			removeElement(item);
			fireIntervalRemoved(this, position, position);
		}
	}
	
	public void dipose()
	{
		model.removeRepresentation(this);
		model = null;
		listener = null;
		currView = null;
	}
}
