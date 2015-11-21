package com.polydes.paint.data.stores;

import java.io.File;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.paint.data.BitmapFont;
import com.polydes.paint.data.LinkedDataItem;

public abstract class FontStore extends DefaultViewableBranch
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
		
		setDirty(false);
	}
	
	protected void fontsSave(File directory)
	{
		updateItem(this);
		
		for(DefaultLeaf item : getItems())
		{
			System.out.println(item.getName() + ", " + item.isDirty());
			
			if(item.isDirty())
			{
				System.out.println(directory.getAbsolutePath() + File.separator + item.getName() + ".fnt");
				String filename = directory.getAbsolutePath() + File.separator + item.getName() + ".fnt";
				((BitmapFont) item).saveToFile(new File(filename));
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
