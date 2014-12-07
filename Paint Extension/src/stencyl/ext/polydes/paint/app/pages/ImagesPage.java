package stencyl.ext.polydes.paint.app.pages;

import stencyl.ext.polydes.paint.data.stores.Images;

@SuppressWarnings("serial")
public class ImagesPage extends ImageSourcePage
{
	private static ImagesPage _instance;
	
	protected ImagesPage()
	{
		super(Images.get());
		
		setListEditEnabled(true);
		folderModel.setUniqueItemNames(true);
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