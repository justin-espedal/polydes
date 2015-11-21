package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.dialog.data.LinkedDataItem;

public abstract class TextStore extends DefaultViewableBranch
{
	protected TextStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
	
	public void updateItem(DefaultLeaf item)
	{
		if(item instanceof LinkedDataItem && item.isDirty())
		{
			((LinkedDataItem) item).updateContents();
			setDirty(true);
		}
		else if(item instanceof DefaultBranch)
		{
			if(item.isDirty())
				setDirty(true);
			
			for(DefaultLeaf curItem : ((DefaultBranch) item).getItems())
			{
				updateItem(curItem);
			}
		}
	}
	
}
