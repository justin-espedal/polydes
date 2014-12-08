package stencyl.ext.polydes.paint.app.pages;

import stencyl.ext.polydes.paint.data.stores.Fonts;

public class FontsPage extends BitmapFontPage
{
	private static FontsPage _instance;
	
	private FontsPage()
	{
		super(Fonts.get());
		setListEditEnabled(true);
		folderModel.setUniqueLeafNames(true);
	}

	public static FontsPage get()
	{
		if (_instance == null)
			_instance = new FontsPage();

		return _instance;
	}

	public static void dispose()
	{
		_instance = null;
	}
}