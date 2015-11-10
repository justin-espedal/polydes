package com.polydes.extrasmanager.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeCreator;
import com.polydes.common.sys.FileMonitor;
import com.polydes.common.sys.FileRenderer;
import com.polydes.common.sys.SysFile;
import com.polydes.common.sys.SysFileOperations;
import com.polydes.common.sys.SysFolder;
import com.polydes.common.util.PopupUtil.PopupItem;

public class ExtrasNodeCreator implements NodeCreator<SysFile,SysFolder>
{
	HierarchyModel<SysFile,SysFolder> model;
	
	public ExtrasNodeCreator(HierarchyModel<SysFile,SysFolder> model)
	{
		this.model = model;
	}
	
	@Override
	public Collection<PopupItem> getCreatableNodeList()
	{
		ArrayList<PopupItem> list = new ArrayList<>();
		for(File f : com.polydes.extrasmanager.io.FileOperations.getTemplates())
		{
			if(f.getName().equals("Thumbs.db"))
				continue;
			list.add(new PopupItem(f.getName(), f, FileRenderer.fetchMiniIcon(f)));
		}
		return list;
	}

	@Override
	public SysFile createNode(PopupItem selected, String nodeName)
	{
		SysFolder sfolder = model.getCreationParentFolder(model.getSelection());
		File folder = sfolder.getFile();
		
		if(selected.text.equals("Folder"))
		{
			String name = SysFileOperations.getUnusedName("New Folder", folder);
			File f = new File(folder, name);
			f.mkdir();
		}
		else
		{
			nodeName = nodeName.substring(0, nodeName.indexOf(" ", nodeName.lastIndexOf('.')));
			File template = (File) selected.data;
			
			String ext = SysFileOperations.getNameParts(template.getName())[1];
			if(!nodeName.endsWith(ext))
				nodeName += ext;
			
			nodeName = SysFileOperations.getUnusedName(nodeName, folder);
			try
			{
				FileUtils.copyFile(template, new File(folder, nodeName));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		FileMonitor.refresh();
		
		return null;
	}

	@Override
	public void editNode(SysFile dataItem)
	{
		
	}

	@Override
	public void nodeRemoved(SysFile toRemove)
	{
		
	}

	@Override
	public boolean attemptRemove(List<SysFile> toRemove)
	{
		return true;
	}
}
