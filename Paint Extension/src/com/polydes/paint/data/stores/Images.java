package com.polydes.paint.data.stores;

import java.io.File;

public class Images extends ImageStore
{
	private static Images _instance;
	
	private Images()
	{
		super("Images");
	}

	public static Images get()
	{
		if(_instance == null)
			_instance = new Images();
		
		return _instance;
	}
	
	@Override
	public void load(File file)
	{
		super.imagesLoad(file);
	}

	@Override
	public void saveChanges(File file)
	{
		super.imagesSave(file);
	}
}
