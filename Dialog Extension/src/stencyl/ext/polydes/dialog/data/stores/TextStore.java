package stencyl.ext.polydes.dialog.data.stores;

import java.io.File;

import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.dialog.data.DataItem;
import stencyl.ext.polydes.dialog.data.Folder;
import stencyl.ext.polydes.dialog.data.LinkedDataItem;

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
