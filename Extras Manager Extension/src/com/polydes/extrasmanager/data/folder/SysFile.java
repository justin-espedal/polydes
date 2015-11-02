package com.polydes.extrasmanager.data.folder;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;
import com.polydes.extrasmanager.app.list.FileListRenderer;

public class SysFile implements Leaf<SysFile,SysFolder>
{
	protected File file;
	protected String path;
	protected int hash;
	
	protected ArrayList<LeafListener<SysFile,SysFolder>> listeners;
	protected SysFolder parent;
	
	protected String name;
	
	public SysFile(File file)
	{
		this.file = file;
		path = file.getAbsolutePath().intern();
		hash = path.hashCode();
		
		listeners = new ArrayList<LeafListener<SysFile,SysFolder>>();
		parent = null;
	}
	
	@Override
	public void setParent(SysFolder parent, boolean addToParent)
	{
		if(this.parent == parent)
			return;
		
		if(parent == null && !addToParent)
		{
			this.parent = null;
			return;
		}
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
	}
	
	public File getFile()
	{
		return file;
	}

	@Override
	public SysFolder getParent()
	{
		return parent;
	}

	@Override
	public void addListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.add(l);
	}

	@Override
	public void removeListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.remove(l);
	}

	@Override
	public void setName(String newName)
	{
		if(name != newName)
		{
			String oldName = name;
			
			if(file.renameTo(new File(file.getParentFile(), newName)))
			{
				name = newName;
				for(LeafListener<SysFile,SysFolder> l : listeners) {l.leafNameChanged(this, oldName);}
				
				if(parent != null)
					parent.registerNameChange(oldName, name);
			}
		}
	}
	
	@Override
	public String getName()
	{
		return file.getName();
	}

	@Override
	public boolean canEditName()
	{
		return true;
	}

	@Override
	public ImageIcon getIcon()
	{
		return FileListRenderer.fetchMiniIcon(this);
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof SysFile)
			return path == ((SysFile) obj).path;
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
}