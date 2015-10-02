package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.common.nodes.Leaf;
import com.polydes.dialog.data.DataItem;
import com.polydes.dialog.data.Folder;
import com.polydes.dialog.data.LinkedDataItem;

public abstract class TextStore extends Folder
{
	protected TextStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
	
	public void updateItem(Leaf<DataItem> item)
	{
		if(item instanceof LinkedDataItem && item.isDirty())
		{
			((LinkedDataItem) item).updateContents();
			setDirty();
		}
		else if(item instanceof Folder)
		{
			if(item.isDirty())
				setDirty();
			
			for(Leaf<DataItem> curItem : ((Folder) item).getItems())
			{
				updateItem(curItem);
			}
		}
	}
	
}
