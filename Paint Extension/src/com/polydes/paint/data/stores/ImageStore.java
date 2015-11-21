package com.polydes.paint.data.stores;

import java.io.File;
import java.io.IOException;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.paint.data.ImageSource;
import com.polydes.paint.data.LinkedDataItem;

import stencyl.sw.util.FileHelper;

public abstract class ImageStore extends DefaultViewableBranch
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
		
		setDirty(false);
	}
	
	protected void imagesSave(File directory)
	{
		updateItem(this);
		
		for(DefaultLeaf item : getItems())
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
		
		setDirty(false);
	}
	
	private void updateItem(DefaultLeaf item)
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
