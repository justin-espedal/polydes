package com.polydes.paint.data.stores;

import java.io.File;
import java.io.IOException;

import com.polydes.common.nodes.Leaf;
import com.polydes.paint.data.DataItem;
import com.polydes.paint.data.Folder;
import com.polydes.paint.data.ImageSource;
import com.polydes.paint.data.LinkedDataItem;

import stencyl.sw.util.FileHelper;

public abstract class ImageStore extends Folder
{
	protected ImageStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
	
	protected void imagesLoad(File directory)
	{
		for(File curFile : directory.listFiles())
		{
			String filename = curFile.getName();
			if(!filename.endsWith(".png"))
				continue;
			filename = filename.substring(0, filename.length() - ".png".length());
			ImageSource source = new ImageSource(filename);
			source.loadFromFile(curFile);
			addItem(source);
		}
		
		setClean();
	}
	
	protected void imagesSave(File directory)
	{
		updateItem(this);
		
		for(Leaf<DataItem> item : getItems())
		{
			System.out.println(item.getName());
			
			if(item.isDirty())
			{
				System.out.println(directory.getAbsolutePath() + File.separator + item.getName() + ".png");
				String filename = directory.getAbsolutePath() + File.separator + item.getName() + ".png";
				try
				{
					FileHelper.writeToPNG(filename, ((ImageSource) item).getImage());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
