package com.polydes.extrasmanager.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.extrasmanager.data.folder.SysFile;
import com.polydes.extrasmanager.data.folder.SysFolder;
import com.polydes.extrasmanager.io.FileOperations;

public class FileOpHierarchyModel extends HierarchyModel<SysFile>
{
	public FileOpHierarchyModel(Branch<SysFile> rootBranch)
	{
		super(rootBranch);
	}
	
	@Override
	public void addItem(Leaf<SysFile> item, Branch<SysFile> target, int position)
	{
		
	}
	
	@Override
	public void removeItem(Leaf<SysFile> item, Branch<SysFile> target)
	{
		
	}
	
	@Override
	public void massMove(Leaf<SysFile>[] transferItems, Branch<SysFile> target, int position)
	{
		List<File> toMove = new ArrayList<File>();
		
		for(Leaf<SysFile> item : transferItems)
			if(item.getParent() != target)
				toMove.add(((SysFile) item).getFile());
		if(toMove.isEmpty())
			return;
		
		FileOperations.moveFiles(toMove, ((SysFolder) target).getFile());
	}
}
