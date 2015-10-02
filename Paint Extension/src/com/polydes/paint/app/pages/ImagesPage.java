package com.polydes.paint.app.pages;

import com.polydes.paint.data.stores.Images;

public class ImagesPage extends ImageSourcePage
{
	private static ImagesPage _instance;
	
	protected ImagesPage()
	{
		super(Images.get());
		
		setListEditEnabled(true);
		folderModel.setUniqueLeafNames(true);
	}
	
	public static ImagesPage get()
	{
		if (_instance == null)
			_instance = new ImagesPage();

		return _instance;
	}

	public static void dispose()
	{
		_instance = null;
	}
}