package stencyl.ext.polydes.extrasmanager.app.list;

import java.io.File;
import java.util.HashSet;

import javax.swing.DefaultListModel;

import stencyl.ext.polydes.extrasmanager.app.utils.ExtrasUtil;
import stencyl.ext.polydes.extrasmanager.data.ExtrasDirectory;

@SuppressWarnings("serial")
public class FileListModel extends DefaultListModel 
{
	public File currView;
	public FileListModelListener listener;
	
	public FileListModel(File path)
	{
		refresh(path);
	}
	
	public void setListener(FileListModelListener listener)
	{
		this.listener = listener;
		if(listener != null && currView != null)
			listener.viewUpdated(this, currView);
	}
	
	public void refresh(File path)
	{
		clear();
		
		currView = path;
		
		if(listener != null)
			listener.viewUpdated(this, path);
		
		if(path == null)
			return;
		
		HashSet<String> toExclude = path.equals(ExtrasDirectory.extrasFolderF) ? ExtrasDirectory.ownedFolderNames : null;
		
		for(File f : ExtrasUtil.orderFiles(path.listFiles(), toExclude))
		{
			addElement(f);
		}
	}
	
	public void refresh()
	{
		refresh(currView);
	}
}
