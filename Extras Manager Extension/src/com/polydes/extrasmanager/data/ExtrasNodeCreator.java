package com.polydes.extrasmanager.data;

import static com.polydes.common.util.Lang.arraylist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeCreator;
import com.polydes.common.sys.FileMonitor;
import com.polydes.common.sys.FileRenderer;
import com.polydes.common.sys.SysFile;
import com.polydes.common.sys.SysFileOperations;
import com.polydes.common.sys.SysFolder;

import stencyl.sw.util.FileHelper;

public class ExtrasNodeCreator implements NodeCreator<SysFile,SysFolder>
{
	HierarchyModel<SysFile,SysFolder> model;
	
	public ExtrasNodeCreator(HierarchyModel<SysFile,SysFolder> model)
	{
		this.model = model;
	}
	
	@Override
	public ArrayList<CreatableNodeInfo> getCreatableNodeList(SysFolder creationBranch)
	{
		ArrayList<CreatableNodeInfo> list = new ArrayList<>();
		for(File f : com.polydes.extrasmanager.io.FileOperations.getTemplates())
		{
			if(f.getName().equals("Thumbs.db"))
				continue;
			list.add(new CreatableNodeInfo(f.getName(), f, FileRenderer.fetchMiniIcon(f)));
		}
		return list;
	}

	@Override
	public SysFile createNode(CreatableNodeInfo selected, String nodeName)
	{
		SysFolder sfolder = model.getCreationParentFolder(model.getSelection());
		File folder = sfolder.getFile();
		
		if(selected.name.equals("Folder"))
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
	
	ArrayList<NodeAction<SysFile>> actions = arraylist(
		new NodeAction<SysFile>("Rename", null, (file) -> FileEditor.rename(file.getFile())),
		new NodeAction<SysFile>("Edit", null, (file) -> FileEditor.edit(file.getFile())),
		new NodeAction<SysFile>("Delete", null, (file) -> {
			FileHelper.delete(file.getFile());
			FileMonitor.refresh();
		})
	);
	
	@Override
	public ArrayList<NodeAction<SysFile>> getNodeActions(SysFile[] targets)
	{
		return actions;
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
