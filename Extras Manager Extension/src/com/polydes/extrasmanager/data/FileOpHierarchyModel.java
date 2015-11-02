package com.polydes.extrasmanager.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.extrasmanager.data.folder.SysFile;
import com.polydes.extrasmanager.data.folder.SysFolder;
import com.polydes.extrasmanager.io.FileOperations;

public class FileOpHierarchyModel extends HierarchyModel<SysFile,SysFolder>
{
	public FileOpHierarchyModel(SysFolder rootBranch)
	{
		super(rootBranch);
	}
	
	@Override
	public void addItem(SysFile item, SysFolder target, int position)
	{
		
	}
	
	@Override
	public void removeItem(SysFile item, SysFolder target)
	{
		
	}
	
	@Override
	public void massMove(List<SysFile> transferItems, SysFolder target, int position)
	{
		List<File> toMove = new ArrayList<File>();
		
		for(SysFile item : transferItems)
			if(item.getParent() != target)
				toMove.add(item.getFile());
		if(toMove.isEmpty())
			return;
		
		FileOperations.moveFiles(toMove, target.getFile());
	}
}
