package com.polydes.paint.data.stores;

import java.io.File;

import com.polydes.common.nodes.Leaf;
import com.polydes.paint.data.BitmapFont;
import com.polydes.paint.data.DataItem;
import com.polydes.paint.data.Folder;
import com.polydes.paint.data.LinkedDataItem;

public abstract class FontStore extends Folder
{
	protected FontStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
	
	protected void fontsLoad(File directory)
	{
		for(File curFile : directory.listFiles())
		{
			String filename = curFile.getName();
			if(filename.endsWith(".fnt"))
			{
				filename = filename.substring(0, filename.length() - ".fnt".length());
				BitmapFont font = new BitmapFont(filename);
				font.loadFromFile(curFile);
				addItem(font);
			}
		}
		
		setClean();
	}
	
	protected void fontsSave(File directory)
	{
		updateItem(this);
		
		for(Leaf<DataItem> item : getItems())
		{
			System.out.println(item.getName() + ", " + item.isDirty());
			
			if(item.isDirty())
			{
				System.out.println(directory.getAbsolutePath() + File.separator + item.getName() + ".fnt");
				String filename = directory.getAbsolutePath() + File.separator + item.getName() + ".fnt";
				((BitmapFont) item).saveToFile(new File(filename));
			}
		}
		
		setClean();
	}
	
	private void updateItem(Leaf<DataItem> item)
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
